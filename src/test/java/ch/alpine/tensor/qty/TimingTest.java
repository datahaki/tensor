// code by jph
package ch.alpine.tensor.qty;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.Serializable;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.mat.Tolerance;
import ch.alpine.tensor.sca.Sign;

class TimingTest {
  @Test
  void testSimple() {
    Timing timing = Timing.stopped();
    assertEquals(timing.nanoSeconds(), Quantity.of(0, "ns"));
    assertEquals(timing.seconds(), Quantity.of(0, "s"));
    assertThrows(NullPointerException.class, timing::stop);
    timing.start();
    Math.sin(1);
    Sign.requirePositive(timing.nanoSeconds());
    timing.stop();
    Sign.requirePositive(timing.seconds());
    Tolerance.CHOP.requireClose(timing.seconds(), //
        UnitSystem.SI().apply(timing.nanoSeconds()));
  }

  @Test
  void testNonSerializable() {
    Timing timing = Timing.started();
    assertFalse(timing instanceof Serializable);
  }

  @Test
  void testStartedFail() {
    Timing timing = Timing.started();
    assertThrows(IllegalStateException.class, timing::start);
    Sign.requirePositive(timing.nanoSeconds());
  }

  @Test
  void testStopFail() {
    Timing timing = Timing.started();
    Math.sin(1);
    Sign.requirePositive(timing.nanoSeconds());
    timing.stop();
    assertThrows(NullPointerException.class, timing::stop);
  }
}
