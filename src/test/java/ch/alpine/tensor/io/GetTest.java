// code by jph
package ch.alpine.tensor.io;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.io.IOException;
import java.util.Objects;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.ScalarQTest;
import ch.alpine.tensor.Tensor;

public class GetTest {
  @Test
  public void testResource() throws IOException {
    File file = new File(getClass().getResource("/io/basic.mathematica").getFile());
    Tensor tensor = Get.of(file);
    assertTrue(Objects.nonNull(tensor));
    assertFalse(ScalarQTest.of(tensor));
    assertEquals(tensor.length(), 13);
    assertEquals(tensor, Get.of(file));
  }

  @Test
  public void testBinary() throws IOException { // this use is not as intended
    File file = new File(getClass().getResource("/io/image/rgb7x11.bmp").getFile());
    Tensor tensor = Get.of(file);
    assertTrue(tensor instanceof StringScalar);
  }

  @Test
  public void testMissing() {
    File file = new File("/io/doesnotexist");
    assertThrows(Exception.class, () -> Get.of(file));
  }
}
