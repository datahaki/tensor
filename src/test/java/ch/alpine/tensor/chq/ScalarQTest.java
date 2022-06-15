// code by jph
package ch.alpine.tensor.chq;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.TensorRuntimeException;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.io.StringScalar;
import ch.alpine.tensor.num.GaussScalar;
import ch.alpine.tensor.qty.Quantity;

class ScalarQTest {
  @Test
  public void testScalar() {
    assertInstanceOf(Scalar.class, Quantity.of(3, "m"));
    assertInstanceOf(Scalar.class, GaussScalar.of(3, 11));
    assertInstanceOf(Scalar.class, StringScalar.of("IDSC"));
  }

  @Test
  public void testVector() {
    assertFalse(Tensors.vector(1, 2, 3) instanceof Scalar);
  }

  @Test
  public void testThenThrow() {
    ScalarQ.thenThrow(Tensors.vector(1, 2, 3));
    assertThrows(TensorRuntimeException.class, () -> ScalarQ.thenThrow(RealScalar.ONE));
  }
}