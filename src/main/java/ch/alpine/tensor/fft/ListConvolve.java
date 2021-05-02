// code by jph
package ch.alpine.tensor.fft;

import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.alg.Reverse;
import ch.alpine.tensor.api.TensorUnaryOperator;

/** The tensor library permits convolution with a kernel of lower rank than tensor.
 * This is unlike in Mathematica.
 * 
 * <p>One application of {@link ListConvolve} is the computation of the coefficients
 * of the product of two polynomials.
 * 
 * <p>inspired by
 * <a href="https://reference.wolfram.com/language/ref/ListConvolve.html">ListConvolve</a>
 * 
 * @see ListCorrelate */
public enum ListConvolve {
  ;
  /** <pre>
   * ListConvolve[{x, y}, {a, b, c, d, e, f}] ==
   * {b x + a y, c x + b y, d x + c y, e x + d y, f x + e y}
   * </pre>
   * 
   * @param kernel
   * @param tensor of the same rank as kernel
   * @return convolution of kernel with tensor */
  public static Tensor of(Tensor kernel, Tensor tensor) {
    return with(kernel).apply(tensor);
  }

  /** @param kernel
   * @return operator that performs convolution with given kernel on tensor input */
  public static TensorUnaryOperator with(Tensor kernel) {
    return ListCorrelate.with(Reverse.all(kernel));
  }
}
