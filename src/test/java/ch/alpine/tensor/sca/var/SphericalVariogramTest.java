// code by jph
package ch.alpine.tensor.sca.var;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Scalars;
import ch.alpine.tensor.api.ScalarUnaryOperator;
import ch.alpine.tensor.qty.Quantity;
import ch.alpine.tensor.sca.Chop;

class SphericalVariogramTest {
  @Test
  void testSimple() {
    ScalarUnaryOperator variogram = SphericalVariogram.of(5, 3);
    Scalar lo = variogram.apply(RealScalar.of(4.9999));
    Scalar hi = variogram.apply(RealScalar.of(5.0001));
    Chop._05.requireClose(lo, hi);
    Chop._05.requireClose(lo, RealScalar.of(3));
  }

  @Test
  void testQuantity() {
    ScalarUnaryOperator variogram = SphericalVariogram.of(Quantity.of(2, "m"), Quantity.of(4, "s"));
    Scalar lo = variogram.apply(Quantity.of(1, "m"));
    Scalar hi = variogram.apply(Quantity.of(5, "m"));
    Chop._05.requireClose(lo, Scalars.fromString("11/4[s]"));
    Chop._05.requireClose(hi, Quantity.of(4, "s"));
  }

  @Test
  void serialize() {
    assertDoesNotThrow(() -> SphericalVariogram.of(5, 3));
  }
}
