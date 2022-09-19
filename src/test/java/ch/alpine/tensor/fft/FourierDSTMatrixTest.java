// code by jph
package ch.alpine.tensor.fft;

import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.RepetitionInfo;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.io.ResourceData;
import ch.alpine.tensor.mat.IdentityMatrix;
import ch.alpine.tensor.mat.Tolerance;
import ch.alpine.tensor.mat.re.Inverse;

class FourierDSTMatrixTest {
  @RepeatedTest(8)
  void test1(RepetitionInfo repetitionInfo) {
    int n = repetitionInfo.getCurrentRepetition();
    Tensor matrix = FourierDSTMatrix._1.of(n);
    Tolerance.CHOP.requireClose(matrix, Inverse.of(matrix));
  }

  @RepeatedTest(8)
  void test23(RepetitionInfo repetitionInfo) {
    int n = repetitionInfo.getCurrentRepetition();
    Tensor matrix2 = FourierDSTMatrix._2.of(n);
    Tensor matrix3 = FourierDSTMatrix._3.of(n);
    Tensor result = matrix2.dot(matrix3);
    Tolerance.CHOP.requireClose(result, IdentityMatrix.of(n));
  }

  @RepeatedTest(8)
  void test4(RepetitionInfo repetitionInfo) {
    int n = repetitionInfo.getCurrentRepetition();
    Tensor matrix = FourierDSTMatrix._4.of(n);
    Tolerance.CHOP.requireClose(matrix, Inverse.of(matrix));
  }

  @Test
  void testSpecific() {
    Scalar scalar = FourierDSTMatrix._2.of(7).Get(5, 6);
    Tolerance.CHOP.requireClose(scalar, RealScalar.of(-0.3779644730092272));
  }

  @ParameterizedTest
  @EnumSource
  void testFromResource(FourierDSTMatrix fourierDSTMatrix) {
    Tensor expect = ResourceData.of("/ch/alpine/tensor/fft/dstmatrix" + fourierDSTMatrix + ".csv");
    Tensor actual = fourierDSTMatrix.of(5);
    Tolerance.CHOP.requireClose(expect, actual);
  }

  @ParameterizedTest
  @EnumSource
  void testFail(FourierDSTMatrix fourierDSTMatrix) {
    assertThrows(Exception.class, () -> fourierDSTMatrix.of(0));
  }
}
