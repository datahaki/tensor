// code by jph
package ch.alpine.tensor.img;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;

import ch.alpine.tensor.RationalScalar;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.io.ImageFormat;

/** implementation deviates from Mathematica in case input is grayscale image
 * 
 * <p>inspired by
 * <a href="https://reference.wolfram.com/language/ref/Thumbnail.html">Thumbnail</a> */
public enum Thumbnail {
  ;
  /** @param tensor
   * @param size strictly positive
   * @return square image with dimensions size x size */
  public static Tensor of(Tensor tensor, int size) {
    return ImageFormat.from(of(ImageFormat.of(tensor), size));
  }

  /** @param bufferedImage
   * @param size
   * @return square image with dimensions size x size */
  public static BufferedImage of(BufferedImage bufferedImage, int size) {
    int w = bufferedImage.getWidth();
    int h = bufferedImage.getHeight();
    Scalar s = RealScalar.of(size);
    Scalar r = RationalScalar.of(w, h);
    Dimension dimension = h <= w //
        ? new Dimension(s.multiply(r).number().intValue(), size)
        : new Dimension(size, s.divide(r).number().intValue());
    BufferedImage result = new BufferedImage(size, size, StaticHelper.type(bufferedImage.getType()));
    Graphics2D graphics = result.createGraphics();
    // TODO TENSOR IMPL improve results
    Image image = bufferedImage.getScaledInstance(dimension.width, dimension.height, Image.SCALE_AREA_AVERAGING);
    graphics.drawImage(image, (size - dimension.width) / 2, (size - dimension.height) / 2, null);
    graphics.dispose();
    return result;
  }
}
