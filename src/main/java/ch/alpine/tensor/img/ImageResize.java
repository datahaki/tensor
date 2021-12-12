// code by jph
package ch.alpine.tensor.img;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.util.List;
import java.util.stream.IntStream;

import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.Unprotect;
import ch.alpine.tensor.alg.Dimensions;
import ch.alpine.tensor.ext.Integers;
import ch.alpine.tensor.io.ImageFormat;
import ch.alpine.tensor.itp.MappedInterpolation;
import ch.alpine.tensor.mat.MatrixQ;
import ch.alpine.tensor.sca.Round;

/** the general implementation {@link ImageResize#of(Tensor, Scalar)}
 * uses Image.SCALE_AREA_AVERAGING with emphasis on quality.
 * 
 * <p>inspired by
 * <a href="https://reference.wolfram.com/language/ref/ImageResize.html">ImageResize</a>
 * 
 * @see MappedInterpolation */
public enum ImageResize {
  ;
  /** @param tensor
   * @param dim0 height of image
   * @param dim1 width of image
   * @return */
  public static Tensor of(Tensor tensor, int dim0, int dim1) {
    boolean gray = MatrixQ.of(tensor);
    BufferedImage bufferedImage = new BufferedImage(dim1, dim0, gray //
        ? BufferedImage.TYPE_BYTE_GRAY
        : BufferedImage.TYPE_INT_ARGB);
    Graphics2D graphics = bufferedImage.createGraphics();
    Image image = ImageFormat.of(tensor).getScaledInstance(dim1, dim0, Image.SCALE_AREA_AVERAGING);
    graphics.drawImage(image, 0, 0, null);
    return ImageFormat.from(bufferedImage);
  }

  /** @param tensor
   * @param factor
   * @return */
  public static Tensor of(Tensor tensor, Scalar factor) {
    List<Integer> list = Dimensions.of(tensor);
    return of(tensor, //
        Round.intValueExact(RealScalar.of(list.get(0)).multiply(factor)), //
        Round.intValueExact(RealScalar.of(list.get(1)).multiply(factor)));
  }

  /** @param tensor
   * @param dimension
   * @return */
  public static Tensor of(Tensor tensor, Dimension dimension) {
    return of(tensor, dimension.height, dimension.width);
  }

  /** function uses nearest neighbor interpolation
   * 
   * @param tensor
   * @param factor positive integer
   * @return */
  public static Tensor nearest(Tensor tensor, int factor) {
    return nearest(tensor, factor, factor);
  }

  /** function uses nearest neighbor interpolation
   * 
   * @param tensor
   * @param fx positive scaling along x axis
   * @param fy positive scaling along y axis
   * @return
   * @throws Exception if either fx or fy is zero or negative */
  public static Tensor nearest(Tensor tensor, int fx, int fy) {
    int dim0 = tensor.length();
    int dim1 = Unprotect.dimension1(tensor);
    // precomputation of indices
    int[] ix = IntStream.range(0, dim0 * fx).map(i -> i / fx).toArray();
    int[] iy = IntStream.range(0, dim1 * fy).map(i -> i / fy).toArray();
    return Tensors.matrix((i, j) -> tensor.get(ix[i], iy[j]), //
        dim0 * Integers.requirePositive(fx), //
        dim1 * Integers.requirePositive(fy));
  }
}
