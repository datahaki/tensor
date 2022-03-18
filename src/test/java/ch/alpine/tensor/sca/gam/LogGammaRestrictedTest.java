// code by jph
package ch.alpine.tensor.sca.gam;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.usr.AssertFail;

public class LogGammaRestrictedTest {
  @Test
  public void testNullFail() {
    AssertFail.of(() -> LogGammaRestricted.FUNCTION.apply(null));
  }
}
