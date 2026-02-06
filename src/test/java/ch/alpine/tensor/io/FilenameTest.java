// code by jph
package ch.alpine.tensor.io;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Iterator;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageOutputStream;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import ch.alpine.tensor.pdf.RandomVariate;
import ch.alpine.tensor.pdf.d.DiscreteUniformDistribution;

class FilenameTest {
  @TempDir
  Path tempDir;

  @Test
  void testImageWriter() throws IOException {
    Path file = tempDir.resolve("file.jpg");
    try (OutputStream outputStream = Files.newOutputStream(file)) {
      Iterator<ImageWriter> iterator = ImageIO.getImageWritersByFormatName("jpeg");
      ImageWriter imageWriter = iterator.next();
      ImageWriteParam imageWriteParam = imageWriter.getDefaultWriteParam();
      imageWriteParam.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
      imageWriteParam.setCompressionQuality(0.85f);
      BufferedImage bgr = ImageFormat._of(RandomVariate.of(DiscreteUniformDistribution.forArray(256), 10, 20, 4), Extension.BMP);
      // BufferedImage bufferedImage = ImageFormat.of(RandomVariate.of(DiscreteUniformDistribution.of(0, 256), 10, 20));
      try (ImageOutputStream imageOutputStream = ImageIO.createImageOutputStream(outputStream)) {
        imageWriter.setOutput(imageOutputStream);
        imageWriter.write(null, new IIOImage(bgr, null, null), imageWriteParam);
        imageWriter.dispose();
      }
    }
    assertTrue(Files.exists(file));
  }

  @Test
  void testFailSpacing() {
    Filename filename = new Filename("dir/title.bmp ");
    assertThrows(IllegalArgumentException.class, filename::extension);
  }

  @Test
  void testFailExtension() {
    Filename filename = new Filename("dir/title.ext");
    assertThrows(IllegalArgumentException.class, filename::extension);
  }

  @Test
  void testFailNoExt() {
    Filename filename = new Filename("dir/mybmp");
    assertThrows(IllegalArgumentException.class, filename::extension);
  }

  @Test
  void testFailTruncate() {
    Filename filename = new Filename("dir/mybmp");
    assertThrows(StringIndexOutOfBoundsException.class, filename::truncate);
  }
}
