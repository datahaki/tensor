// code by jph
package ch.alpine.tensor.fft;

import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.alg.VectorQ;

/** The interface is compliant with Mathematica standards:
 * 
 * "The result of FourierDxTMatrix[n].list is equivalent to FourierDxT[list]
 * when list has length n. However, the computation of FourierDxT[list] is
 * much faster and has less numerical error." */
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

  /** @return */
  DiscreteFourierTransform inverse();
}
