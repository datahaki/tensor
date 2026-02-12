// code by jph
package ch.alpine.tensor.mat;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Arrays;

import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.RepetitionInfo;
import org.junit.jupiter.api.Test;

import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.alg.Dimensions;
import ch.alpine.tensor.alg.Dot;
import ch.alpine.tensor.pdf.ComplexNormalDistribution;
import ch.alpine.tensor.pdf.RandomVariate;

class MatrixDotConjugateTransposeTest {
  @Test
  void testDimensions() {
    Tensor tensor = MatrixDotConjugateTranspose.self(HilbertMatrix.of(2, 3));
    assertEquals(Dimensions.of(tensor), Arrays.asList(2, 2));
  }

  @Test
  void testDotIdentity() {
    Tensor tensor = RandomVariate.of(ComplexNormalDistribution.STANDARD, 2, 4);
    Tensor result = MatrixDotConjugateTranspose.self(tensor);
    Tensor expect = Dot.of(tensor, ConjugateTranspose.of(tensor));
    Tolerance.CHOP.requireClose(result, expect);
    assertEquals(result, expect);
  }

  @RepeatedTest(8)
  void testComplexSquare(RepetitionInfo repetitionInfo) {
    int n = repetitionInfo.getCurrentRepetition();
    Tensor matrix = RandomVariate.of(ComplexNormalDistribution.STANDARD, n, n);
    Tensor polard = MatrixDotConjugateTranspose.self(matrix);
    HermitianMatrixQ.INSTANCE.require(polard);
  }

  @RepeatedTest(8)
  void testComplex1(RepetitionInfo repetitionInfo) {
    int n = repetitionInfo.getCurrentRepetition();
    Tensor matrix = RandomVariate.of(ComplexNormalDistribution.STANDARD, n, n + 2);
    Tensor polard = MatrixDotConjugateTranspose.self(matrix);
    HermitianMatrixQ.INSTANCE.require(polard);
  }

  @RepeatedTest(8)
  void testComplex2(RepetitionInfo repetitionInfo) {
    int n = repetitionInfo.getCurrentRepetition();
    Tensor matrix = RandomVariate.of(ComplexNormalDistribution.STANDARD, n + 2, n);
    Tensor polard = MatrixDotConjugateTranspose.self(matrix);
    HermitianMatrixQ.INSTANCE.require(polard);
  }
}
