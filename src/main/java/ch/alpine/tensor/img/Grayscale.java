// code adapted from chatgpt
package ch.alpine.tensor.img;

import java.awt.Transparency;
import java.awt.color.ColorSpace;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.ComponentColorModel;
import java.awt.image.DataBuffer;
import java.awt.image.WritableRaster;

public enum Grayscale {
  ;
  // TODO
  public static void asd() {
    int width = 100;
    int height = 200;
    ColorSpace cs = ColorSpace.getInstance(ColorSpace.CS_GRAY);
    int[] nBits = { 8, 8 }; // gray + alpha
    ColorModel cm = new ComponentColorModel(cs, nBits, true, // has alpha
        false, Transparency.TRANSLUCENT, DataBuffer.TYPE_BYTE);
    WritableRaster raster = java.awt.image.Raster.createInterleavedRaster( //
        DataBuffer.TYPE_BYTE, width, height, 2, // gray + alpha
        null);
    BufferedImage image = new BufferedImage(cm, raster, false, null);
  }
}
