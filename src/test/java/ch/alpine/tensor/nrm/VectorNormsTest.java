// code by jph
package ch.alpine.tensor.nrm;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.NoSuchElementException;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.TensorRuntimeException;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.api.TensorScalarFunction;
import ch.alpine.tensor.api.TensorUnaryOperator;

public class VectorNormsTest {
  public static final TensorScalarFunction[] VALUES = { //
      Vector1Norm::of, //
      Vector2Norm::of, //
      VectorInfinityNorm::of, //
  };

  @Test
  public void testEmptyFail() {
    for (TensorScalarFunction norm : VALUES) {
      assertThrows(NoSuchElementException.class, () -> norm.apply(Tensors.empty()));
      assertThrows(TensorRuntimeException.class, () -> norm.apply(RealScalar.ONE));
      assertThrows(ClassCastException.class, () -> norm.apply(Tensors.fromString("{{1, 2}, {3}}")));
    }
  }

  @Test
  public void testOk1() {
    Tensor v = Tensors.vector(0, 0, 0, 0);
    for (TensorScalarFunction norm : VALUES)
      assertEquals(v, NormalizeUnlessZero.with(norm).apply(v));
  }

  @Test
  public void testEmpty() {
    for (TensorScalarFunction norm : VALUES) {
      TensorUnaryOperator tensorUnaryOperator = NormalizeUnlessZero.with(norm);
      Tensor tensor = Tensors.empty();
      assertThrows(NoSuchElementException.class, () -> tensorUnaryOperator.apply(tensor));
    }
  }
}
