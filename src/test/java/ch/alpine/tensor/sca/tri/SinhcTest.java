// code by jph
package ch.alpine.tensor.sca.tri;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import ch.alpine.tensor.DoubleScalar;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Throw;
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

  @ParameterizedTest
  @ValueSource(doubles = { 1e-12, 1e-20, 1e-30, 1e-50 })
  void testEps(double angle) {
    Scalar eps = DoubleScalar.of(angle);
    assertEquals(Sinhc.FUNCTION.apply(eps), RealScalar.ONE);
  }

  @Test
  void testFail() {
    assertThrows(Throw.class, () -> Sinhc.FUNCTION.apply(Quantity.of(0, "m")));
  }
}
