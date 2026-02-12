// code by jph
package ch.alpine.tensor.mat;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Arrays;

import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.RepetitionInfo;
import org.junit.jupiter.api.Test;

import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.Throw;
import ch.alpine.tensor.alg.Dimensions;
import ch.alpine.tensor.pdf.ComplexNormalDistribution;
import ch.alpine.tensor.pdf.RandomVariate;
import ch.alpine.tensor.pdf.c.UniformDistribution;

class ConjugateTransposeTest {
  @Test
  void testExample1() {
    Tensor m1 = Tensors.fromString("{{1, 5+I}, {2, 3}}");
    Tensor m2 = Tensors.fromString("{{1, 2}, {5-I, 3}}");
    assertEquals(ConjugateTranspose.of(m1), m2);
  }

  @Test
  void testExample2() {
    Tensor m1 = Tensors.fromString("{{1+2*I, 5+I}, {2, 3}}");
    Tensor m2 = Tensors.fromString("{{1-2*I, 2}, {5-I, 3}}");
    assertEquals(ConjugateTranspose.of(m1), m2);
  }

  @Test
  void testRank3() {
    Tensor tensor = ConjugateTranspose.of(RandomVariate.of(UniformDistribution.unit(), 2, 3, 4));
    assertEquals(Dimensions.of(tensor), Arrays.asList(3, 2, 4));
  }

  @RepeatedTest(6)
  void testComplex1(RepetitionInfo repetitionInfo) {
    int n = repetitionInfo.getCurrentRepetition();
    Tensor matrix = RandomVariate.of(ComplexNormalDistribution.STANDARD, n, n + 2);
    Tensor polard = ConjugateTranspose.of(matrix).dot(matrix);
    HermitianMatrixQ.INSTANCE.require(polard);
  }

  @RepeatedTest(6)
  void testComplex2(RepetitionInfo repetitionInfo) {
    int n = repetitionInfo.getCurrentRepetition();
    Tensor matrix = RandomVariate.of(ComplexNormalDistribution.STANDARD, n + 2, n);
    Tensor polard = ConjugateTranspose.of(matrix).dot(matrix);
    HermitianMatrixQ.INSTANCE.require(polard);
  }

  @Test
  void testScalarFail() {
    assertThrows(Throw.class, () -> ConjugateTranspose.of(RealScalar.ONE));
  }

  @Test
  void testVectorFail() {
    assertThrows(Throw.class, () -> ConjugateTranspose.of(Tensors.vector(1, 2, 3)));
  }
}
