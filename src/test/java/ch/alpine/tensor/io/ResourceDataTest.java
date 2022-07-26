// code by jph
package ch.alpine.tensor.io;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.awt.image.BufferedImage;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.Scalars;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.alg.Dimensions;
import ch.alpine.tensor.itp.Interpolation;
import ch.alpine.tensor.itp.LinearInterpolation;

class ResourceDataTest {
  private static void _checkColorscheme(Interpolation interpolation) {
    assertThrows(IndexOutOfBoundsException.class, () -> interpolation.get(Tensors.vector(256)));
  }

  @Test
  void testColorschemeClassic() {
    Tensor tensor = ResourceData.of("/ch/alpine/tensor/img/colorscheme/classic.csv");
    assertNotNull(tensor);
    assertEquals(Dimensions.of(tensor), Arrays.asList(256, 4));
    Interpolation interpolation = LinearInterpolation.of(tensor);
    assertEquals(interpolation.get(Tensors.vector(255)), Tensors.vector(255, 237, 237, 255));
    _checkColorscheme(interpolation);
  }

  @Test
  void testHue() {
    Tensor tensor = ResourceData.of("/ch/alpine/tensor/img/colorscheme/_hue.csv");
    assertNotNull(tensor);
    assertEquals(Dimensions.of(tensor), Arrays.asList(7, 4));
    Interpolation interpolation = LinearInterpolation.of(tensor);
    assertEquals(interpolation.get(Tensors.vector(0)), Tensors.vector(255, 0, 0, 255));
    _checkColorscheme(interpolation);
  }

  @Test
  void testPrimes() {
    Tensor primes = ResourceData.of("/ch/alpine/tensor/num/primes.vector");
    List<Integer> dimensions = Dimensions.of(primes);
    assertEquals(dimensions.size(), 1);
    assertTrue(500 < dimensions.get(0));
    assertEquals(primes.Get(5), Scalars.fromString("13"));
  }

  @Test
  void testPrimesLines() {
    Tensor linesp = Tensor.of(ResourceData.lines("/ch/alpine/tensor/num/primes.vector").stream().map(Scalars::fromString));
    Tensor vector = ResourceData.of("/ch/alpine/tensor/num/primes.vector");
    assertEquals(linesp, vector);
  }

  @Test
  void testCsvGz() {
    Tensor actual = ResourceData.of("/ch/alpine/tensor/io/mathematica23.csv.gz");
    Tensor expected = Tensors.fromString("{{123/875+I, 9.3}, {-9, 5/8123123123123123, 1010101}}");
    assertEquals(expected, actual);
  }

  @Test
  void testBufferedImagePng() {
    BufferedImage bufferedImage = ResourceData.bufferedImage("/ch/alpine/tensor/img/rgba15x33.png");
    assertEquals(bufferedImage.getWidth(), 15);
    assertEquals(bufferedImage.getHeight(), 33);
    assertEquals(bufferedImage.getType(), BufferedImage.TYPE_4BYTE_ABGR);
  }

  @Test
  void testBufferedImageJpg() {
    BufferedImage bufferedImage = ResourceData.bufferedImage("/ch/alpine/tensor/img/rgb15x33.jpg");
    assertEquals(bufferedImage.getWidth(), 15);
    assertEquals(bufferedImage.getHeight(), 33);
    assertEquals(bufferedImage.getType(), BufferedImage.TYPE_3BYTE_BGR);
  }

  @Test
  void testJpg() {
    Tensor image = ResourceData.of("/ch/alpine/tensor/img/rgb15x33.jpg");
    assertEquals(Dimensions.of(image), Arrays.asList(33, 15, 4));
  }

  @Test
  void testBufferedImageBmp() {
    BufferedImage bufferedImage = ResourceData.bufferedImage("/ch/alpine/tensor/img/rgb7x11.bmp");
    assertEquals(bufferedImage.getWidth(), 7);
    assertEquals(bufferedImage.getHeight(), 11);
    assertEquals(bufferedImage.getType(), BufferedImage.TYPE_3BYTE_BGR);
  }

  @Test
  void testBufferedImageBmpNull() {
    assertThrows(RuntimeException.class, () -> ResourceData.bufferedImage("/doesnotexist.jpg"));
  }

  @Test
  void testBmp() {
    Tensor image = ResourceData.of("/ch/alpine/tensor/img/rgb7x11.bmp");
    assertEquals(Dimensions.of(image), Arrays.asList(11, 7, 4));
    assertEquals(image.get(10, 4), Tensors.vector(0, 7, 95, 255));
  }

  @Test
  void testFailNull() {
    assertThrows(RuntimeException.class, () -> ResourceData.of("/ch/alpine/tensor/number/exists.fail"));
    assertThrows(RuntimeException.class, () -> ResourceData.of("/ch/alpine/tensor/number/exists.fail.bmp"));
  }

  @Test
  void testObjectNull() {
    assertThrows(RuntimeException.class, () -> ResourceData.object("/ch/alpine/tensor/number/exists.fail"));
  }

  @Test
  void testPropertiesFailNull() {
    assertThrows(RuntimeException.class, () -> ResourceData.properties("/ch/alpine/tensor/number/exists.properties"));
  }

  @Test
  void testUnknownExtension() {
    assertThrows(RuntimeException.class, () -> ResourceData.of("/ch/alpine/tensor/io/extension.unknown"));
  }

  @Test
  void testCorruptContent() {
    assertThrows(RuntimeException.class, () -> ResourceData.of("/ch/alpine/tensor/io/corrupt.png"));
  }

  @Test
  void testLines() {
    List<String> lines = ResourceData.lines("/ch/alpine/tensor/io/basic.mathematica");
    assertEquals(lines.size(), 7);
  }

  @Test
  void testLinesNull() {
    assertThrows(RuntimeException.class, () -> ResourceData.lines("/ch/alpine/tensor/io/doesnotexist"));
  }
}
