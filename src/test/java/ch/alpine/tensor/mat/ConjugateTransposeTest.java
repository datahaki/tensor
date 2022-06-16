// code by jph
package ch.alpine.tensor.mat;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Arrays;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.TensorRuntimeException;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.alg.Dimensions;
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

  @Test
  void testScalarFail() {
    assertThrows(TensorRuntimeException.class, () -> ConjugateTranspose.of(RealScalar.ONE));
  }

  @Test
  void testVectorFail() {
    assertThrows(TensorRuntimeException.class, () -> ConjugateTranspose.of(Tensors.vector(1, 2, 3)));
  }
}
