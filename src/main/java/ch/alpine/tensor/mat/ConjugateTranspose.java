// code by jph
package ch.alpine.tensor.mat;

import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.alg.Transpose;
import ch.alpine.tensor.sca.Conjugate;

/** implementation consistent with Mathematica.
 * 
 * <p>inspired by
 * <a href="https://reference.wolfram.com/language/ref/ConjugateTranspose.html">ConjugateTranspose</a>
 * 
 * @see Conjugate
 * @see Transpose */
public enum ConjugateTranspose {
  ;
  /** @param tensor of rank at least 2
   * @return transpose of tensor with entries conjugated
   * @throws Exception if given tensor is not of rank at least 2 */
  public static Tensor of(Tensor tensor) {
    return Transpose.of(tensor).map(Conjugate.FUNCTION);
  }
}
