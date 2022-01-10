// code by jph
package ch.alpine.tensor.img;

import java.awt.image.BufferedImage;

/* package */ enum StaticHelper {
  ;
  /** @param bufferedImage
   * @return either byte_gray or int_argb */
  public static int type(BufferedImage bufferedImage) {
    return bufferedImage.getType() == BufferedImage.TYPE_BYTE_GRAY //
        ? BufferedImage.TYPE_BYTE_GRAY
        : BufferedImage.TYPE_INT_ARGB;
  }
}
