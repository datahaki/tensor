// code by jph
package ch.alpine.tensor.nrm;

import ch.alpine.tensor.Rational;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.alg.ConstantArray;

public enum AveragingWeights {
  ;
  /** @param n
   * @return vector of length n with entries {1/n, 1/n, ..., 1/n}
   * @see NormalizeTotal */
  public static Tensor of(int n) {
    return ConstantArray.of(Rational.of(1, n), n);
  }
}
