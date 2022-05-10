// code by jph
package ch.alpine.tensor.mat.qr;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.lang.reflect.Modifier;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.ext.Serialization;

class QRSignOperatorsTest {
  @Test
  public void testSerializable() throws ClassNotFoundException, IOException {
    for (QRSignOperator qrSignOperator : QRSignOperators.values())
      Serialization.copy(qrSignOperator);
  }

  @Test
  public void testIsDetExact() {
    assertTrue(QRSignOperators.STABILITY.isDetExact());
    assertFalse(QRSignOperators.ORIENTATION.isDetExact());
  }

  @Test
  public void testPackageVisibility() {
    assertTrue(Modifier.isPublic(QRSignOperators.class.getModifiers()));
  }
}
