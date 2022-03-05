// code by jph
package ch.alpine.tensor.num;

import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Scalars;
import ch.alpine.tensor.api.ScalarUnaryOperator;
import ch.alpine.tensor.sca.exp.Log;

/** -p Log[p] with continuation at p == 0 */
public enum ShannonEntropy implements ScalarUnaryOperator {
  FUNCTION;

  @Override
  public Scalar apply(Scalar scalar) {
    return Scalars.isZero(scalar) //
        ? scalar
        : scalar.multiply(Log.FUNCTION.apply(scalar)).negate();
  }
}
