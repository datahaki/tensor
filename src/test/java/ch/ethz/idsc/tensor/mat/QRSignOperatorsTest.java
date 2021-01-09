// code by jph
package ch.ethz.idsc.tensor.mat;

import java.io.IOException;
import java.lang.reflect.Modifier;

import ch.ethz.idsc.tensor.ext.Serialization;
import junit.framework.TestCase;

public class QRSignOperatorsTest extends TestCase {
  public void testSerializable() throws ClassNotFoundException, IOException {
    for (QRSignOperator qrSignOperator : QRSignOperators.values())
      Serialization.copy(qrSignOperator);
  }

  public void testIsDetExact() {
    assertFalse(QRSignOperators.ORIENTATION.isDetExact());
    assertTrue(QRSignOperators.STABILITY.isDetExact());
  }

  public void testPackageVisibility() {
    assertTrue(Modifier.isPublic(QRSignOperators.class.getModifiers()));
  }
}
