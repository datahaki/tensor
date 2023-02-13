// code by jph
package ch.alpine.tensor.ext;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.security.SecureRandom;
import java.util.random.RandomGenerator;

import org.junit.jupiter.api.Test;

class CompressionTest {
  private static byte[] createBytes(int length) {
    RandomGenerator random = new SecureRandom();
    byte[] bytes = new byte[length];
    for (int count = 0; count < bytes.length; ++count)
      bytes[count] = (byte) random.nextInt(2);
    return bytes;
  }

  @Test
  void testInflate() {
    byte[] bytes = createBytes(1000);
    byte[] comp = Compression.deflate(bytes);
    try {
      byte[] deco = Compression.inflate(comp);
      assertArrayEquals(bytes, deco);
    } catch (Exception exception) {
      throw new RuntimeException(exception);
    }
  }

  @Test
  void testInflateEmpty() {
    byte[] bytes = createBytes(0);
    byte[] comp = Compression.deflate(bytes);
    try {
      byte[] deco = Compression.inflate(comp);
      assertArrayEquals(bytes, deco);
    } catch (Exception exception) {
      throw new RuntimeException(exception);
    }
  }

  @Test
  void testInflateEmpty2() {
    assertThrows(Exception.class, () -> Compression.inflate(new byte[0]));
  }

  @Test
  void testInflateCurrupt() {
    byte[] bytes = createBytes(1000);
    byte[] comp = Compression.deflate(bytes);
    comp[comp.length - 6] = (byte) (comp[comp.length - 6] - 23);
    comp[comp.length - 5] = (byte) (comp[comp.length - 5] - 23);
    comp[comp.length - 4] = (byte) (comp[comp.length - 4] - 23);
    comp[comp.length - 3] = (byte) (comp[comp.length - 3] - 23);
    assertThrows(Exception.class, () -> Compression.inflate(comp));
  }

  @Test
  void testInflateIncomplete() {
    byte[] bytes = createBytes(1000);
    byte[] comp = Compression.deflate(bytes);
    assertThrows(Exception.class, () -> Compression.inflate(comp, 0, comp.length - 3));
  }
}
