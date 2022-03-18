// code by jph
package ch.alpine.tensor.ext;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.Serializable;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.usr.AssertFail;

public class TimingTest {
  @Test
  public void testSimple() {
    Timing timing = Timing.stopped();
    assertEquals(timing.nanoSeconds(), 0);
    assertEquals(timing.seconds(), 0.0);
    AssertFail.of(() -> timing.stop());
    timing.start();
    Math.sin(1);
    assertTrue(0 < timing.nanoSeconds());
    timing.stop();
    assertTrue(0 < timing.seconds());
    assertEquals(timing.seconds(), timing.nanoSeconds() * 1e-9);
  }

  @Test
  public void testNonSerializable() {
    Timing timing = Timing.started();
    assertFalse(timing instanceof Serializable);
  }

  @Test
  public void testStartedFail() {
    Timing timing = Timing.started();
    AssertFail.of(() -> timing.start());
    assertTrue(0 < timing.nanoSeconds());
  }

  @Test
  public void testStopFail() {
    Timing timing = Timing.started();
    Math.sin(1);
    assertTrue(0 < timing.nanoSeconds());
    timing.stop();
    AssertFail.of(() -> timing.stop());
  }
}
