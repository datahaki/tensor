// code by jph
package ch.alpine.tensor.img;

import java.awt.image.BufferedImage;

/* package */ enum StaticHelper {
  ;
  /** @param type
   * @return either byte_gray or int_argb */
  public static int type(int type) {
    return type == BufferedImage.TYPE_BYTE_GRAY //
        ? BufferedImage.TYPE_BYTE_GRAY
        : BufferedImage.TYPE_INT_ARGB;
  }
}
