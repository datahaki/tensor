// code by jph
package ch.alpine.tensor.io;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.File;
import java.io.IOException;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;

class GetTest {
  @Test
  void testResource() throws IOException {
    File file = new File(getClass().getResource("/ch/alpine/tensor/io/basic.mathematica").getFile());
    Tensor tensor = Get.of(file);
    assertNotNull(tensor);
    assertFalse(tensor instanceof Scalar);
    assertEquals(tensor.length(), 13);
    assertEquals(tensor, Get.of(file));
  }

  @Test
  void testBinary() throws IOException { // this use is not as intended
    File file = new File(getClass().getResource("/ch/alpine/tensor/img/rgb7x11.bmp").getFile());
    Tensor tensor = Get.of(file);
    assertInstanceOf(StringScalar.class, tensor);
  }

  @Test
  void testMissing() {
    File file = new File("/ch/alpine/tensor/io/doesnotexist");
    assertThrows(Exception.class, () -> Get.of(file));
  }
}
