// code by jph
package ch.alpine.tensor.mat.qr;

import static org.junit.jupiter.api.Assertions.assertFalse;

import java.lang.reflect.Modifier;

import org.junit.jupiter.api.Test;

public class QRDecompositionBaseTest {
  @Test
  public void testPackageVisibility() {
    assertFalse(Modifier.isPublic(QRDecompositionBase.class.getModifiers()));
  }
}
