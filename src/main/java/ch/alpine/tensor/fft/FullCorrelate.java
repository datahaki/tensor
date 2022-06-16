// code by jph
package ch.alpine.tensor.fft;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.TensorRuntimeException;
import ch.alpine.tensor.alg.Array;
import ch.alpine.tensor.alg.Dimensions;
import ch.alpine.tensor.api.TensorUnaryOperator;
import ch.alpine.tensor.chq.ScalarQ;
import ch.alpine.tensor.ext.Integers;
import ch.alpine.tensor.red.Times;

/** The tensor library permits correlation with a kernel of lower rank than tensor.
 * This is unlike in Mathematica.
 * 
 * @see FullConvolve
 * @see ListConvolve */
public class FullCorrelate implements TensorUnaryOperator {
  /** <pre>
   * FullCorrelate[{x, y}, {a, b, c, d, e, f}] ==
   * {a y, a x + b y, b x + c y, c x + d y, d x + e y, e x + f y, f x}
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
    return new FullCorrelate(kernel);
  }

  // ---
  private final Tensor kernel;
  private final List<Integer> mask;
  private final int level;

  private FullCorrelate(Tensor kernel) {
    ScalarQ.thenThrow(kernel);
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
        .map(index -> size.get(index) + mask.get(index) - 1) //
        .mapToObj(Integers::requirePositive) //
        .collect(Collectors.toList());
    return Array.of(index -> {
      List<Integer> kofs = IntStream.range(0, index.size()).mapToObj(i -> Math.max(0, mask.get(i) - 1 - index.get(i))).collect(Collectors.toList());
      List<Integer> tofs = IntStream.range(0, index.size()).mapToObj(i -> Math.max(0, index.get(i) - mask.get(i) + 1)).collect(Collectors.toList());
      List<Integer> widt = IntStream.range(0, index.size()) //
          .mapToObj(i -> Math.min(mask.get(i) - kofs.get(i), size.get(i) - tofs.get(i))).collect(Collectors.toList());
      return Times.of(kernel.block(kofs, widt), tensor.block(tofs, widt)).flatten(level) //
          .reduce(Tensor::add).orElseThrow();
    }, dimensions);
  }

  @Override // from Object
  public String toString() {
    return String.format("FullCorrelate[%s]", mask);
  }
}
