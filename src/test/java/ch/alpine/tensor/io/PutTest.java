// code by jph
package ch.alpine.tensor.io;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.File;
import java.io.IOException;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;

public class PutTest {
  @Test
  public void testUnstructured(@TempDir File tempDir) throws IOException {
    File file = new File(tempDir, "file.put");
    Tensor tensor = Tensors.fromString("{{2, 3.123+3*I, 34.1231}, {556, 3/456, -323/2, {3, 8.45`}}}");
    Put.of(file, tensor.unmodifiable());
    Tensor readin = Get.of(file);
    assertEquals(tensor, readin);
  }

  @Test
  public void testNullFail(@TempDir File tempDir) {
    File file = new File(tempDir, "file.put");
    assertThrows(Exception.class, () -> Put.of(file, null));
  }
}
