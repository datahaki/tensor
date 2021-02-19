// code by jph
package ch.ethz.idsc.tensor.fft;

import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.api.TensorUnaryOperator;

/** The tensor library permits correlation with a kernel of lower rank than tensor.
 * This is unlike in Mathematica.
 * 
 * <p>inspired by
 * <a href="https://reference.wolfram.com/language/ref/ListCorrelate.html">ListCorrelate</a>
 * 
 * @see ListConvolve */
public enum ListCorrelate {
  ;
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
    return new ListCorrelateOperator(kernel);
  }
}
