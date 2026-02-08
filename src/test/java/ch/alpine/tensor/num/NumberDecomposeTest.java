// code by jph
package ch.alpine.tensor.num;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.api.ScalarTensorFunction;

class NumberDecomposeTest {
  @Test
  void testSimple() {
    ScalarTensorFunction numberDecompose = NumberDecompose.of(Tensors.vector(86400, 3600, 60, 1));
    Tensor result = numberDecompose.apply(RealScalar.of(100_000));
    assertEquals(result, Tensors.vector(1, 3, 46, 40));
    assertTrue(numberDecompose.toString().startsWith("NumberDecompose["));
  }
}
