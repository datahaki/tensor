// code by jph
package ch.alpine.tensor.img;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.awt.image.BufferedImage;

import org.junit.jupiter.api.Test;

class StaticHelperTest {
  @Test
  void testSimple() {
    assertEquals(StaticHelper.type(BufferedImage.TYPE_3BYTE_BGR), BufferedImage.TYPE_INT_ARGB);
    assertEquals(StaticHelper.type(BufferedImage.TYPE_INT_ARGB), BufferedImage.TYPE_INT_ARGB);
    assertEquals(StaticHelper.type(BufferedImage.TYPE_BYTE_GRAY), BufferedImage.TYPE_BYTE_GRAY);
  }
}
