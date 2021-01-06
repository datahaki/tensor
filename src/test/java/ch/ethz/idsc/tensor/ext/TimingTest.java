// code by jph
package ch.ethz.idsc.tensor.ext;

import java.io.Serializable;

import ch.ethz.idsc.tensor.usr.AssertFail;
import junit.framework.TestCase;

public class TimingTest extends TestCase {
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

  public void testNonSerializable() {
    Timing timing = Timing.started();
    assertFalse(timing instanceof Serializable);
  }

  public void testStartedFail() {
    Timing timing = Timing.started();
    AssertFail.of(() -> timing.start());
    assertTrue(0 < timing.nanoSeconds());
  }

  public void testStopFail() {
    Timing timing = Timing.started();
    Math.sin(1);
    assertTrue(0 < timing.nanoSeconds());
    timing.stop();
    AssertFail.of(() -> timing.stop());
  }
}
