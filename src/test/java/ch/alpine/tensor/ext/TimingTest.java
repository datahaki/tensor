// code by jph
package ch.alpine.tensor.ext;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.Serializable;

import org.junit.jupiter.api.Test;

class TimingTest {
  @Test
  void testSimple() {
    Timing timing = Timing.stopped();
    assertEquals(timing.nanoSeconds(), 0);
    assertEquals(timing.seconds(), 0.0);
    assertThrows(NullPointerException.class, () -> timing.stop());
    timing.start();
    Math.sin(1);
    assertTrue(0 < timing.nanoSeconds());
    timing.stop();
    assertTrue(0 < timing.seconds());
    assertEquals(timing.seconds(), timing.nanoSeconds() * 1e-9);
  }

  @Test
  void testNonSerializable() {
    Timing timing = Timing.started();
    assertFalse(timing instanceof Serializable);
  }

  @Test
  void testStartedFail() {
    Timing timing = Timing.started();
    assertThrows(IllegalStateException.class, () -> timing.start());
    assertTrue(0 < timing.nanoSeconds());
  }

  @Test
  void testStopFail() {
    Timing timing = Timing.started();
    Math.sin(1);
    assertTrue(0 < timing.nanoSeconds());
    timing.stop();
    assertThrows(NullPointerException.class, () -> timing.stop());
  }
}
