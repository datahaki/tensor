// code by jph
package ch.alpine.tensor.sca.gam;

import ch.alpine.tensor.usr.AssertFail;
import junit.framework.TestCase;

public class LogGammaRestrictedTest extends TestCase {
  public void testNullFail() {
    AssertFail.of(() -> LogGammaRestricted.FUNCTION.apply(null));
  }
}
