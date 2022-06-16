// code by jph
package ch.alpine.tensor.mat.qr;

import static org.junit.jupiter.api.Assertions.assertFalse;

import java.lang.reflect.Modifier;

import org.junit.jupiter.api.Test;

class QRDecompositionBaseTest {
  @Test
  void testPackageVisibility() {
    assertFalse(Modifier.isPublic(QRDecompositionBase.class.getModifiers()));
  }
}
