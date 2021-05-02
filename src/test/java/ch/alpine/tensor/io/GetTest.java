// code by jph
package ch.alpine.tensor.io;

import java.io.File;
import java.io.IOException;
import java.util.Objects;

import ch.alpine.tensor.ScalarQ;
import ch.alpine.tensor.Tensor;
import junit.framework.TestCase;

public class GetTest extends TestCase {
  public void testResource() throws IOException {
    File file = new File(getClass().getResource("/io/basic.mathematica").getFile());
    Tensor tensor = Get.of(file);
    assertTrue(Objects.nonNull(tensor));
    assertFalse(ScalarQ.of(tensor));
    assertEquals(tensor.length(), 13);
    assertEquals(tensor, Get.of(file));
  }

  public void testBinary() throws IOException { // this use is not as intended
    File file = new File(getClass().getResource("/io/image/rgb7x11.bmp").getFile());
    Tensor tensor = Get.of(file);
    assertTrue(tensor instanceof StringScalar);
  }

  public void testMissing() {
    File file = new File("/io/doesnotexist");
    try {
      Get.of(file);
      fail();
    } catch (Exception exception) {
      // ---
    }
  }
}
