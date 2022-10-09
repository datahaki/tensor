// code by jph
package ch.alpine.tensor.prc;

import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

class RenewalProcessTest {
  @Test
  void test() {
    assertThrows(Exception.class, () -> RenewalProcess.of(null));
  }
}
