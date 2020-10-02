// code by jph
package ch.ethz.idsc.tensor.num;

import java.lang.reflect.Modifier;
import java.math.BigInteger;

import ch.ethz.idsc.tensor.usr.AssertFail;
import junit.framework.TestCase;

public class ProbablePrimesTest extends TestCase {
  public void testPrimeFail() {
    AssertFail.of(() -> ProbablePrimes.INSTANCE.require(BigInteger.TEN));
  }

  public void testPackageVisibility() {
    assertFalse(Modifier.isPublic(ProbablePrimes.class.getModifiers()));
  }
}
