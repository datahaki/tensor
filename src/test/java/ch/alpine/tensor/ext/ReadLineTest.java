// code by jph
package ch.alpine.tensor.ext;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.IOException;
import java.io.InputStream;

import org.junit.jupiter.api.Test;

public class ReadLineTest {
  @Test
  public void testCount() throws IOException {
    try (InputStream inputStream = getClass().getResource("/io/libreoffice_calc.csv").openStream()) {
      long count = ReadLine.of(inputStream).count();
      assertEquals(count, 4);
      assertEquals(inputStream.available(), 0);
      inputStream.close();
      try {
        inputStream.available();
        fail();
      } catch (Exception exception) {
        // ---
      }
    }
  }

  @Test
  public void testFail() {
    try (InputStream inputStream = getClass().getResource("/io/doesnotexist.csv").openStream()) {
      fail();
    } catch (Exception exception) {
      // ---
    }
  }

  @Test
  public void testNullFail() {
    try {
      ReadLine.of(null);
      fail();
    } catch (Exception exception) {
      // ---
    }
  }
}
