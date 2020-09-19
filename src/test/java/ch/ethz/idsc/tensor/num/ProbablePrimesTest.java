// code by jph
package ch.ethz.idsc.tensor.num;

import java.lang.reflect.Modifier;
import java.math.BigInteger;

import junit.framework.TestCase;

public class ProbablePrimesTest extends TestCase {
  public void testPrimeFail() {
    try {
      ProbablePrimes.INSTANCE.require(BigInteger.TEN);
      fail();
    } catch (Exception exception) {
      // ---
    }
  }

  public void testPackageVisibility() {
    assertFalse(Modifier.isPublic(ProbablePrimes.class.getModifiers()));
  }
}
