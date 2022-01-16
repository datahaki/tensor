// code by jph
package ch.alpine.tensor.mat.qr;

import java.lang.reflect.Modifier;

import junit.framework.TestCase;

public class QRDecompositionBaseTest extends TestCase {
  public void testPackageVisibility() {
    assertFalse(Modifier.isPublic(QRDecompositionBase.class.getModifiers()));
  }
}
