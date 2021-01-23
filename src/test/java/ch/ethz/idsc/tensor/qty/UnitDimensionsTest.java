// code by jph
package ch.ethz.idsc.tensor.qty;

import java.io.IOException;

import ch.ethz.idsc.tensor.ext.Serialization;
import junit.framework.TestCase;

public class UnitDimensionsTest extends TestCase {
  public void testSimple() throws ClassNotFoundException, IOException {
    UnitDimensions unitDimensions = Serialization.copy(new UnitDimensions(UnitSystem.SI()));
    assertEquals(unitDimensions.toBase(Unit.of("N")), Unit.of("kg*m*s^-2"));
    assertEquals(unitDimensions.toBase(Unit.of("km")), Unit.of("m"));
    assertEquals(unitDimensions.toBase(Unit.of("km*h^3")), Unit.of("m*s^3"));
    assertEquals(unitDimensions.toBase(Unit.of("kW*h")), Unit.of("kg*m^2*s^-2"));
    assertEquals(unitDimensions.toBase(Unit.of("xknown^3")), Unit.of("xknown^3"));
    assertEquals(unitDimensions.toBase(Unit.of("xkn*own^3")), Unit.of("xkn*own^3"));
    assertEquals(unitDimensions.toBase(Unit.of("g^-2*xkn*own^3")), Unit.of("kg^-2*xkn*own^3"));
  }
}
