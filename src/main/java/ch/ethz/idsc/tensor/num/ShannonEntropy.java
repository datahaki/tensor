// code by jph
package ch.ethz.idsc.tensor.num;

import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Scalars;
import ch.ethz.idsc.tensor.api.ScalarUnaryOperator;
import ch.ethz.idsc.tensor.sca.Log;

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
