// code by jph
package ch.alpine.tensor.mat.ex;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.BitSet;

import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.RepetitionInfo;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import ch.alpine.tensor.RationalScalar;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.TensorRuntimeException;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.alg.Array;
import ch.alpine.tensor.alg.Dot;
import ch.alpine.tensor.fft.FourierMatrix;
import ch.alpine.tensor.lie.LeviCivitaTensor;
import ch.alpine.tensor.lie.Symmetrize;
import ch.alpine.tensor.mat.DiagonalMatrix;
import ch.alpine.tensor.mat.HilbertMatrix;
import ch.alpine.tensor.mat.IdentityMatrix;
import ch.alpine.tensor.mat.MatrixQ;
import ch.alpine.tensor.mat.SymmetricMatrixQ;
import ch.alpine.tensor.mat.Tolerance;
import ch.alpine.tensor.mat.re.Inverse;
import ch.alpine.tensor.num.GaussScalar;
import ch.alpine.tensor.pdf.Distribution;
import ch.alpine.tensor.pdf.RandomVariate;
import ch.alpine.tensor.pdf.c.NormalDistribution;
import ch.alpine.tensor.pdf.c.UniformDistribution;
import ch.alpine.tensor.pdf.d.DiscreteUniformDistribution;
import ch.alpine.tensor.sca.Imag;
import ch.alpine.tensor.sca.Real;

class MatrixPowerTest {
  private static boolean trunc(Tensor m, Tensor r) {
    return Tolerance.CHOP.of(m.subtract(r)).equals(Array.zeros(m.length(), m.length()));
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

  @Test
  void testHilbert() {
    checkLow(HilbertMatrix.of(4));
  }

  @Test
  void testFourier() {
    checkLow(FourierMatrix.of(3));
    checkLow(FourierMatrix.of(6));
  }

  @Test
  void testMathematicaEx() {
    assertEquals( //
        MatrixPower.of(Tensors.fromString("{{1, 1}, {1, 2}}"), 10), //
        Tensors.fromString("{{4181, 6765}, {6765, 10946}}") //
    );
  }

  @Test
  void testMathematicaInv2() {
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

  @Test
  void testSet() {
    assertEquals(powerOf(3, 5), 243);
    assertEquals(powerOf(2, 21), 2097152);
    assertEquals(powerOf(5, 6), 15625);
    assertEquals(powerOf(5, 0), 1);
  }

  @Test
  void testIdentityMatrix() {
    for (int n = 1; n < 6; ++n) {
      Tensor matrix = IdentityMatrix.of(n);
      Tensor sqrt = MatrixPower.ofSymmetric(matrix, RationalScalar.HALF);
      Tolerance.CHOP.requireClose(sqrt.dot(sqrt), matrix);
    }
  }

  @Test
  void testNegativeDiagonal() {
    Tensor matrix = DiagonalMatrix.of(-1, -2, -3);
    Tensor sqrt = MatrixPower.ofSymmetric(matrix, RationalScalar.HALF);
    Tolerance.CHOP.requireClose(sqrt.dot(sqrt), matrix);
  }

  @RepeatedTest(9)
  void testSymmetric(RepetitionInfo repetitionInfo) {
    int n = repetitionInfo.getCurrentRepetition();
    Distribution distribution = NormalDistribution.standard();
    Tensor matrix = Symmetrize.of(RandomVariate.of(distribution, n, n));
    {
      Tensor sqrt = MatrixPower.ofSymmetric(matrix, RationalScalar.HALF);
      SymmetricMatrixQ.require(sqrt);
      Tolerance.CHOP.requireClose(sqrt.dot(sqrt), matrix);
    }
    {
      Tensor sqrt = MatrixPower.ofSymmetric(matrix, RationalScalar.of(1, 3));
      SymmetricMatrixQ.require(sqrt);
      Tolerance.CHOP.requireClose(sqrt.dot(sqrt).dot(sqrt), matrix);
    }
    {
      Tensor sqrt = MatrixPower.ofSymmetric(matrix, RationalScalar.of(1, 4));
      SymmetricMatrixQ.require(sqrt);
      Tolerance.CHOP.requireClose(sqrt.dot(sqrt).dot(sqrt).dot(sqrt), matrix);
    }
  }

  @Test
  void testComplexDiagnoal() {
    Tensor tensor = MatrixPower.ofSymmetric(DiagonalMatrix.of(-1, 4), RationalScalar.HALF);
    Tolerance.CHOP.requireClose(tensor, Tensors.fromString("{{I, 0}, {0, 2}}"));
  }

  @Test
  void testComplex() {
    Tensor tensor = MatrixPower.ofSymmetric(Tensors.fromString("{{3, 4}, {4, -5.}}"), RealScalar.of(0.345));
    Tensor re = Tensors.fromString("{{1.58297621781119750, +0.28292717088123903}, {+0.2829271708812389, 1.0171218760487195}}");
    Tensor im = Tensors.fromString("{{0.24891109223875751, -0.60092453470790870}, {-0.6009245347079087, 1.4507601616545749}}");
    Tolerance.CHOP.requireClose(Real.of(tensor), re);
    Tolerance.CHOP.requireClose(Imag.of(tensor), im);
  }

  @ParameterizedTest
  @ValueSource(ints = { 3, 4, 5 })
  void testGaussian(int n) {
    int prime = 7879;
    Distribution distribution = DiscreteUniformDistribution.of(0, prime);
    Scalar one = GaussScalar.of(1, prime);
    Tensor matrix = RandomVariate.of(distribution, n, n).map(s -> GaussScalar.of(s.number().intValue(), prime));
    Tensor result = MatrixPower.of(matrix, +343386231231234L);
    Tensor revers = MatrixPower.of(matrix, -343386231231234L);
    MatrixQ.requireSize(result, n, n);
    assertEquals(DiagonalMatrix.of(n, one), Dot.of(result, revers));
  }

  @Test
  void testHermitian() {
    Tensor matrix = Tensors.fromString("{{0, I}, {-I, 0}}");
    MatrixPower.ofHermitian(matrix, RealScalar.of(2.3));
  }

  @Test
  void testNonSymmetricFail() {
    assertThrows(TensorRuntimeException.class, () -> MatrixPower.ofSymmetric(RandomVariate.of(UniformDistribution.of(-2, 2), 4, 4), RationalScalar.HALF));
  }

  @Test
  void testNullFail() {
    assertThrows(NullPointerException.class, () -> MatrixPower.ofSymmetric(null, RationalScalar.HALF));
  }

  @Test
  void testFailZero() {
    Tensor matrix = Array.zeros(2, 3);
    assertThrows(IllegalArgumentException.class, () -> MatrixPower.of(matrix, -1));
    assertThrows(IllegalArgumentException.class, () -> MatrixPower.of(matrix, 0));
    assertThrows(IllegalArgumentException.class, () -> MatrixPower.of(matrix, 1));
  }

  @Test
  void testFailOne() {
    Tensor matrix = HilbertMatrix.of(3, 2);
    assertThrows(IllegalArgumentException.class, () -> MatrixPower.of(matrix, -1));
    assertThrows(IllegalArgumentException.class, () -> MatrixPower.of(matrix, 0));
    assertThrows(IllegalArgumentException.class, () -> MatrixPower.of(matrix, 1));
  }

  @Test
  void testFailAd() {
    assertThrows(ClassCastException.class, () -> MatrixPower.of(LeviCivitaTensor.of(3), 1));
  }
}
