// code by jph
package ch.alpine.tensor.sca;

import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Scalars;
import ch.alpine.tensor.api.ScalarUnaryOperator;

/** <p>inspired by
 * <a href="https://reference.wolfram.com/language/ref/TriangleWave.html">TriangleWave</a> */
public enum TriangleWave implements ScalarUnaryOperator {
  FUNCTION;

  private final Scalar _4 = RealScalar.of(4);
  private final Mod mod = Mod.function(4, -1);

  @Override
  public Scalar apply(Scalar t) {
    Scalar x = mod.apply(t.multiply(_4));
    return Scalars.lessThan(x, RealScalar.ONE) //
        ? x
        : RealScalar.TWO.subtract(x);
  }
}
