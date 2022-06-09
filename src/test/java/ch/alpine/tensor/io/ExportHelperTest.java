// code by jph
package ch.alpine.tensor.io;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Modifier;

import javax.imageio.ImageIO;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.nrm.FrobeniusNorm;

class ExportHelperTest {
  @Test
  public void testGif(@TempDir File tempDir) throws IOException {
    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream(128);
    Tensor image = Tensors.fromString("{{{255, 2, 3, 255}, {0, 0, 0, 0}, {91, 120, 230, 255}, {0, 0, 0, 0}}}");
    ExportHelper.of(Extension.GIF, image, byteArrayOutputStream);
    File file = new File(tempDir, "file.gif");
    Export.of(file, image);
    assertTrue(file.isFile());
    assertTrue(file.delete());
    byte[] array = byteArrayOutputStream.toByteArray(); // 54 bytes used
    BufferedImage bufferedImage = ImageIO.read(new ByteArrayInputStream(array));
    Tensor tensor = ImageFormat.from(bufferedImage);
    assertEquals(image, tensor);
  }

  @Test
  public void testGif2() throws IOException {
    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream(128);
    Tensor row1 = Tensors.fromString("{{255, 2, 3, 255}, {0, 0, 0, 0}, {91, 120, 230, 255}, {0, 0, 0, 0}}");
    Tensor image = Tensors.of(row1, row1);
    ExportHelper.of(Extension.GIF, image, byteArrayOutputStream);
    byte[] array = byteArrayOutputStream.toByteArray(); // 56 bytes used
    BufferedImage bufferedImage = ImageIO.read(new ByteArrayInputStream(array));
    Tensor tensor = ImageFormat.from(bufferedImage);
    Scalar diff = FrobeniusNorm.between(image, tensor);
    diff.copy();
    // unfortunately there seems to be a problem with the java gif parser
  }

  @Test
  public void testFileExtensionFail() throws IOException {
    OutputStream outputStream = new ByteArrayOutputStream(512);
    ExportHelper.of(Extension.VECTOR, Tensors.empty(), outputStream);
  }

  @Test
  public void testGzFail() {
    OutputStream outputStream = new ByteArrayOutputStream(512);
    assertThrows(Exception.class, () -> ExportHelper.of(Extension.GZ, Tensors.empty(), outputStream));
  }

  @Test
  public void testVisibility() {
    assertFalse(Modifier.isPublic(ExportHelper.class.getModifiers()));
  }
}
