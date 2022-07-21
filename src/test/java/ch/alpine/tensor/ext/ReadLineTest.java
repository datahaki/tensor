// code by jph
package ch.alpine.tensor.ext;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

import org.junit.jupiter.api.Test;

class ReadLineTest {
  @Test
  void testCount() throws IOException {
    try (InputStream inputStream = getClass().getResource("/ch/alpine/tensor/io/libreoffice_calc.csv").openStream()) {
      long count = ReadLine.of(inputStream).count();
      assertEquals(count, 4);
      assertEquals(inputStream.available(), 0);
      inputStream.close();
      assertThrows(Exception.class, () -> inputStream.available());
    }
  }

  @Test
  void testCharset() throws IOException {
    try (InputStream inputStream = getClass().getResource("/ch/alpine/tensor/io/libreoffice_calc.csv").openStream()) {
      Charset charset = StandardCharsets.US_ASCII;
      long count = ReadLine.of(inputStream, charset).count();
      assertEquals(count, 4);
      assertEquals(inputStream.available(), 0);
      inputStream.close();
      assertThrows(Exception.class, () -> inputStream.available());
    }
  }

  @Test
  void testFail() {
    try (InputStream inputStream = getClass().getResource("/ch/alpine/tensor/io/doesnotexist.csv").openStream()) {
      fail();
    } catch (Exception exception) {
      // ---
    }
  }

  @Test
  void testNullFail() {
    assertThrows(Exception.class, () -> ReadLine.of(null));
  }
}
