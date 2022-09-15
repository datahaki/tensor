// code by jph
package ch.alpine.tensor.img;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;
import java.util.OptionalInt;
import java.util.function.IntPredicate;
import java.util.function.Predicate;
import java.util.stream.IntStream;

import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.alg.TensorRank;
import ch.alpine.tensor.alg.Transpose;
import ch.alpine.tensor.api.TensorUnaryOperator;
import ch.alpine.tensor.ext.Integers;

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
    return new ImageCrop(2, (Predicate<Tensor> & Serializable) value::equals);
  }

  // ---
  private final int depth;
  private final int[] sigma;
  private final Predicate<Tensor> predicate;

  /** @param depth strictly positive of where to apply predicate,
   * for images, depth equals 2
   * @param predicate */
  public ImageCrop(int depth, Predicate<Tensor> predicate) {
    this.depth = depth;
    sigma = new int[depth];
    sigma[0] = depth - 1;
    for (int count = 1; count < depth; ++count)
      sigma[count] = count - 1;
    this.predicate = Objects.requireNonNull(predicate);
  }

  @Override
  public Tensor apply(Tensor tensor) {
    Integers.requireLessEquals(depth, TensorRank.of(tensor));
    int level = Math.max(0, depth - 2);
    for (int count = 0; count < depth; ++count) {
      Tensor ftensor = tensor;
      IntPredicate intPredicate = i -> !ftensor.get(i).flatten(level).allMatch(predicate);
      int length = tensor.length();
      OptionalInt optionalInt = IntStream.range(0, length).filter(intPredicate).findFirst();
      if (optionalInt.isEmpty())
        return Tensors.empty();
      int lo = optionalInt.getAsInt();
      int hi = IntStream.range(0, length - lo).map(i -> length - i - 1) //
          .filter(intPredicate).findFirst().getAsInt();
      tensor = Transpose.of(tensor.block(List.of(lo), List.of(hi - lo + 1)), sigma);
    }
    return tensor;
  }
}
