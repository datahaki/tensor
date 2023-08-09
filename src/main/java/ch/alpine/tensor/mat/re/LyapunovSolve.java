// code by jph
package ch.alpine.tensor.mat.re;

import ch.alpine.tensor.Tensor;

/** inspired by
 * <a href="https://reference.wolfram.com/language/ref/LyapunovSolve.html">LyapunovSolve</a> */
enum LyapunovSolve {
  ;
  public static Tensor of(Tensor a, Tensor b) {
    a.subtract(b);
    // TODO TENSOR IMPL
    throw new UnsupportedOperationException();
  }
}
