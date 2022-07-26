// code by jph
package ch.alpine.tensor.io;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import java.lang.reflect.Modifier;
import java.nio.charset.StandardCharsets;
import java.util.Random;

import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;

class StaticHelperTest {
  @Test
  void testCharset() {
    assertEquals(StaticHelper.CHARSET.displayName(), "UTF-8");
  }

  @RepeatedTest(10)
  void testUsAscii() {
    Random random = new Random();
    byte[] data = new byte[random.nextInt(567)];
    random.nextBytes(data);
    String string = new String(data, StandardCharsets.US_ASCII);
    assertEquals(data.length, string.length());
  }

  @RepeatedTest(10)
  void testUtf8() {
    Random random = new Random();
    byte[] data = new byte[random.nextInt(567)];
    random.nextBytes(data);
    String string = new String(data, StandardCharsets.ISO_8859_1);
    assertEquals(data.length, string.length());
  }

  @Test
  void testPackageVisibility() {
    assertFalse(Modifier.isPublic(StaticHelper.class.getModifiers()));
  }
}
