// code by jph
package ch.alpine.tensor.fft;

import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.RepetitionInfo;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import ch.alpine.tensor.ComplexScalar;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.io.ResourceData;
import ch.alpine.tensor.mat.IdentityMatrix;
import ch.alpine.tensor.mat.Tolerance;
import ch.alpine.tensor.mat.re.Inverse;

class FourierDSTTest {
  @Test
  void test1() {
    Tensor vector = Tensors.vector(1, 2, 3);
    Tensor r1 = FourierDST._1.of(vector);
    Tensor r2 = FourierDST._1.matrix(vector.length()).dot(vector);
    Tolerance.CHOP.requireClose(r1, r2);
  }

  @Test
  void test1Complex() {
    Tensor vector = Tensors.of(ComplexScalar.of(1, 2), ComplexScalar.of(0, 0.3), RealScalar.ONE);
    Tensor r1 = FourierDST._1.of(vector);
    Tensor r2 = FourierDST._1.matrix(vector.length()).dot(vector);
    // System.out.println(r2);
    // Tolerance.CHOP.requireClose(r1, r2);
  }

  @RepeatedTest(8)
  void test1(RepetitionInfo repetitionInfo) {
    int n = repetitionInfo.getCurrentRepetition();
    Tensor matrix = FourierDST._1.matrix(n);
    Tolerance.CHOP.requireClose(matrix, Inverse.of(matrix));
  }

  @RepeatedTest(8)
  void test23(RepetitionInfo repetitionInfo) {
    int n = repetitionInfo.getCurrentRepetition();
    Tensor matrix2 = FourierDST._2.matrix(n);
    Tensor matrix3 = FourierDST._3.matrix(n);
    Tensor result = matrix2.dot(matrix3);
    Tolerance.CHOP.requireClose(result, IdentityMatrix.of(n));
  }

  @RepeatedTest(8)
  void test4(RepetitionInfo repetitionInfo) {
    int n = repetitionInfo.getCurrentRepetition();
    Tensor matrix = FourierDST._4.matrix(n);
    Tolerance.CHOP.requireClose(matrix, Inverse.of(matrix));
  }

  @Test
  void testSpecific() {
    Scalar scalar = FourierDST._2.matrix(7).Get(5, 6);
    Tolerance.CHOP.requireClose(scalar, RealScalar.of(-0.3779644730092272));
  }

  @ParameterizedTest
  @EnumSource
  void testFromResource(FourierDST fourierDSTMatrix) {
    Tensor expect = ResourceData.of("/ch/alpine/tensor/fft/dstmatrix" + fourierDSTMatrix + ".csv");
    Tensor actual = fourierDSTMatrix.matrix(5);
    Tolerance.CHOP.requireClose(expect, actual);
  }

  @ParameterizedTest
  @EnumSource
  void testFail(FourierDST fourierDSTMatrix) {
    assertThrows(Exception.class, () -> fourierDSTMatrix.matrix(0));
  }
}
