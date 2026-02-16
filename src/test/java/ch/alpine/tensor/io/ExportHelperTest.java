// code by jph
package ch.alpine.tensor.io;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Modifier;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;

import javax.imageio.ImageIO;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import ch.alpine.tensor.Rational;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.Unprotect;
import ch.alpine.tensor.alg.Array;
import ch.alpine.tensor.alg.Dimensions;
import ch.alpine.tensor.alg.Flatten;
import ch.alpine.tensor.alg.Transpose;
import ch.alpine.tensor.api.ScalarTensorFunction;
import ch.alpine.tensor.fft.ListConvolve;
import ch.alpine.tensor.img.ColorDataGradients;
import ch.alpine.tensor.img.Raster;
import ch.alpine.tensor.nrm.FrobeniusNorm;
import ch.alpine.tensor.pdf.Distribution;
import ch.alpine.tensor.pdf.RandomVariate;
import ch.alpine.tensor.pdf.d.DiscreteUniformDistribution;
import ch.alpine.tensor.sca.Round;

class ExportHelperTest {
  @TempDir
  Path tempDir;

  @Test
  void testGif() throws IOException {
    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream(128);
    Tensor image = Tensors.fromString("{{{255, 2, 3, 255}, {0, 0, 0, 0}, {91, 120, 230, 255}, {0, 0, 0, 0}}}");
    ExportHelper.of(Extension.GIF, image, byteArrayOutputStream);
    Path path = tempDir.resolve("file.gif");
    Export.of(path, image);
    assertTrue(Files.isRegularFile(path));
    Files.delete(path);
    byte[] array = byteArrayOutputStream.toByteArray(); // 54 bytes used
    BufferedImage bufferedImage = ImageIO.read(new ByteArrayInputStream(array));
    Tensor tensor = ImageFormat.from(bufferedImage);
    assertEquals(image, tensor);
  }

  @Test
  void testGif2() throws IOException {
    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream(128);
    Tensor row1 = Tensors.fromString("{{255, 2, 3, 255}, {0, 0, 0, 0}, {91, 120, 230, 255}, {0, 0, 0, 0}}");
    Tensor image = Tensors.of(row1, row1);
    ExportHelper.of(Extension.GIF, image, byteArrayOutputStream);
    byte[] array = byteArrayOutputStream.toByteArray(); // 56 bytes used
    BufferedImage bufferedImage = ImageIO.read(new ByteArrayInputStream(array));
    Tensor tensor = ImageFormat.from(bufferedImage);
    Scalar diff = FrobeniusNorm.of(image.subtract(tensor));
    diff.copy();
    // unfortunately there seems to be a problem with the java gif parser
  }

  @Test
  void testFileExtensionFail() throws IOException {
    OutputStream outputStream = new ByteArrayOutputStream(512);
    ExportHelper.of(Extension.VECTOR, Tensors.empty(), outputStream);
  }

  @Test
  void testGzFail() {
    OutputStream outputStream = new ByteArrayOutputStream(512);
    assertThrows(Exception.class, () -> ExportHelper.of(Extension.GZ, Tensors.empty(), outputStream));
  }

  @Test
  void testSomeGz() throws IOException {
    Path path = tempDir.resolve("some.unknown.gz");
    assertThrows(Exception.class, () -> Export.of(path, Array.zeros(4, 4, 4)));
    assertEquals(Files.list(tempDir).toList().size(), 0);
  }

  @Test
  void testGzGz() throws IOException {
    Path path = tempDir.resolve("some.gz.gz");
    assertThrows(Exception.class, () -> Export.of(path, Array.zeros(4, 4, 4)));
    assertEquals(Files.list(tempDir).toList().size(), 0);
  }

  static Tensor _readRGBA() throws IOException {
    Path path = Unprotect.resourcePath("/ch/alpine/tensor/img/rgba15x33.png");
    assertTrue(Files.isRegularFile(path));
    BufferedImage bufferedImage = null;
    try (InputStream inputStream = Files.newInputStream(path)) {
      bufferedImage = ImageIO.read(inputStream);
    }
    return ImageFormat.from(bufferedImage);
  }

  @Test
  void testRGBAFile() throws Exception {
    Tensor tensor = _readRGBA();
    assertEquals(tensor.get(19, 12), Tensors.vector(118, 130, 146, 200));
    assertEquals(tensor.get(0, 14), Tensors.vector(254, 0, 0, 255)); // almost red, fe0000
    assertEquals(Dimensions.of(tensor), Arrays.asList(33, 15, 4));
  }

  @Test
  void testGrayFile() throws Exception {
    Path path = Unprotect.resourcePath("/ch/alpine/tensor/img/gray15x9.png");
    assertTrue(Files.isReadable(path));
    BufferedImage bufferedImage = null;
    try (InputStream inputStream = Files.newInputStream(path)) {
      bufferedImage = ImageIO.read(inputStream);
    }
    Tensor tensor = ImageFormat.from(bufferedImage);
    // confirmed with gimp
    assertEquals(tensor.Get(0, 2), RealScalar.of(175));
    assertEquals(tensor.Get(1, 2), RealScalar.of(109));
    assertEquals(tensor.Get(2, 2), RealScalar.of(94));
    assertEquals(Dimensions.of(tensor), Arrays.asList(9, 15));
  }

