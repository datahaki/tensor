// code by jph
package ch.alpine.tensor.fft;

import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.alg.Reverse;
import ch.alpine.tensor.api.TensorUnaryOperator;

/** The tensor library permits convolution with a kernel of lower rank than tensor.
 * This is unlike in Mathematica.
 * 
 * <p>One application of {@link FullConvolve} is the computation of the coefficients
 * of the product of two polynomials.
 * 
 * <p>inspired by
 * <a href="https://reference.wolfram.com/language/ref/ListConvolve.html">ListConvolve</a>
 * 
 * @see FullCorrelate */
public enum FullConvolve {
  ;
  /** <pre>
   * FullConvolve[{x, y}, {a, b, c, d, e, f}] ==
   * {a x, b x + a y, c x + b y, d x + c y, e x + d y, f x + e y, f y}
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
    return FullCorrelate.with(Reverse.all(kernel));
  }
}
