// code by jph
package ch.alpine.tensor.io;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Modifier;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Iterator;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageOutputStream;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.DisabledOnOs;
import org.junit.jupiter.api.condition.OS;
import org.junit.jupiter.api.io.TempDir;

import ch.alpine.tensor.ext.PathName;
import ch.alpine.tensor.pdf.RandomVariate;
import ch.alpine.tensor.pdf.d.DiscreteUniformDistribution;

class ExtensionTest {
  @TempDir
  Path tempDir;

  @Test
  void testImageWriter() throws IOException {
    Path path = tempDir.resolve("file.jpg");
    try (OutputStream outputStream = Files.newOutputStream(path)) {
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
    assertTrue(Files.exists(path));
  }

  @DisabledOnOs(OS.WINDOWS)
  @Test
  void testFailSpacing() {
    PathName filename = PathName.of(Path.of("dir/title.bmp "));
    filename.parent();
  }

  @Test
  void testTempDir() {
    PathName pathName = PathName.of(tempDir.resolve("title.ext"));
    assertEquals(pathName.title(), "title");
    assertEquals(pathName.extension(), "ext");
  }

  @Test
  void testComponents() {
    PathName pathName = PathName.of(Path.of("dir/title.ext"));
    assertEquals(pathName.parent(), Path.of("dir"));
    assertEquals(pathName.title(), "title");
    assertEquals(pathName.extension(), "ext");
    assertEquals(pathName.hasDot(), true);
  }

  @Test
  void testNoExt() {
    PathName pathName = PathName.of(Path.of("dir/mybmp"));
    assertEquals(pathName.parent(), Path.of("dir"));
    assertEquals(pathName.title(), "mybmp");
    assertEquals(pathName.extension(), "");
    assertEquals(pathName.hasDot(), false);
  }

  @Test
  void testFailTruncate() {
    PathName filename = PathName.of(Path.of("dir/mybmp"));
    assertEquals(filename, filename.truncate());
  }

  @Test
  void testTruncate() {
    PathName filename = PathName.of(Path.of("dir/some.bmp.gz"));
    assertEquals(Extension.of(filename.extension()), Extension.GZ);
    PathName truncate = filename.truncate();
    assertEquals(Extension.of(truncate.extension()), Extension.BMP);
  }

  @Test
  void testExtension() {
    PathName filename = PathName.of(Path.of("dir/some.gif"));
    assertEquals(Extension.of(filename.extension()), Extension.GIF);
  }

  @Test
  void testSimple() {
    assertEquals(Extension.of("bMp"), Extension.BMP);
    assertEquals(Extension.of("gz"), Extension.GZ);
  }

  @Test
  void testFail() {
    assertThrows(Exception.class, Extension.CSV::colorType);
    assertThrows(IllegalArgumentException.class, () -> Extension.of("unknown"));
  }

  @Test
  void testJavaFail() {
    assertThrows(Exception.class, () -> Extension.valueOf("asd"));
  }

  @Test
  void testVisibility() {
    assertFalse(Modifier.isPublic(Extension.class.getModifiers()));
  }
}
