// code by jph
package ch.ethz.idsc.tensor.lie;

import java.lang.reflect.Modifier;

import junit.framework.TestCase;

public class QRSignOperatorTest extends TestCase {
  public void testPackageVisibility() {
    assertFalse(Modifier.isPublic(QRSignOperator.class.getModifiers()));
  }
}
