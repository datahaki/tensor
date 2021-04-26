// code by jph
package ch.ethz.idsc.tensor.mat.re;

import java.io.IOException;
import java.lang.reflect.Modifier;

import ch.ethz.idsc.tensor.ext.Serialization;
import junit.framework.TestCase;

public class PivotsTest extends TestCase {
  public void testSerializable() throws ClassNotFoundException, IOException {
    for (Pivot pivot : Pivots.values())
      Serialization.copy(pivot);
  }

  public void testValueOf() {
    for (Pivots pivots : Pivots.values())
      assertEquals(Enum.valueOf(Pivots.class, pivots.name()), pivots);
  }

  public void testClassEnumConstants() {
    Class<?> cls = Pivots.class;
    assertTrue(Enum.class.isAssignableFrom(cls));
    Object[] enumConstants = cls.getEnumConstants();
    Object result = null;
    for (Object obj : enumConstants) {
      if (obj.toString().equals("ARGMAX_ABS")) {
        result = obj;
      }
    }
    assertEquals(result, Pivots.ARGMAX_ABS);
  }

  public void testPackageVisibility() {
    assertTrue(Modifier.isPublic(Pivots.class.getModifiers()));
  }
}
