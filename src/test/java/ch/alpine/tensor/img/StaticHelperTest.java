// code by jph
package ch.alpine.tensor.img;

import java.awt.image.BufferedImage;

import junit.framework.TestCase;

public class StaticHelperTest extends TestCase {
  public void testSimple() {
    assertEquals(StaticHelper.type(BufferedImage.TYPE_3BYTE_BGR), BufferedImage.TYPE_INT_ARGB);
    assertEquals(StaticHelper.type(BufferedImage.TYPE_INT_ARGB), BufferedImage.TYPE_INT_ARGB);
    assertEquals(StaticHelper.type(BufferedImage.TYPE_BYTE_GRAY), BufferedImage.TYPE_BYTE_GRAY);
  }
}
