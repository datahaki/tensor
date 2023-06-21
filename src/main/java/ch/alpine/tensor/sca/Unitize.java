// code by jph
package ch.alpine.tensor.sca;

import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Scalars;
import ch.alpine.tensor.api.ScalarUnaryOperator;
import ch.alpine.tensor.num.Boole;

/** maps a non-zero scalar to {@link RealScalar#ONE}, and a zero scalar to {@link RealScalar#ZERO}
 * 
 * <p>inspired by
 * <a href="https://reference.wolfram.com/language/ref/Unitize.html">Unitize</a> */
public enum Unitize implements ScalarUnaryOperator {
  FUNCTION;

  @Override
  public Scalar apply(Scalar scalar) {
    return Boole.of(Scalars.nonZero(scalar));
  }
}
