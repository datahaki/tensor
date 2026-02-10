// code by jph
package ch.alpine.tensor.img;

import java.awt.image.BufferedImage;

import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.Unprotect;
import ch.alpine.tensor.api.TensorUnaryOperator;
import ch.alpine.tensor.ext.Integers;

/** inspired by
 * <a href="https://reference.wolfram.com/language/ref/ImageRotate.html">ImageRotate</a> */
public enum ImageRotate implements TensorUnaryOperator {
  CCW {
    /** @param tensor of rank at least 2
     * @return counter-clockwise rotation, i.e. +90 degree */
    @Override
    public Tensor apply(Tensor tensor) {
      int rows = tensor.length();
      int cols = cols(tensor);
      int r = cols - 1;
      return Tensors.matrix((i, j) -> tensor.get(j, r - i), cols, rows);
    }
  },
  CW {
    /** @param tensor of rank at least 2
     * @return clockwise rotation, i.e. -90 degree */
    @Override
    public Tensor apply(Tensor tensor) {
      int rows = tensor.length();
      int cols = cols(tensor);
      int r = rows - 1;
      return Tensors.matrix((i, j) -> tensor.get(r - j, i), cols, rows);
    }
  },
  _180 {
    /** @param tensor of rank at least 2
     * @return rotation by 180 degree */
    @Override
    public Tensor apply(Tensor tensor) {
      int rows = tensor.length();
      int cols = cols(tensor);
      int r = rows - 1;
      int s = cols - 1;
      return Tensors.matrix((i, j) -> tensor.get(r - i, s - j), rows, cols);
    }
  };

  private static int cols(Tensor tensor) {
    return Integers.requirePositive(Unprotect.dimension1(tensor));
  }

  /** @param bufferedImage
   * @return */
  public static BufferedImage cw(BufferedImage bufferedImage) {
    int width = bufferedImage.getWidth();
    int height = bufferedImage.getHeight();
    BufferedImage result = new BufferedImage(height, width, bufferedImage.getType());
    for (int i = 0; i < width; ++i)
      for (int j = 0; j < height; ++j)
        result.setRGB(height - 1 - j, i, bufferedImage.getRGB(i, j));
    return result;
  }
}
