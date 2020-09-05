// code by jph
package ch.ethz.idsc.tensor.mat;

import java.io.IOException;
import java.lang.reflect.Modifier;

import ch.ethz.idsc.tensor.io.Serialization;
import junit.framework.TestCase;

public class PivotsTest extends TestCase {
  public void testPackageVisibility() {
    assertTrue(Modifier.isPublic(Pivots.class.getModifiers()));
    assertTrue(Modifier.isPublic(Pivot.class.getModifiers()));
    assertTrue(Modifier.isPublic(Pivots.class.getModifiers()));
  }

  public void testSerializable() throws ClassNotFoundException, IOException {
    for (Pivot pivot : Pivots.values())
      Serialization.copy(pivot);
  }
}
