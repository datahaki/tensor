// code by jph
package ch.ethz.idsc.tensor.mat;

import java.io.IOException;
import java.lang.reflect.Modifier;

import ch.ethz.idsc.tensor.io.Serialization;
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

  public void testPackageVisibility() {
    assertTrue(Modifier.isPublic(Pivots.class.getModifiers()));
  }
}
