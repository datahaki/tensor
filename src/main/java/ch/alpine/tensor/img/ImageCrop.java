// code by jph
package ch.alpine.tensor.img;

import java.io.Serializable;
import java.util.OptionalInt;
import java.util.function.Predicate;
import java.util.stream.IntStream;

import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Unprotect;
import ch.alpine.tensor.alg.TensorMap;
import ch.alpine.tensor.api.TensorUnaryOperator;
import ch.alpine.tensor.num.Boole;
import ch.alpine.tensor.red.Total;

/** inspired by
 * <a href="https://reference.wolfram.com/language/ref/ImageCrop.html">ImageCrop</a> */
public class ImageCrop implements TensorUnaryOperator {
  /** for grayscale images given value should be a scalar
   * for RGBA images given value should be a vector of length 4
   * 
   * @param value
   * @return operator that removes the boundary of images of given color value */
  @SuppressWarnings("unchecked")
  public static TensorUnaryOperator eq(Tensor value) {
    return new ImageCrop((Predicate<Tensor> & Serializable) value::equals);
  }

  // ---
  private final Predicate<Tensor> predicate;

  private ImageCrop(Predicate<Tensor> predicate) {
    this.predicate = predicate;
  }

  @Override
  public Tensor apply(Tensor image) {
    // int depth = 2;
    // TODO TENSOR IMG not as efficient as could be
    int dim0 = image.length();
    final Tensor fimage = image;
    int d0lo = IntStream.range(0, dim0) //
        .filter(i -> !fimage.get(i).stream().allMatch(predicate)) //
        .findFirst() //
        .orElse(dim0);
    int d0hi = IntStream.range(d0lo, dim0) //
        .map(i -> dim0 - i - 1) //
        .filter(i -> !fimage.get(i).stream().allMatch(predicate)) //
        .findFirst() //
        .orElse(dim0);
    image = Tensor.of(image.stream().skip(d0lo).limit(d0hi - d0lo + 1));
    int dim1 = Unprotect.dimension1(image);
    Tensor boole = TensorMap.of(entry -> Boole.of(predicate.test(entry)), image, 2);
    Tensor vectorX = TensorMap.of(Total::of, boole, 0);
    int fdim0 = image.length();
    Scalar dimS0 = RealScalar.of(fdim0);
    OptionalInt d1min1 = IntStream.range(0, dim1) //
        .filter(index -> !vectorX.Get(index).equals(dimS0)).findFirst();
    OptionalInt d1max = IntStream.range(0, dim1) //
        .filter(index -> !vectorX.Get(dim1 - 1 - index).equals(dimS0)).findFirst();
    int xmin = d1min1.orElse(0);
    int xmax = dim1 - d1max.orElse(0);
    return Tensor.of(image.stream().map(row -> row.extract(xmin, xmax)));
  }
}
