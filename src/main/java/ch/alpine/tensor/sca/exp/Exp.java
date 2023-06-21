// code by jph
package ch.alpine.tensor.sca.exp;

import ch.alpine.tensor.ComplexScalar;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Throw;
import ch.alpine.tensor.api.ScalarUnaryOperator;

/** gives the exponential of a {@link Scalar} that implements {@link ExpInterface}.
 * Supported types include {@link RealScalar}, and {@link ComplexScalar}.
 * 
 * <p>inspired by
 * <a href="https://reference.wolfram.com/language/ref/Exp.html">Exp</a>
 * 
 * @see ExpInterface
 * @see Log */
public enum Exp implements ScalarUnaryOperator {
  FUNCTION;

  @Override
  public Scalar apply(Scalar scalar) {
    if (scalar instanceof ExpInterface expInterface)
      return expInterface.exp();
    throw new Throw(scalar);
  }
}
