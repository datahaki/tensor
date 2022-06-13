// code by jph
package ch.alpine.tensor.sca.tri;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.DoubleScalar;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.TensorRuntimeException;
import ch.alpine.tensor.qty.Quantity;

class SinhcTest {
  @Test
  void testSimple() {
    assertEquals(Sinhc.FUNCTION.apply(RealScalar.ZERO), RealScalar.ONE);
  }

  @Test
  void testMin() {
    Scalar eps = DoubleScalar.of(Double.MIN_VALUE);
    assertEquals(Sinhc.FUNCTION.apply(eps), RealScalar.ONE);
  }

  @Test
  void testMinNeg() {
    Scalar eps = DoubleScalar.of(-Double.MIN_VALUE);
    assertEquals(Sinhc.FUNCTION.apply(eps), RealScalar.ONE);
  }

  @Test
  void testEps() {
    Scalar eps = DoubleScalar.of(1e-12);
    assertEquals(Sinhc.FUNCTION.apply(eps), RealScalar.ONE);
  }

  @Test
  void testFail() {
    assertThrows(TensorRuntimeException.class, () -> Sinhc.FUNCTION.apply(Quantity.of(0, "m")));
  }
}
