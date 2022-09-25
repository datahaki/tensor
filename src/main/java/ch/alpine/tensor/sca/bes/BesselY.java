// adapted from colt by jph
package ch.alpine.tensor.sca.bes;

import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Scalars;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.red.Times;
import ch.alpine.tensor.sca.exp.Log;
import ch.alpine.tensor.sca.ply.Polynomial;
import ch.alpine.tensor.sca.pow.Sqrt;
import ch.alpine.tensor.sca.tri.Cos;
import ch.alpine.tensor.sca.tri.Sin;

/** inspired by
 * <a href="https://reference.wolfram.com/language/ref/BesselY.html">BesselY</a> */
public enum BesselY {
  ;
  /** Returns the Bessel function of the second kind of order <tt>n</tt> of the argument.
   * 
   * @param n the order of the Bessel function.
   * @param x the value to compute the bessel function of. */
  public static Scalar of(int n, Scalar x) {
    if (n == 0)
      return _0(x);
    if (n == 1)
      return _1(x);
    Scalar tox = RealScalar.of(2).divide(x);
    Scalar bym = _0(x);
    Scalar byn = _1(x);
    for (int j = 1; j < n; j++) {
      Scalar byp = RealScalar.of(j).multiply(tox).multiply(byn).subtract(bym);
      bym = byn;
      byn = byp;
    }
    return byn;
  }

  public static Scalar of(Scalar n, Scalar x) {
    return BesselY.of(Scalars.intValueExact(n), x);
  }

  public static Scalar of(Number n, Number x) {
    return of(RealScalar.of(n), RealScalar.of(x));
  }

  private static final Polynomial _00num = Polynomial.of(Tensors.vector( //
      -2957821389.0, 7062834065.0, -512359803.6, 10879881.29, -86327.92757, 228.4622733));
  private static final Polynomial _00den = Polynomial.of(Tensors.vector( //
      40076544269.0, 745249964.8, 7189466.438, 47447.26470, 226.1030244, 1.0));
  private static final Polynomial _01num = Polynomial.of(Tensors.vector( //
      1.0, -0.1098628627e-2, 0.2734510407e-4, -0.2073370639e-5, 0.2093887211e-6));
  private static final Polynomial _01den = Polynomial.of(Tensors.vector( //
      -0.1562499995e-1, 0.1430488765e-3, -0.6911147651e-5, 0.7621095161e-6, -0.934945152e-7));

  /** Returns the Bessel function of the second kind of order 0 of the argument.
   * 
   * @param x the value to compute the bessel function of. */
  public static Scalar _0(Scalar x) {
    // TODO TENSOR IMPL case x < 0 missing!
    if (Scalars.lessThan(x, RealScalar.of(8))) {
      Scalar y = x.multiply(x);
      Scalar ans1 = _00num.apply(y);
      Scalar ans2 = _00den.apply(y);
      return ans1.divide(ans2).add(Times.of(RealScalar.of(0.636619772), BesselJ._0(x), Log.FUNCTION.apply(x)));
    }
    Scalar z = RealScalar.of(8.0).divide(x);
    Scalar y = z.multiply(z);
    Scalar xx = x.add(RealScalar.of(-0.785398164));
    Scalar ans1 = _01num.apply(y);
    Scalar ans2 = _01den.apply(y);
    return Sqrt.FUNCTION.apply(RealScalar.of(0.636619772).divide(x)).multiply( //
        Sin.FUNCTION.apply(xx).multiply(ans1).add(Times.of(z, Cos.FUNCTION.apply(xx), ans2)));
  }

  public static Scalar _0(Number x) {
    return _0(RealScalar.of(x));
  }

  private static final Polynomial _10num = Polynomial.of(Tensors.vector( //
      -0.4900604943e13, 0.1275274390e13, -0.5153438139e11, 0.7349264551e9, -0.4237922726e7, 0.8511937935e4));
  private static final Polynomial _10den = Polynomial.of(Tensors.vector( //
      0.2499580570e14, 0.4244419664e12, 0.3733650367e10, 0.2245904002e8, 0.1020426050e6, 0.3549632885e3, 1));
  private static final Polynomial _11num = Polynomial.of(Tensors.vector( //
      1.0, 0.183105e-2, -0.3516396496e-4, 0.2457520174e-5, -0.240337019e-6));
  private static final Polynomial _11den = Polynomial.of(Tensors.vector( //
      0.04687499995, -0.2002690873e-3, 0.8449199096e-5, -0.88228987e-6, 0.105787412e-6));

  /** Returns the Bessel function of the second kind of order 1 of the argument.
   * 
   * @param x the value to compute the bessel function of. */
  public static Scalar _1(Scalar x) {
    // TODO TENSOR IMPL case x < 0 missing!
    if (Scalars.lessThan(x, RealScalar.of(8))) {
      Scalar y = x.multiply(x);
      Scalar ans1 = _10num.apply(y).multiply(x);
      Scalar ans2 = _10den.apply(y);
      return ans1.divide(ans2).add(RealScalar.of(0.636619772).multiply(BesselJ._1(x).multiply(Log.FUNCTION.apply(x)).subtract(x.reciprocal())));
    }
    Scalar z = RealScalar.of(8.0).divide(x);
    Scalar y = z.multiply(z);
    Scalar xx = x.add(RealScalar.of(-2.356194491));
    Scalar ans1 = _11num.apply(y);
    Scalar ans2 = _11den.apply(y);
    return Sqrt.FUNCTION.apply(RealScalar.of(0.636619772).divide(x)).multiply(//
        Sin.FUNCTION.apply(xx).multiply(ans1).add(Times.of(z, Cos.FUNCTION.apply(xx), ans2)));
  }

  public static Scalar _1(Number x) {
    return _1(RealScalar.of(x));
  }
}
