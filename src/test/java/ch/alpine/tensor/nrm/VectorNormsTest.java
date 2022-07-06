// code by jph
package ch.alpine.tensor.nrm;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.NoSuchElementException;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.Throw;
import ch.alpine.tensor.api.TensorScalarFunction;
import ch.alpine.tensor.api.TensorUnaryOperator;

class VectorNormsTest {
  public static final TensorScalarFunction[] VALUES = { //
      Vector1Norm::of, //
      Vector2Norm::of, //
      VectorInfinityNorm::of, //
  };

  @Test
  void testEmptyFail() {
    for (TensorScalarFunction norm : VALUES) {
      assertThrows(NoSuchElementException.class, () -> norm.apply(Tensors.empty()));
      assertThrows(Throw.class, () -> norm.apply(RealScalar.ONE));
      assertThrows(ClassCastException.class, () -> norm.apply(Tensors.fromString("{{1, 2}, {3}}")));
    }
  }

  @Test
  void testOk1() {
    Tensor v = Tensors.vector(0, 0, 0, 0);
    for (TensorScalarFunction norm : VALUES)
      assertEquals(v, NormalizeUnlessZero.with(norm).apply(v));
  }

  @Test
  void testEmpty() {
    for (TensorScalarFunction norm : VALUES) {
      TensorUnaryOperator tensorUnaryOperator = NormalizeUnlessZero.with(norm);
      Tensor tensor = Tensors.empty();
      assertThrows(NoSuchElementException.class, () -> tensorUnaryOperator.apply(tensor));
    }
  }
}
