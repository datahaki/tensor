// code by jph
package ch.alpine.tensor.num;

import java.math.BigInteger;

import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Throw;
import ch.alpine.tensor.ext.Cache;
import ch.alpine.tensor.sca.Abs;

/* package */ enum StaticHelper {
  ;
  /** stores sqrt roots of gauss scalars */
  public static final Cache<GaussScalar, GaussScalar> SQRT = Cache.of(StaticHelper::sqrt, 256 * 3);

  public static GaussScalar sqrt(GaussScalar gaussScalar) {
    for (BigInteger index = BigInteger.ZERO; //
        index.compareTo(gaussScalar.prime()) < 0; //
        index = index.add(BigInteger.ONE))
      if (index.multiply(index).mod(gaussScalar.prime()).equals(gaussScalar.number()))
        return GaussScalar.of(index, gaussScalar.prime());
    throw new Throw(gaussScalar);
  }

  // ---
  // TODO TENSOR NUM function does not result in Mathematica standard for all input
  public static Scalar normalForm(Scalar scalar) {
    return scalar instanceof RealScalar //
        ? Abs.FUNCTION.apply(scalar)
        : scalar;
  }
}
