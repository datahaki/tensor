// code by jph
package ch.ethz.idsc.tensor.lie;

import java.util.BitSet;

import ch.ethz.idsc.tensor.RationalScalar;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Array;
import ch.ethz.idsc.tensor.mat.DiagonalMatrix;
import ch.ethz.idsc.tensor.mat.FourierMatrix;
import ch.ethz.idsc.tensor.mat.HilbertMatrix;
import ch.ethz.idsc.tensor.mat.IdentityMatrix;
import ch.ethz.idsc.tensor.mat.Inverse;
import ch.ethz.idsc.tensor.mat.SymmetricMatrixQ;
import ch.ethz.idsc.tensor.mat.Tolerance;
import ch.ethz.idsc.tensor.pdf.Distribution;
import ch.ethz.idsc.tensor.pdf.NormalDistribution;
import ch.ethz.idsc.tensor.pdf.RandomVariate;
import ch.ethz.idsc.tensor.pdf.UniformDistribution;
import ch.ethz.idsc.tensor.sca.Chop;
import ch.ethz.idsc.tensor.sca.Imag;
import ch.ethz.idsc.tensor.sca.Real;
import ch.ethz.idsc.tensor.usr.AssertFail;
import junit.framework.TestCase;

public class MatrixPowerTest extends TestCase {
  private static boolean trunc(Tensor m, Tensor r) {
    return Chop._12.of(m.subtract(r)).equals(Array.zeros(m.length(), m.length()));
  }

  private static void checkLow(Tensor m) {
    int n = m.length();
    assertEquals(MatrixPower.of(m, 0), IdentityMatrix.of(n));
    assertEquals(MatrixPower.of(m, 1), m);
    assertEquals(MatrixPower.of(m, -1), Inverse.of(m));
    assertEquals(MatrixPower.of(m, 2), m.dot(m));
    Tensor inv = Inverse.of(m);
    assertEquals(MatrixPower.of(m, -2), inv.dot(inv));
    assertEquals(MatrixPower.of(m, 3), m.dot(m).dot(m));
    assertTrue(trunc(MatrixPower.of(m, 3), m.dot(m).dot(m)));
    assertTrue(trunc(MatrixPower.of(m, 4), m.dot(m).dot(m).dot(m)));
    assertTrue(trunc(MatrixPower.of(m, 5), m.dot(m).dot(m).dot(m).dot(m)));
    assertTrue(trunc(MatrixPower.of(m, 6), m.dot(m).dot(m).dot(m).dot(m).dot(m)));
  }

  public void testHilbert() {
    checkLow(HilbertMatrix.of(4));
  }

  public void testFourier() {
    checkLow(FourierMatrix.of(3));
    checkLow(FourierMatrix.of(6));
  }

  public void testMathematicaEx() {
    assertEquals( //
        MatrixPower.of(Tensors.fromString("{{1, 1}, {1, 2}}"), 10), //
        Tensors.fromString("{{4181, 6765}, {6765, 10946}}") //
    );
  }

  public void testMathematicaInv2() {
    assertEquals( //
        MatrixPower.of(Tensors.fromString("{{1, 1}, {1, 2}}"), -2), //
        Tensors.fromString("{{5, -3}, {-3, 2}}") //
    );
  }

  static int log2Long(long n) {
    return 63 - Long.numberOfLeadingZeros(n);
  }

  static long powerOf(int x, long exp) {
    BitSet bitSet = BitSet.valueOf(new long[] { exp });
    long y = 1;
    for (int bitIndex = log2Long(exp); bitIndex >= 0; --bitIndex) {
      y = y * y;
      if (bitSet.get(bitIndex))
        y = y * x;
    }
    return y;
  }

  public void testSet() {
    assertEquals(powerOf(3, 5), 243);
    assertEquals(powerOf(2, 21), 2097152);
    assertEquals(powerOf(5, 6), 15625);
    assertEquals(powerOf(5, 0), 1);
  }

  public void testIdentityMatrix() {
    for (int n = 1; n < 6; ++n) {
      Tensor matrix = IdentityMatrix.of(n);
      Tensor sqrt = MatrixPower.ofSymmetric(matrix, RationalScalar.HALF);
      Chop._08.requireClose(sqrt.dot(sqrt), matrix);
    }
  }

  public void testNegativeDiagonal() {
    Tensor matrix = DiagonalMatrix.of(-1, -2, -3);
    Tensor sqrt = MatrixPower.ofSymmetric(matrix, RationalScalar.HALF);
    Chop._08.requireClose(sqrt.dot(sqrt), matrix);
  }

  public void testSymmetric() {
    for (int n = 2; n < 10; ++n) {
      Distribution distribution = NormalDistribution.standard();
      Tensor matrix = Symmetrize.of(RandomVariate.of(distribution, n, n));
      {
        Tensor sqrt = MatrixPower.ofSymmetric(matrix, RationalScalar.HALF);
        SymmetricMatrixQ.require(sqrt);
        Chop._08.requireClose(sqrt.dot(sqrt), matrix);
      }
      {
        Tensor sqrt = MatrixPower.ofSymmetric(matrix, RationalScalar.of(1, 3));
        SymmetricMatrixQ.require(sqrt);
        Chop._08.requireClose(sqrt.dot(sqrt).dot(sqrt), matrix);
      }
      {
        Tensor sqrt = MatrixPower.ofSymmetric(matrix, RationalScalar.of(1, 4));
        SymmetricMatrixQ.require(sqrt);
        Chop._08.requireClose(sqrt.dot(sqrt).dot(sqrt).dot(sqrt), matrix);
      }
    }
  }

  public void testComplexDiagnoal() {
    Tensor tensor = MatrixPower.ofSymmetric(DiagonalMatrix.of(-1, 4), RationalScalar.HALF);
    Tolerance.CHOP.requireClose(tensor, Tensors.fromString("{{I, 0}, {0, 2}}"));
  }

  public void testComplex() {
    Tensor tensor = MatrixPower.ofSymmetric(Tensors.fromString("{{3, 4}, {4, -5.}}"), RealScalar.of(0.345));
    Tensor re = Tensors.fromString("{{1.58297621781119750, +0.28292717088123903}, {+0.2829271708812389, 1.0171218760487195}}");
    Tensor im = Tensors.fromString("{{0.24891109223875751, -0.60092453470790870}, {-0.6009245347079087, 1.4507601616545749}}");
    Tolerance.CHOP.requireClose(Real.of(tensor), re);
    Tolerance.CHOP.requireClose(Imag.of(tensor), im);
  }

  public void testNonSymmetricFail() {
    AssertFail.of(() -> MatrixPower.ofSymmetric(RandomVariate.of(UniformDistribution.of(-2, 2), 4, 4), RationalScalar.HALF));
  }

  public void testNullFail() {
    AssertFail.of(() -> MatrixPower.ofSymmetric(null, RationalScalar.HALF));
  }

  public void testFailZero() {
    Tensor matrix = Array.zeros(2, 3);
    AssertFail.of(() -> MatrixPower.of(matrix, 0));
  }

  public void testFailOne() {
    Tensor matrix = HilbertMatrix.of(3, 2);
    AssertFail.of(() -> MatrixPower.of(matrix, 1));
  }

  public void testFailAd() {
    AssertFail.of(() -> MatrixPower.of(LeviCivitaTensor.of(3), 1));
  }
}
