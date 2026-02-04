// code by jph
package ch.alpine.tensor.num;

import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Scalars;
import ch.alpine.tensor.api.ScalarUnaryOperator;
import ch.alpine.tensor.sca.exp.Log;

/** -p Log[p] with continuation at p == 0 */
public enum ShannonEntropy implements ScalarUnaryOperator {
  FUNCTION;

  @Override
  public Scalar apply(Scalar scalar) {
    // TODO TENSOR no units involved
    return Scalars.isZero(scalar) //
        ? RealScalar.ZERO
        : scalar.multiply(Log.FUNCTION.apply(scalar)).negate();
  }

  /** @param p probability
   * @return entropy of a Boolean random variable that is true with probability p */
  public static Scalar booleanEntropy(Scalar p) {
    return FUNCTION.apply(p).add(FUNCTION.apply(RealScalar.ONE.subtract(p)));
  }
}
