// code by jph
package ch.alpine.tensor.io;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.Test;

class GifAnimationWriterTest {
  @Test
  public void testSimple() {
    assertEquals(TimeUnit.MILLISECONDS.convert(1, TimeUnit.SECONDS), 1000L);
    assertEquals(TimeUnit.MILLISECONDS.convert(1000_000, TimeUnit.MICROSECONDS), 1000L);
  }
}
