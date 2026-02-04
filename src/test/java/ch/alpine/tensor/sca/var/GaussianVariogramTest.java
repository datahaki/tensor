// code by jph
package ch.alpine.tensor.sca.var;

import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.api.ScalarUnaryOperator;
import ch.alpine.tensor.qty.Quantity;
import ch.alpine.tensor.sca.Chop;

class GaussianVariogramTest {
  @Test
  void testQuantity() {
    ScalarUnaryOperator variogram = new GaussianVariogram(Quantity.of(2, "m"));
    Scalar lo = variogram.apply(Quantity.of(1, "m"));
    Chop._05.requireClose(lo, RealScalar.of(0.7788007830714049));
    Scalar hi = variogram.apply(Quantity.of(5, "m"));
    Chop._05.requireClose(hi, RealScalar.of(0.0019304541362277093));
  }

  @Test
  void testZeroFail() {
    assertThrows(Exception.class, () -> new GaussianVariogram(RealScalar.ZERO));
  }
}
