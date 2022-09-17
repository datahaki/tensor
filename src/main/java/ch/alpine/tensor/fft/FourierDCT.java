// code by jph
package ch.alpine.tensor.fft;

import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.alg.VectorQ;

/** <p>inspired by
 * <a href="https://reference.wolfram.com/language/ref/FourierDCT.html">FourierDCT</a> */
public enum FourierDCT {
  ;
  /** @param vector
   * @return */
  public static Tensor of(Tensor vector) {
    return VectorQ.require(vector).dot(FourierDCTMatrix._2.of(vector.length()));
  }
}
