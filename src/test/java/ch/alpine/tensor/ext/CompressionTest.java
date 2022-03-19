// code by jph
package ch.alpine.tensor.ext;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Random;

import org.junit.jupiter.api.Test;

public class CompressionTest {
  private static byte[] createBytes(int length) {
    Random random = new SecureRandom();
    byte[] bytes = new byte[length];
    for (int count = 0; count < bytes.length; ++count)
      bytes[count] = (byte) random.nextInt(2);
    return bytes;
  }

  @Test
  public void testInflate() {
    byte[] bytes = createBytes(1000);
    byte[] comp = Compression.deflate(bytes);
    try {
      byte[] deco = Compression.inflate(comp);
      assertTrue(Arrays.equals(bytes, deco));
    } catch (Exception exception) {
      exception.printStackTrace();
      fail();
    }
  }

  @Test
  public void testInflateEmpty() {
    byte[] bytes = createBytes(0);
    byte[] comp = Compression.deflate(bytes);
    try {
      byte[] deco = Compression.inflate(comp);
      assertTrue(Arrays.equals(bytes, deco));
    } catch (Exception exception) {
      exception.printStackTrace();
    }
  }

  @Test
  public void testInflateEmpty2() {
    assertThrows(Exception.class, () -> Compression.inflate(new byte[0]));
  }

  @Test
  public void testInflateCurrupt() {
    byte[] bytes = createBytes(1000);
    byte[] comp = Compression.deflate(bytes);
    comp[comp.length - 6] = (byte) (comp[comp.length - 6] - 23);
    comp[comp.length - 5] = (byte) (comp[comp.length - 5] - 23);
    comp[comp.length - 4] = (byte) (comp[comp.length - 4] - 23);
    comp[comp.length - 3] = (byte) (comp[comp.length - 3] - 23);
    assertThrows(Exception.class, () -> Compression.inflate(comp));
  }

  @Test
  public void testInflateIncomplete() {
    byte[] bytes = createBytes(1000);
    byte[] comp = Compression.deflate(bytes);
    assertThrows(Exception.class, () -> Compression.inflate(comp, 0, comp.length - 3));
  }
}
