// code by jph
package ch.alpine.tensor.usr;

import java.util.Objects;
import java.util.Properties;

import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.io.ResourceData;
import ch.alpine.tensor.qty.KnownUnitQ;
import ch.alpine.tensor.qty.Quantity;
import ch.alpine.tensor.qty.Unit;
import ch.alpine.tensor.qty.UnitSystem;
import junit.framework.TestCase;

public class MathematicaFormTest extends TestCase {
  private static final Properties PROPERTIES = ResourceData.properties("/unit/names.properties");

  public void testSimple() {
    String string = MathematicaForm.of(Quantity.of(3, "m^2*s"));
    assertEquals(string, "Quantity[3, \"Meters\"^2*\"Seconds\"]");
  }

  public void testSimple2() {
    String string = MathematicaForm.of(Tensors.of(Quantity.of(3, "A^2*V^-1")));
    assertEquals(string, "{Quantity[3, \"Amperes\"^2*\"Volts\"^-1]}");
  }

  public void testKeys() {
    for (String string : PROPERTIES.stringPropertyNames())
      assertTrue(KnownUnitQ.SI().test(Unit.of(string)));
  }

  public void testUnits() {
    int count = 0;
    for (String string : UnitSystem.SI().map().keySet()) {
      String value = PROPERTIES.getProperty(string);
      if (Objects.isNull(value))
        ++count;
    }
    assertTrue(count < 100);
  }
}
