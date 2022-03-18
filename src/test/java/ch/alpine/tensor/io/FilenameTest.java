// code by jph
package ch.alpine.tensor.io;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Iterator;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageOutputStream;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.pdf.RandomVariate;
import ch.alpine.tensor.pdf.d.DiscreteUniformDistribution;
import ch.alpine.tensor.usr.TestFile;

public class FilenameTest {
  @Test
  public void testImageWriter() throws IOException {
    File file = TestFile.withExtension("jpg");
    try (OutputStream outputStream = new FileOutputStream(file)) {
      Iterator<ImageWriter> iterator = ImageIO.getImageWritersByFormatName("jpeg");
      ImageWriter imageWriter = iterator.next();
      ImageWriteParam imageWriteParam = imageWriter.getDefaultWriteParam();
      imageWriteParam.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
      imageWriteParam.setCompressionQuality(0.85f);
      BufferedImage bgr = ImageFormat.bgr(RandomVariate.of(DiscreteUniformDistribution.of(0, 256), 10, 20, 4));
      // BufferedImage bufferedImage = ImageFormat.of(RandomVariate.of(DiscreteUniformDistribution.of(0, 256), 10, 20));
      try (ImageOutputStream imageOutputStream = ImageIO.createImageOutputStream(outputStream)) {
        imageWriter.setOutput(imageOutputStream);
        imageWriter.write(null, new IIOImage(bgr, null, null), imageWriteParam);
        imageWriter.dispose();
      }
    }
    file.delete();
    assertFalse(file.exists());
  }

  @Test
  public void testFailSpacing() {
    Filename filename = new Filename("dir/title.bmp ");
    assertThrows(IllegalArgumentException.class, () -> filename.extension());
  }

  @Test
  public void testFailExtension() {
    Filename filename = new Filename("dir/title.ext");
    assertThrows(IllegalArgumentException.class, () -> filename.extension());
  }

  @Test
  public void testFailNoExt() {
    Filename filename = new Filename("dir/mybmp");
    assertThrows(IllegalArgumentException.class, () -> filename.extension());
  }

  @Test
  public void testFailTruncate() {
    Filename filename = new Filename("dir/mybmp");
    assertThrows(StringIndexOutOfBoundsException.class, () -> filename.truncate());
  }
}
