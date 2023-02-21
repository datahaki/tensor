// code by jph
package ch.alpine.tensor.fft;

import ch.alpine.tensor.Tensor;

public interface DiscreteFourierTransform {
  /** @param vector
   * @return depending on convention either
   * matrix(vector.length()) . vector, or alternatively
   * vector . matrix(vector.length()) */
  Tensor transform(Tensor vector);

  /** @param n positive
   * @return square matrix of dimensions n x n */
  Tensor matrix(int n);
}