  @Test
  void testGrayJpg() throws Exception {
    Path path = Unprotect.resourcePath("/ch/alpine/tensor/img/gray15x9.jpg");
    assertTrue(Files.isRegularFile(path));
    BufferedImage bufferedImage = null;
    try (InputStream inputStream = Files.newInputStream(path)) {
      bufferedImage = ImageIO.read(inputStream);
    }
    Tensor tensor = ImageFormat.from(bufferedImage);
    // confirmed with gimp
    assertEquals(tensor.Get(0, 2), RealScalar.of(84));
    assertEquals(tensor.Get(1, 2), RealScalar.of(66));
    assertEquals(tensor.Get(2, 2), RealScalar.of(39));
    assertEquals(tensor.Get(0, 14), RealScalar.ZERO);
    assertEquals(Dimensions.of(tensor), Arrays.asList(9, 15));
  }

  @Test
  void testGrayJpg1() throws Exception {
    Path path = Unprotect.resourcePath("/ch/alpine/tensor/img/gray15x9.jpg");
    assertTrue(Files.isRegularFile(path));
    Tensor tensor = Import.of(path);
    assertEquals(Dimensions.of(tensor), List.of(9, 15));
    Path target = tempDir.resolve("grayscale.jpg");
    Export.of(target, tensor);
    Tensor result = Import.of(target);
    assertEquals(Dimensions.of(result), List.of(9, 15));
  }

  @ParameterizedTest
  @ValueSource(strings = { "jpg", "png" })
  void testGray(String ext) throws Exception {
    Path path = Unprotect.resourcePath("/ch/alpine/tensor/img/gray5x3." + ext);
    assertTrue(Files.isRegularFile(path));
    Tensor tensor = Import.of(path);
    assertEquals(Dimensions.of(tensor), List.of(5, 3));
  }

  @Test
  void testGrayBimap1() {
    Tensor scale = Array.of(list -> RealScalar.of(list.get(0)), 256, 20);
    assertEquals(scale, ImageFormat.from(ImageFormat.of(scale)));
  }

  @Test
  void testGrayBimap2() {
    Distribution distribution = DiscreteUniformDistribution.forArray(256);
    Tensor image = RandomVariate.of(distribution, 20, 30);
    Tensor bimap = ImageFormat.from(ImageFormat.of(image));
    assertEquals(image, bimap);
  }

  @Test
  void testRGBAConvert() throws Exception {
    Path path = Unprotect.resourcePath("/ch/alpine/tensor/img/rgba15x33.png");
    BufferedImage bufferedImage = null;
    try (InputStream inputStream = Files.newInputStream(path)) {
      bufferedImage = ImageIO.read(inputStream);
    }
    Tensor tensor = ImageFormat.from(bufferedImage);
    assertEquals(tensor, ImageFormat.from(ImageFormat.of(tensor)));
  }

  @Test
  void testRGBASmooth() throws Exception {
    Path path = Unprotect.resourcePath("/ch/alpine/tensor/img/rgba15x33.png");
    BufferedImage bufferedImage = null;
    try (InputStream inputStream = Files.newInputStream(path)) {
      bufferedImage = ImageIO.read(inputStream);
    }
    Tensor tensor = ImageFormat.from(bufferedImage);
    Tensor kernel = Array.of(_ -> Rational.of(1, 6), 3, 2, 1);
    Tensor array = ListConvolve.of(kernel, tensor);
    ImageFormat.of(array); // succeeds
  }

  @Test
  void testRGBAInvalid() throws Exception {
    Path path = Unprotect.resourcePath("/ch/alpine/tensor/img/rgba15x33.png");
    BufferedImage bufferedImage = null;
    try (InputStream inputStream = Files.newInputStream(path)) {
      bufferedImage = ImageIO.read(inputStream);
    }
    Tensor tensor = ImageFormat.from(bufferedImage);
    Tensor kernel = Array.of(_ -> Rational.of(1, 1), 3, 5, 1);
    Tensor array = ListConvolve.of(kernel, tensor);
    assertThrows(IllegalArgumentException.class, () -> ImageFormat.of(array));
  }

  private static Tensor _gradients() {
    Tensor arr = Array.of(list -> RealScalar.of(list.get(1)), 3, 11);
    Tensor image = Tensors.empty();
    for (ScalarTensorFunction cdf : ColorDataGradients.values())
      image.append(Raster.of(arr, cdf));
    image = Flatten.of(image, 1);
    image = Transpose.of(image, 1, 0, 2);
    return image.maps(Round.FUNCTION);
  }

  @Test
  void testColorBimap() {
    Tensor scale = _gradients();
    assertEquals(scale, ImageFormat.from(ImageFormat.of(scale)));
  }

  @Test
  void testVisibility() {
    assertFalse(Modifier.isPublic(ExportHelper.class.getModifiers()));
  }
}
