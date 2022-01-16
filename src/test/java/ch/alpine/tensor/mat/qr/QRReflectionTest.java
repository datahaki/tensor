// code by jph
package ch.alpine.tensor.mat.qr;

import java.lang.reflect.Modifier;

import junit.framework.TestCase;

public class QRReflectionTest extends TestCase {
  public void testPackageVisibility() {
    assertFalse(Modifier.isPublic(QRReflection.class.getModifiers()));
  }
}
