// code by jph
package ch.ethz.idsc.tensor.qty;

import java.io.IOException;

import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.api.ScalarUnaryOperator;
import ch.ethz.idsc.tensor.ext.Serialization;
import junit.framework.TestCase;

public class UnitDimensionsTest extends TestCase {
  public void testSimple() throws ClassNotFoundException, IOException {
    UnitSystem unitSystem = Serialization.copy(UnitSystem.SI());
    assertEquals(unitSystem.dimensions(Unit.of("N")), Unit.of("kg*m*s^-2"));
    assertEquals(unitSystem.dimensions(Unit.of("km")), Unit.of("m"));
    assertEquals(unitSystem.dimensions(Unit.of("km*h^3")), Unit.of("m*s^3"));
    assertEquals(unitSystem.dimensions(Unit.of("kW*h")), Unit.of("kg*m^2*s^-2"));
    assertEquals(unitSystem.dimensions(Unit.of("xknown^3")), Unit.of("xknown^3"));
    assertEquals(unitSystem.dimensions(Unit.of("xkn*own^3")), Unit.of("xkn*own^3"));
    assertEquals(unitSystem.dimensions(Unit.of("g^-2*xkn*own^3")), Unit.of("kg^-2*xkn*own^3"));
  }

  public void testTogs() {
    ScalarUnaryOperator suo = QuantityMagnitude.SI().in("m^2*K*W^-1");
    Scalar scalar = suo.apply(Quantity.of(10, "togs"));
    assertEquals(scalar, RealScalar.ONE);
  }
}
