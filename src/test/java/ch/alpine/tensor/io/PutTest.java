// code by jph
package ch.alpine.tensor.io;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.File;
import java.io.IOException;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.usr.TestFile;

public class PutTest {
  @Test
  public void testUnstructured() throws IOException {
    File file = TestFile.withExtension("put");
    Tensor tensor = Tensors.fromString("{{2, 3.123+3*I, 34.1231}, {556, 3/456, -323/2, {3, 8.45`}}}");
    Put.of(file, tensor.unmodifiable());
    Tensor readin = Get.of(file);
    assertTrue(file.delete());
    assertFalse(file.exists());
    assertEquals(tensor, readin);
  }

  @Test
  public void testNullFail() {
    File file = TestFile.withExtension("put");
    try {
      Put.of(file, null);
      fail();
    } catch (Exception exception) {
      // ---
    }
  }
}
