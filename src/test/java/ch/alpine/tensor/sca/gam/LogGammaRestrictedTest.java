// code by jph
package ch.alpine.tensor.sca.gam;

import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

class LogGammaRestrictedTest {
  @Test
  void testNullFail() {
    assertThrows(NullPointerException.class, () -> LogGammaRestricted.FUNCTION.apply(null));
  }
}
