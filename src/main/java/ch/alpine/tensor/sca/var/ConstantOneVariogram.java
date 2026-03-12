// code by jph
package ch.alpine.tensor.sca.var;

import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.api.ScalarUnaryOperator;
import ch.alpine.tensor.sca.Sign;

public enum ConstantOneVariogram implements ScalarUnaryOperator {
  INSTANCE;

  @Override
  public Scalar apply(Scalar r) {
    Sign.requirePositiveOrZero(r);
    return RealScalar.ONE;
  }
}
