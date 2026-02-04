// code by jph
package ch.alpine.tensor.red;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.function.BiFunction;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.num.Pi;

class InnerTest {
  @Test
  void testSimple() {
    BiFunction<Scalar, Tensor, Scalar> ex = (_, _) -> Pi.VALUE;
    Tensor result = Inner.with(ex).apply(Tensors.vector(2, 3), Tensors.vector(2, 3));
    assertEquals(result, Tensors.vector(Math.PI, Math.PI));
  }
}
