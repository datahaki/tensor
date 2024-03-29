// code by jph
package ch.alpine.tensor.ext;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.awt.image.BufferedImage;
import java.util.List;

import org.junit.jupiter.api.Test;

class ResourceDataTest {
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
  void testObjectNull() {
    assertThrows(RuntimeException.class, () -> ResourceData.object("/ch/alpine/tensor/number/exists.fail"));
  }

  @Test
  void testPropertiesFailNull() {
    assertThrows(RuntimeException.class, () -> ResourceData.properties("/ch/alpine/tensor/number/exists.properties"));
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
