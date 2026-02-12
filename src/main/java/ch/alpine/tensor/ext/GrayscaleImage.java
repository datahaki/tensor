// code adapted from chatgpt
package ch.alpine.tensor.ext;

import java.awt.Transparency;
import java.awt.color.ColorSpace;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.ComponentColorModel;
import java.awt.image.DataBuffer;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;

/** grayscale image with alpha channel */
public enum GrayscaleImage {
  ;
  /** @param width
   * @param height
   * @return */
  public static BufferedImage of(int width, int height) {
    ColorSpace colorSpace = ColorSpace.getInstance(ColorSpace.CS_GRAY);
    int[] nBits = { 8, 8 }; // gray + alpha
    ColorModel colorModel = new ComponentColorModel(colorSpace, nBits, true, // has alpha
        false, Transparency.TRANSLUCENT, DataBuffer.TYPE_BYTE);
    WritableRaster writableRaster = Raster.createInterleavedRaster( //
        DataBuffer.TYPE_BYTE, //
        width, height, //
        2, // gray + alpha
        null);
    return new BufferedImage(colorModel, writableRaster, false, null);
  }
}
