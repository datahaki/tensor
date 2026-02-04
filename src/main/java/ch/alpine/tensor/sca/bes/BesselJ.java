// adapted from colt by jph
package ch.alpine.tensor.sca.bes;

import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Scalars;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.red.Times;
import ch.alpine.tensor.sca.Abs;
import ch.alpine.tensor.sca.Sign;
import ch.alpine.tensor.sca.ply.Polynomial;
import ch.alpine.tensor.sca.pow.Sqrt;
import ch.alpine.tensor.sca.tri.Cos;
import ch.alpine.tensor.sca.tri.Sin;

/** inspired by
 * <a href="https://reference.wolfram.com/language/ref/BesselJ.html">BesselJ</a> */
public enum BesselJ {
  ;
  static final Scalar ACC = RealScalar.of(40.0);
  static final Scalar BIGNO = RealScalar.of(1.0e+10);
  static final Scalar BIGNI = RealScalar.of(1.0e-10);

  /** Returns the Bessel function of the first kind of order <tt>n</tt> of the argument.
   * 
   * @param n the order of the Bessel function.
   * @param x the value to compute the bessel function of. */
  public static Scalar of(int n, Scalar x) {
    int j, m;
    Scalar bj, bjm, bjp, sum, tox, ans;
    boolean jsum;
    if (n == 0)
      return _0(x);
    if (n == 1)
      return _1(x);
    if (Scalars.isZero(x))
      return RealScalar.ZERO;
    Scalar ax = Abs.FUNCTION.apply(x);
    if (Scalars.lessThan(RealScalar.of(n), ax)) {
      tox = RealScalar.of(2.0).divide(ax);
      bjm = _0(ax);
      bj = _1(ax);
      for (j = 1; j < n; j++) {
        bjp = RealScalar.of(j).multiply(tox).multiply(bj).subtract(bjm);
        bjm = bj;
        bj = bjp;
      }
      ans = bj;
    } else {
      tox = RealScalar.of(2.0).divide(ax);
      Scalar sqrt = Sqrt.FUNCTION.apply(ACC.multiply(RealScalar.of(n)));
      m = 2 * ((n + sqrt.number().intValue()) / 2);
      jsum = false;
      bjp = ans = sum = RealScalar.ZERO;
      bj = RealScalar.of(1.0);
      for (j = m; j > 0; j--) {
        bjm = RealScalar.of(j).multiply(tox).multiply(bj).subtract(bjp);
        bjp = bj;
        bj = bjm;
        if (Scalars.lessThan(BIGNO, Abs.FUNCTION.apply(bj))) {
          bj = bj.multiply(BIGNI);
          bjp = bjp.multiply(BIGNI);
          ans = ans.multiply(BIGNI);
          sum = sum.multiply(BIGNI);
        }
        if (jsum)
          sum = sum.add(bj);
        jsum = !jsum;
        if (j == n)
          ans = bjp;
      }
      sum = sum.add(sum).subtract(bj);
      ans = ans.divide(sum);
    }
    return Sign.isNegative(x) && n % 2 == 1 //
        ? ans.negate()
        : ans;
  }

  public static Scalar of(Scalar n, Scalar x) {
    return of(Scalars.intValueExact(n), x);
  }

  public static Scalar of(Number n, Number x) {
    return of(RealScalar.of(n), RealScalar.of(x));
  }

  /** Returns the Bessel function of the first kind of order 0 of the argument.
   * 
   * @param x the value to compute the bessel function of. */
  public static Scalar _0(Scalar x) {
    Scalar ax = Abs.FUNCTION.apply(x);
    if (Scalars.lessThan(ax, RealScalar.of(8))) {
      Polynomial num = Polynomial.of(Tensors.vector( //
          57568490574.0, -13362590354.0, 651619640.7, -11214424.18, 77392.33017, -184.9052456));
      Polynomial den = Polynomial.of(Tensors.vector( //
          57568490411.0, 1029532985.0, 9494680.718, 59272.64853, 267.8532712, 1.0));
      Scalar y = x.multiply(x);
      return num.apply(y).divide(den.apply(y));
    }
    Scalar z = RealScalar.of(8).divide(ax);
    Polynomial num = Polynomial.of(Tensors.vector( //
        1.0, -0.1098628627e-2, 0.2734510407e-4, -0.2073370639e-5, 0.2093887211e-6));
    Polynomial den = Polynomial.of(Tensors.vector( //
        -0.1562499995e-1, 0.1430488765e-3, -0.6911147651e-5, 0.7621095161e-6, -0.934935152e-7));
    Scalar y = z.multiply(z);
    Scalar ans1 = num.apply(y);
    Scalar ans2 = den.apply(y);
    Scalar xx = ax.add(RealScalar.of(-0.785398164));
    return Sqrt.FUNCTION.apply(RealScalar.of(0.636619772).divide(ax)).multiply( //
        Cos.FUNCTION.apply(xx).multiply(ans1).subtract(Times.of(z, Sin.FUNCTION.apply(xx), ans2)));
  }

  public static Scalar _0(Number x) {
    return _0(RealScalar.of(x));
  }

  /** Returns the Bessel function of the first kind of order 1 of the argument.
   * 
   * @param x the value to compute the bessel function of. */
  public static Scalar _1(Scalar x) {
    Scalar ax = Abs.FUNCTION.apply(x);
    Scalar y;
    Scalar ans1;
    Scalar ans2;
    if (Scalars.lessThan(ax, RealScalar.of(8))) {
      y = x.multiply(x);
      Polynomial num = Polynomial.of(Tensors.vector( //
          72362614232.0, -7895059235.0, 242396853.1, -2972611.439, 15704.48260, -30.16036606));
      ans1 = num.apply(y).multiply(x);
      Polynomial den = Polynomial.of(Tensors.vector( //
          144725228442.0, 2300535178.0, 18583304.74, 99447.43394, 376.9991397, 1.0));
      ans2 = den.apply(y);
      return ans1.divide(ans2);
    }
    Scalar z = RealScalar.of(8).divide(ax);
    Scalar xx = ax.add(RealScalar.of(-2.356194491));
    y = z.multiply(z);
    Polynomial num = Polynomial.of(Tensors.vector( //
        1.0, 0.183105e-2, -0.3516396496e-4, 0.2457520174e-5, -0.240337019e-6));
    ans1 = num.apply(y);
    Polynomial den = Polynomial.of(Tensors.vector( //
        0.04687499995, -0.2002690873e-3, 0.8449199096e-5, -0.88228987e-6, 0.105787412e-6));
    ans2 = den.apply(y);
    Scalar ans = Sqrt.FUNCTION.apply(RealScalar.of(0.636619772).divide(ax)).multiply( //
        Cos.FUNCTION.apply(xx).multiply(ans1).subtract(Times.of(z, Sin.FUNCTION.apply(xx), ans2)));
    if (Sign.isNegative(x))
      ans = ans.negate();
    return ans;
  }

  public static Scalar _1(Number x) {
    return _1(RealScalar.of(x));
  }
}
