// code by jph
package ch.alpine.tensor.fft;

import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.alg.VectorQ;

/** Careful:
 * MATHEMATICA CONVENTION !
 * FourierDCT[vector] == vector . FourierDCTMatrix */
public interface DiscreteFourierTransform {
  /** @param vector
   * @return matrix(vector.length()) . vector
   * @throws Exception if input is not a vector */
  default Tensor transform(Tensor vector) {
    return matrix(vector.length()).dot(VectorQ.require(vector));
  }

  /** @param n positive
   * @return square matrix of dimensions n x n */
  Tensor matrix(int n);

  DiscreteFourierTransform inverse();
}
