// code by jph
package ch.alpine.tensor.sca.bes;

import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Scalars;

/** inspired by
 * <a href="https://reference.wolfram.com/language/ref/BesselI.html">BesselI</a> */
public enum BesselJ {
  ;
  public static Scalar of(Scalar n, Scalar x) {
    return RealScalar.of(Bessel.jn(Scalars.intValueExact(n), x.number().doubleValue()));
  }

  public static Scalar of(Number n, Number x) {
    return of(RealScalar.of(n), RealScalar.of(x));
  }

  public static Scalar _0(Scalar x) {
    return RealScalar.of(Bessel.j0(x.number().doubleValue()));
  }

  public static Scalar _0(Number x) {
    return _0(RealScalar.of(x));
  }

  public static Scalar _1(Scalar x) {
    return RealScalar.of(Bessel.j1(x.number().doubleValue()));
  }

  public static Scalar _1(Number x) {
    return _1(RealScalar.of(x));
  }
}
