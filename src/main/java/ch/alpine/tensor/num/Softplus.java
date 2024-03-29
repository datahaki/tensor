// code by jph
package ch.alpine.tensor.num;

import ch.alpine.tensor.DoubleScalar;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Scalars;
import ch.alpine.tensor.api.ScalarUnaryOperator;
import ch.alpine.tensor.sca.Ramp;
import ch.alpine.tensor.sca.exp.Exp;
import ch.alpine.tensor.sca.exp.Log;

/** softplus is a non-negative smooth {@link Ramp}
 * 
 * <a href="https://en.wikipedia.org/wiki/Rectifier_(neural_networks)">documentation</a> */
public enum Softplus implements ScalarUnaryOperator {
  FUNCTION;

  private static final Scalar LO = DoubleScalar.of(-50);
  private static final Scalar HI = DoubleScalar.of(+50);

  @Override
  public Scalar apply(Scalar scalar) {
    if (Scalars.lessThan(HI, scalar))
      return scalar;
    if (Scalars.lessThan(scalar, LO))
      return scalar.zero();
    return Log.FUNCTION.apply(Exp.FUNCTION.apply(scalar).add(RealScalar.ONE));
  }
}
