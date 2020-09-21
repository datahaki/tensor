// code by jph
package ch.ethz.idsc.tensor.lie;

import java.io.IOException;
import java.lang.reflect.Modifier;

import ch.ethz.idsc.tensor.io.Serialization;
import junit.framework.TestCase;

public class QRSignOperatorsTest extends TestCase {
  public void testSerializable() throws ClassNotFoundException, IOException {
    for (QRSignOperator qrSignOperator : QRSignOperators.values())
      Serialization.copy(qrSignOperator);
  }

  public void testPackageVisibility() {
    assertTrue(Modifier.isPublic(QRSignOperators.class.getModifiers()));
  }
}
