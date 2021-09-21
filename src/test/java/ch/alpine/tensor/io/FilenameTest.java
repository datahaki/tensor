// code by jph
package ch.alpine.tensor.io;

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

import ch.alpine.tensor.ext.HomeDirectory;
import ch.alpine.tensor.pdf.DiscreteUniformDistribution;
import ch.alpine.tensor.pdf.RandomVariate;
import ch.alpine.tensor.usr.AssertFail;
import junit.framework.TestCase;

public class FilenameTest extends TestCase {
  public void testImageWriter() throws IOException {
    File file = HomeDirectory.file(getClass().getSimpleName() + ".jpg");
    assertFalse(file.exists());
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

  public void testFailSpacing() {
    Filename filename = new Filename("dir/title.bmp ");
    AssertFail.of(() -> filename.extension());
  }

  public void testFailExtension() {
    Filename filename = new Filename("dir/title.ext");
    AssertFail.of(() -> filename.extension());
  }

  public void testFailNoExt() {
    Filename filename = new Filename("dir/mybmp");
    AssertFail.of(() -> filename.extension());
  }

  public void testFailTruncate() {
    Filename filename = new Filename("dir/mybmp");
    AssertFail.of(() -> filename.truncate());
  }
}
