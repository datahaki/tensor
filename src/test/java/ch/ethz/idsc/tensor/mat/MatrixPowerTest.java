// code by jph
package ch.ethz.idsc.tensor.mat;

import java.util.BitSet;

import ch.ethz.idsc.tensor.RationalScalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Array;
import ch.ethz.idsc.tensor.lie.LieAlgebras;
import ch.ethz.idsc.tensor.lie.Symmetrize;
import ch.ethz.idsc.tensor.pdf.Distribution;
import ch.ethz.idsc.tensor.pdf.NormalDistribution;
import ch.ethz.idsc.tensor.pdf.RandomVariate;
import ch.ethz.idsc.tensor.pdf.UniformDistribution;
import ch.ethz.idsc.tensor.sca.Chop;
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

  public void testNonSymmetricFail() {
    try {
      MatrixPower.ofSymmetric(RandomVariate.of(UniformDistribution.of(-2, 2), 4, 4), RationalScalar.HALF);
      fail();
    } catch (Exception exception) {
      // ---
    }
  }

  public void testNullFail() {
    try {
      MatrixPower.ofSymmetric(null, RationalScalar.HALF);
      fail();
    } catch (Exception exception) {
      // ---
    }
  }

  public void testFailZero() {
    Tensor matrix = Array.zeros(2, 3);
    try {
      MatrixPower.of(matrix, 0);
      fail();
    } catch (Exception exception) {
      // ---
    }
  }

  public void testFailOne() {
    Tensor matrix = HilbertMatrix.of(3, 2);
    try {
      MatrixPower.of(matrix, 1);
      fail();
    } catch (Exception exception) {
      // ---
    }
  }

  public void testFailAd() {
    Tensor tensor = LieAlgebras.he1();
    try {
      MatrixPower.of(tensor, 1);
      fail();
    } catch (Exception exception) {
      // ---
    }
  }
}
