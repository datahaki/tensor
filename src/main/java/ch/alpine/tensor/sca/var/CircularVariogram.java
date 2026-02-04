// code by jph
package ch.alpine.tensor.sca.var;

import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.api.ScalarUnaryOperator;
import ch.alpine.tensor.sca.Sign;

public record CircularVariogram(Scalar a, Scalar b) implements ScalarUnaryOperator {
  @Override
  public Scalar apply(Scalar r) {
    Sign.requirePositiveOrZero(r);
    return null;
  }
}
