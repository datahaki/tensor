// code by jph
package ch.alpine.tensor.num;

import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Scalars;
import ch.alpine.tensor.api.ScalarUnaryOperator;
import ch.alpine.tensor.sca.exp.Log;

/** The Shannon Entropy function is used to measure the uncertainty, randomness,
 * or information content of a dataset or probability distribution.
 * 
 * It is based on the information theory developed by Claude Shannon.
 * 
 * -p Log[p] with continuation at p == 0 */
public enum ShannonEntropy implements ScalarUnaryOperator {
  BITS(Log.base(2)),
  NATS(Log.FUNCTION);

  private final ScalarUnaryOperator suo;

  ShannonEntropy(ScalarUnaryOperator suo) {
    this.suo = suo;
  }

  @Override
  public Scalar apply(Scalar scalar) {
    return Scalars.isZero(scalar) //
        ? RealScalar.ZERO
        : scalar.multiply(suo.apply(scalar)).negate();
  }

  /** @param p probability
   * @return entropy of a Boolean random variable that is true with probability p */
  public Scalar booleanEntropy(Scalar p) {
    return apply(p).add(apply(RealScalar.ONE.subtract(p)));
  }
}
