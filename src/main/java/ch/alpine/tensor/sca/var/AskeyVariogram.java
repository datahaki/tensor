// code by jph
package ch.alpine.tensor.sca.var;

import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Scalars;
import ch.alpine.tensor.Throw;
import ch.alpine.tensor.api.ScalarUnaryOperator;
import ch.alpine.tensor.sca.pow.Power;

/** inspired by
 * <a href="https://reference.wolfram.com/language/ref/VariogramModel.html">VariogramModel</a> */
public record AskeyVariogram(Scalar a, Scalar b, Scalar c) implements ScalarUnaryOperator {
  @Override
  public Scalar apply(Scalar r) {
    if (Scalars.lessEquals(b, r))
      return a;
    if (Scalars.lessEquals(b.zero(), r))
      return RealScalar.ONE.subtract(Power.of(RealScalar.ONE.subtract(r.divide(b)), c)).multiply(a);
    throw new Throw(r);
  }
}
