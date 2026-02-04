// code by jph
package ch.alpine.tensor.nrm;

import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;

/** In the special case that either vector has norm 0, the return value is zero, for example
 * <pre>
 * CosineDistance[{0, 0}, {a, b}] == 0
 * </pre>
 * 
 * <p>inspired by
 * <a href="https://reference.wolfram.com/language/ref/CosineDistance.html">CosineDistance</a> */
public enum CosineDistance {
  ;
  /** @param u vector
   * @param v vector
   * @return */
  public static Scalar of(Tensor u, Tensor v) {
    return CosineSimilarity.of(u, v) //
        .map(RealScalar.ONE::subtract) // 1 - ratio
        .orElse(RealScalar.ZERO);
  }
}
