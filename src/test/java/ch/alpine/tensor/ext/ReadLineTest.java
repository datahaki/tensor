// code by jph
package ch.alpine.tensor.ext;

import java.io.IOException;
import java.io.InputStream;

import junit.framework.TestCase;

public class ReadLineTest extends TestCase {
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

  public void testFail() {
    try (InputStream inputStream = getClass().getResource("/io/doesnotexist.csv").openStream()) {
      fail();
    } catch (Exception exception) {
      // ---
    }
  }

  public void testNullFail() {
    try {
      ReadLine.of(null);
      fail();
    } catch (Exception exception) {
      // ---
    }
  }
}
