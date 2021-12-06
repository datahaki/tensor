// code by jph
package ch.alpine.tensor.usr;

import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.qty.Quantity;
import junit.framework.TestCase;

public class MathematicaFormTest extends TestCase {
  public void testSimple() {
    String string = MathematicaForm.of(Quantity.of(3, "m^2*s"));
    assertEquals(string, "Quantity[3, \"Meters\"^2*\"Seconds\"]");
  }

  public void testSimple2() {
    String string = MathematicaForm.of(Tensors.of(Quantity.of(3, "A^2*V^-1")));
    assertEquals(string, "{Quantity[3, \"Amperes\"^2*\"Volts\"^-1]}");
  }
}
