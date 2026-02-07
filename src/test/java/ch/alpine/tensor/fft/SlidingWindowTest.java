// code by jph
package ch.alpine.tensor.fft;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.ext.Integers;
import test.wrap.SerializableQ;

class SlidingWindowTest {
  @Test
  void testWindowLengthPower2() {
    SlidingWindow slidingWindow = new SlidingWindow(null, null);
    SerializableQ.require(slidingWindow);
    for (int k = 1; k < 100; ++k) {
      Integer windowLength = slidingWindow.complete(k).windowLength();
      assertTrue(Integers.isPowerOf2(windowLength));
    }
  }
}
