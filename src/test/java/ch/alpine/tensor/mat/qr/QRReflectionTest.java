// code by jph
package ch.alpine.tensor.mat.qr;

import static org.junit.jupiter.api.Assertions.assertFalse;

import java.lang.reflect.Modifier;

import org.junit.jupiter.api.Test;

class QRReflectionTest {
  @Test
  void testPackageVisibility() {
    assertFalse(Modifier.isPublic(QRReflection.class.getModifiers()));
  }
}
