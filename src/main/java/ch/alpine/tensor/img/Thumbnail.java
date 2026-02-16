// code by jph
package ch.alpine.tensor.img;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;

import ch.alpine.tensor.Rational;
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
    return of(bufferedImage, size, false);
  }

  public static BufferedImage of(BufferedImage bufferedImage, int size, boolean rotate) {
    // TODO TENSOR CbbFit
    int w = bufferedImage.getWidth();
    int h = bufferedImage.getHeight();
    Scalar s = RealScalar.of(size);
    Scalar r = Rational.of(w, h);
    Dimension dimension = h <= w //
        ? new Dimension(s.multiply(r).number().intValue(), size)
        : new Dimension(size, s.divide(r).number().intValue());
    BufferedImage result = new BufferedImage(size, size, bufferedImage.getType());
    Graphics2D graphics = result.createGraphics();
    Image image = ImageResize.DEGREE_1.of(bufferedImage, dimension.width, dimension.height);
    graphics.drawImage(image, //
        rotate ? 0 : (size - dimension.width) / 2, //
        rotate ? (size - dimension.height) / 2 : 0, //
        null);
    graphics.dispose();
    return result;
  }
}
