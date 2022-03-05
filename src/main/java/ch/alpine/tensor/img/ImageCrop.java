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
  public static TensorUnaryOperator color(Tensor value) {
    return new ImageCrop((Predicate<Tensor> & Serializable) value::equals);
  }

  // ---
  private final Predicate<Tensor> predicate;

  private ImageCrop(Predicate<Tensor> predicate) {
    this.predicate = predicate;
  }

  @Override
  public Tensor apply(Tensor image) {
    // TODO TENSOR IMG not as efficient as could be
    int dim0 = image.length();
    int dim1 = Unprotect.dimension1(image);
    Tensor boole = TensorMap.of(entry -> Boole.of(predicate.test(entry)), image, 2);
    Tensor vectorX = TensorMap.of(Total::of, boole, 0);
    Tensor vectorY = TensorMap.of(Total::of, boole, 1);
    Scalar dimS0 = RealScalar.of(dim0);
    Scalar dimS1 = RealScalar.of(dim1);
    OptionalInt xMin = IntStream.range(0, dim1) //
        .filter(index -> !vectorX.Get(index).equals(dimS0)).findFirst();
    OptionalInt xMax = IntStream.range(0, dim1) //
        .filter(index -> !vectorX.Get(dim1 - 1 - index).equals(dimS0)).findFirst();
    OptionalInt yMin = IntStream.range(0, dim0) //
        .filter(index -> !vectorY.Get(index).equals(dimS1)).findFirst();
    OptionalInt yMax = IntStream.range(0, dim0) //
        .filter(index -> !vectorY.Get(dim0 - 1 - index).equals(dimS1)).findFirst();
    int xmin = xMin.orElse(0);
    int xmax = dim1 - xMax.orElse(0);
    int skip = yMin.orElse(0);
    return Tensor.of(image.stream() //
        .skip(skip) //
        .limit(dim0 - yMax.orElse(0) - skip) //
        .map(row -> row.extract(xmin, xmax)));
  }
}
