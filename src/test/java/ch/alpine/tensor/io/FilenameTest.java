// code by jph
package ch.alpine.tensor.io;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

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
import org.junit.jupiter.api.io.TempDir;

import ch.alpine.tensor.pdf.RandomVariate;
import ch.alpine.tensor.pdf.d.DiscreteUniformDistribution;

class FilenameTest {
  @Test
  void testImageWriter(@TempDir File tempDir) throws IOException {
    File file = new File(tempDir, "file.jpg");
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
    assertTrue(file.exists());
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
