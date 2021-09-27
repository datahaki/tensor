// code by jph
package ch.alpine.tensor.fft;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.TensorRuntimeException;
import ch.alpine.tensor.Unprotect;
import ch.alpine.tensor.alg.Array;
import ch.alpine.tensor.alg.Dimensions;
import ch.alpine.tensor.api.TensorUnaryOperator;
import ch.alpine.tensor.ext.Integers;

/** The tensor library permits correlation with a kernel of lower rank than tensor.
 * This is unlike in Mathematica.
 * 
 * <p>inspired by
 * <a href="https://reference.wolfram.com/language/ref/ListCorrelate.html">ListCorrelate</a>
 * 
 * @see ListConvolve */
public class ListCorrelate implements TensorUnaryOperator {
  /** <pre>
   * ListCorrelate[{x, y}, {a, b, c, d, e, f}] ==
   * {a x + b y, b x + c y, c x + d y, d x + e y, e x + f y}
   * </pre>
   * 
   * @param kernel
   * @param tensor of the same rank as kernel
   * @return correlation of kernel with tensor
   * @throws Exception if rank of kernel exceeds rank of tensor, or their dimensions are
   * unsuitable for correlation, for instance if tensor is a {@link Scalar} */
  public static Tensor of(Tensor kernel, Tensor tensor) {
    return with(kernel).apply(tensor);
  }

  /** @param kernel
   * @return operator that performs correlation with given kernel on tensor input */
  public static TensorUnaryOperator with(Tensor kernel) {
    return new ListCorrelate(kernel);
  }

  // ---
  private final Tensor kernel;
  private final List<Integer> mask;
  private final int level;

  private ListCorrelate(Tensor kernel) {
    this.kernel = kernel;
    mask = Dimensions.of(kernel);
    level = mask.size() - 1;
  }

  @Override // from TensorUnaryOperator
  public Tensor apply(Tensor tensor) {
    List<Integer> size = Dimensions.of(tensor);
    if (size.size() <= level)
      throw TensorRuntimeException.of(kernel, tensor);
    List<Integer> dimensions = IntStream.range(0, mask.size()) //
        .map(index -> size.get(index) - mask.get(index) + 1) //
        .mapToObj(Integers::requirePositive) //
        .collect(Collectors.toList());
    Tensor refs = Unprotect.references(tensor);
    return Array.of(index -> kernel.pmul(refs.block(index, mask)).flatten(level) //
        .reduce(Tensor::add).orElseThrow(), dimensions);
  }

  @Override // from Object
  public String toString() {
    return String.format("%s[%s]", getClass().getSimpleName(), mask);
  }
}
