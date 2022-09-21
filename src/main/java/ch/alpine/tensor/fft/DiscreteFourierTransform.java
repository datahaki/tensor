// code by jph
package ch.alpine.tensor.fft;

import ch.alpine.tensor.Tensor;

public interface DiscreteFourierTransform {
  /** function evaluates fast for input of length equal to a power of 2
   * 
   * @param vector
   * @return */
  Tensor of(Tensor vector);

  /** @param n positive
   * @return square matrix of dimensions n x n */
  Tensor matrix(int n);
}
