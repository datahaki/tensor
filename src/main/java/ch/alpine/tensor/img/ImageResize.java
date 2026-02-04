// code by jph
package ch.alpine.tensor.img;

import java.awt.Dimension;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
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
import ch.alpine.tensor.sca.Round;

/** <p>inspired by
 * <a href="https://reference.wolfram.com/language/ref/ImageResize.html">ImageResize</a>
 * 
 * @see MappedInterpolation */
public enum ImageResize {
  ;
  /** Careful: even if given width and height are identical to the dimensions of
   * given bufferedImage, the color values of the pixels in the return image may
   * be different from the original! In particular, it was observed that repeated,
   * i.e. iterative resizing causes grayscale images to converge to black and white
   * palette. This effect is regardless of the rendering hint used.
   * 
   * the function is particularly suitable for down-sizing a given image to a smaller
   * resolution. The implementation uses the SCALE_AREA_AVERAGING algorithm.
   * 
   * Careful: width and height may be constrained to be less equal to {@link Short#MAX_VALUE}
   * 
   * @param bufferedImage
   * @param width of rescaled image
   * @param height of rescaled image
   * @param interpolationType {@link AffineTransformOp#TYPE_NEAREST_NEIGHBOR} etc.
   * @return scaled instance of given buffered image with given dimensions */
  public static BufferedImage of(BufferedImage bufferedImage, int width, int height, int interpolationType) {
    AffineTransform affineTransform = AffineTransform.getScaleInstance( //
        width / (double) bufferedImage.getWidth(), //
        height / (double) bufferedImage.getHeight());
    return new AffineTransformOp(affineTransform, interpolationType).filter( //
        bufferedImage, //
        new BufferedImage(width, height, bufferedImage.getType()));
  }

  /** Careful: width and height may be constrained to be less equal to {@link Short#MAX_VALUE}
   * 
   * @param tensor of rank 2 or 3
   * @param height of image
   * @param width of image
   * @return */
  public static Tensor of(Tensor tensor, int height, int width) {
    return ImageFormat.from(of(ImageFormat.of(tensor), width, height, AffineTransformOp.TYPE_BILINEAR));
  }

  /** Remark: for a factor of one the width and height of the image remain identical
   * 
   * @param tensor of rank 2 or 3
   * @param factor
   * @return image scaled by given factor */
  public static Tensor of(Tensor tensor, Scalar factor) {
    List<Integer> list = Dimensions.of(tensor);
    return of(tensor, //
        Round.intValueExact(RealScalar.of(list.get(0)).multiply(factor)), //
        Round.intValueExact(RealScalar.of(list.get(1)).multiply(factor)));
  }

  /** @param tensor of rank 2 or 3
   * @param dimension
   * @return tensor that is a resized version of given tensor to given dimension */
  public static Tensor of(Tensor tensor, Dimension dimension) {
    return of(tensor, dimension.height, dimension.width);
  }

  /** function uses nearest neighbor interpolation
   * 
   * @param tensor of rank 2 or 3
   * @param factor positive integer
   * @return */
  public static Tensor nearest(Tensor tensor, int factor) {
    return nearest(tensor, factor, factor);
  }

  /** function uses nearest neighbor interpolation
   * 
   * @param tensor of rank 2 or 3
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
