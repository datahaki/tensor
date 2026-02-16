// code by jph
package ch.alpine.tensor.sca;

import ch.alpine.tensor.Rational;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Scalars;
import ch.alpine.tensor.api.ScalarUnaryOperator;

/** <p>inspired by
 * <a href="https://reference.wolfram.com/language/ref/SquareWave.html">SquareWave</a> */
public enum SquareWave implements ScalarUnaryOperator {
  FUNCTION;

  private final Mod mod = Mod.function(1);
  private final Scalar minusOne = RealScalar.ONE.negate();

  @Override
  public Scalar apply(Scalar t) {
    return Scalars.lessThan(mod.apply(t), Rational.HALF) //
        ? RealScalar.ONE
        : minusOne;
  }
}
