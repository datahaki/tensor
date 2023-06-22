// code by jph
package ch.alpine.tensor.fft;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Random;
import java.util.random.RandomGenerator;

import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import ch.alpine.tensor.ComplexScalar;
import ch.alpine.tensor.RationalScalar;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.Throw;
import ch.alpine.tensor.alg.Subdivide;
import ch.alpine.tensor.mat.ConjugateTranspose;
import ch.alpine.tensor.mat.HilbertMatrix;
import ch.alpine.tensor.mat.IdentityMatrix;
import ch.alpine.tensor.mat.SymmetricMatrixQ;
import ch.alpine.tensor.mat.Tolerance;
import ch.alpine.tensor.mat.VandermondeMatrix;
import ch.alpine.tensor.mat.re.Inverse;
import ch.alpine.tensor.nrm.FrobeniusNorm;
import ch.alpine.tensor.nrm.Matrix1Norm;
import ch.alpine.tensor.nrm.MatrixInfinityNorm;
import ch.alpine.tensor.num.Pi;
import ch.alpine.tensor.pdf.ComplexNormalDistribution;
import ch.alpine.tensor.pdf.RandomVariate;
import ch.alpine.tensor.sca.exp.Exp;
import ch.alpine.tensor.sca.pow.Sqrt;

class FourierTest {
  @Test
  void test2() {
    Tensor vector = Tensors.fromString("{1 + 2*I, 3 + 11*I}");
    Tensor expect = Tensors.fromString("{2.828427124746190 + 9.19238815542512*I, -1.414213562373095 - 6.36396103067893*I}");
    Tolerance.CHOP.requireClose(Fourier.FORWARD.transform(vector), expect);
    Tolerance.CHOP.requireClose(Fourier.INVERSE.transform(Fourier.FORWARD.transform(vector)), vector);
  }

  @Test
  void test2Quantity() {
    Tensor vector = Tensors.fromString("{1 + 2*I[m], 3 + 11*I[m]}");
    Tensor expect = Tensors.fromString("{2.828427124746190 + 9.19238815542512*I[m], -1.414213562373095 - 6.36396103067893*I[m]}");
    Tolerance.CHOP.requireClose(Fourier.FORWARD.transform(vector), expect);
    Tolerance.CHOP.requireClose(Fourier.INVERSE.transform(Fourier.FORWARD.transform(vector)), vector);
  }

  @Test
  void test4() {
    Tensor vector = Tensors.vector(1, 2, 0, 0);
    Tensor tensor = Fourier.FORWARD.transform(vector);
    Tensor expect = Tensors.fromString("{1.5, 0.5 + I, -0.5, 0.5 - I}");
    Tolerance.CHOP.requireClose(Fourier.FORWARD.matrix(vector.length()).dot(vector), expect);
    Tolerance.CHOP.requireClose(vector.dot(Fourier.FORWARD.matrix(vector.length())), expect);
    Tolerance.CHOP.requireClose(tensor, expect);
    Tensor backed = Fourier.FORWARD.transform(expect);
    Tolerance.CHOP.requireClose(backed, Tensors.vector(1, 0, 0, 2));
  }

  @RepeatedTest(10)
  void testRandom() {
    for (int n = 0; n < 7; ++n) {
      Tensor vector = RandomVariate.of(ComplexNormalDistribution.STANDARD, 1 << n);
      Tensor result = Fourier.FORWARD.transform(vector);
      Tensor dotmat = vector.dot(Fourier.FORWARD.matrix(vector.length()));
      Tolerance.CHOP.requireClose(dotmat, result);
      Tolerance.CHOP.requireClose(Fourier.INVERSE.transform(result), vector);
    }
  }

  @ParameterizedTest
  @EnumSource
  void testFailScalar(Fourier fourier) {
    assertThrows(Exception.class, () -> fourier.transform(RealScalar.ONE));
    assertThrows(Exception.class, () -> fourier.transform(Tensors.empty()));
    assertThrows(Throw.class, () -> fourier.transform(Tensors.vector(1, 2, 0)));
    assertThrows(ClassCastException.class, () -> fourier.transform(HilbertMatrix.of(4)));
  }

  public void checkFormat(int n) {
    Tensor original = Fourier.FORWARD.matrix(n);
    SymmetricMatrixQ.require(original);
    Tensor matrix = original.map(Tolerance.CHOP);
    SymmetricMatrixQ.require(matrix);
    Tensor invert = ConjugateTranspose.of(matrix);
    SymmetricMatrixQ.require(matrix);
    Tolerance.CHOP.requireClose(matrix.dot(invert), IdentityMatrix.of(n));
    Tolerance.CHOP.requireClose(Inverse.of(matrix), invert);
  }

  @Test
  void testSeveral() {
    RandomGenerator randomGenerator = new Random();
    int n = 1 + randomGenerator.nextInt(20);
    checkFormat(n);
  }

  @Test
  void testNorm4() {
    Tensor m = Fourier.FORWARD.matrix(4);
    assertEquals(Matrix1Norm.of(m), RealScalar.of(2));
    assertEquals(Matrix1Norm.of(m), MatrixInfinityNorm.of(m));
    assertEquals(Matrix1Norm.of(m), FrobeniusNorm.of(m));
    // Norm._2.of m == 1 is confirmed with Mathematica
  }

  @Test
  void testVandermonde() {
    for (int n = 1; n < 8; ++n) {
      Tensor vector = Subdivide.of(RealScalar.ZERO, Pi.TWO, n).multiply(ComplexScalar.I).map(Exp.FUNCTION).extract(0, n);
      Scalar scalar = Sqrt.FUNCTION.apply(RationalScalar.of(1, n));
      Tensor matrix = VandermondeMatrix.of(vector).multiply(scalar);
      Tolerance.CHOP.requireClose(Fourier.FORWARD.matrix(n), matrix);
    }
  }

  private static void _check(int n) {
    Tensor matrix = Fourier.FORWARD.matrix(n);
    Tensor inverse = Fourier.INVERSE.matrix(n);
    Tolerance.CHOP.requireClose(matrix.dot(inverse), IdentityMatrix.of(n));
  }

  @Test
  void testInverse() {
    _check(8);
    _check(10);
    _check(11);
  }

  @ParameterizedTest
  @EnumSource
  void testNegativeFail(Fourier fourier) {
    assertThrows(IllegalArgumentException.class, () -> fourier.matrix(0));
    assertThrows(IllegalArgumentException.class, () -> fourier.matrix(-1));
  }
}
