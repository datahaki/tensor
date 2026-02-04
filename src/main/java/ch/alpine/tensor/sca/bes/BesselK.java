/* Copyright 1999 CERN - European Organization for Nuclear Research.
 * Permission to use, copy, modify, distribute and sell this software and its documentation for any purpose
 * is hereby granted without fee, provided that the above copyright notice appear in all copies and
 * that both that copyright notice and this permission notice appear in supporting documentation.
 * CERN makes no representations about the suitability of this software for any purpose.
 * It is provided "as is" without expressed or implied warranty. */
// adapted from colt by jph
package ch.alpine.tensor.sca.bes;

import ch.alpine.tensor.ComplexScalar;
import ch.alpine.tensor.DoubleScalar;
import ch.alpine.tensor.RationalScalar;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Scalars;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.api.ScalarUnaryOperator;
import ch.alpine.tensor.sca.Clips;
import ch.alpine.tensor.sca.Sign;
import ch.alpine.tensor.sca.exp.Exp;
import ch.alpine.tensor.sca.exp.Log;
import ch.alpine.tensor.sca.gam.Gamma;
import ch.alpine.tensor.sca.ply.ClenshawChebyshev;
import ch.alpine.tensor.sca.pow.Sqrt;

/** inspired by
 * <a href="https://reference.wolfram.com/language/ref/BesselK.html">BesselK</a> */
public enum BesselK {
  ;
  public static Scalar of(Scalar n, Scalar x) {
    return RealScalar.of(kn(Scalars.intValueExact(n), x.number().doubleValue()));
  }

  public static Scalar of(Number n, Number x) {
    return of(RealScalar.of(n), RealScalar.of(x));
  }

  /* Chebyshev coefficients for K0(x) + log(x/2) I0(x)
   * in the interval [0,4]. The odd order coefficients are all
   * zero; only the even order coefficients are listed.
   * 
   * lim(x->0){ K0(x) + log(x/2) I0(x) } = -EUL. */
  // 10 elements
  private static final ScalarUnaryOperator A_k0 = ClenshawChebyshev.forward( //
      Clips.interval(0, 4), //
      Tensors.vectorDouble( //
          -0.2676636966169514, //
          0.3442898999246285, //
          0.0359799365153615, //
          0.001264615411446926, //
          2.286212103119452E-5, //
          2.5347910790261494E-7, //
          1.904516377220209E-9, //
          1.0349695257633842E-11, //
          4.25981614279661E-14, //
          1.374465435613523E-16 //
      ));
  /* Chebyshev coefficients for exp(x) sqrt(x) K0(x)
   * in the inverted interval [2,infinity].
   * 
   * lim(x->inf){ exp(x) sqrt(x) K0(x) } = sqrt(pi/2). */
  // 25 elements
  private static final ScalarUnaryOperator B_k0 = ClenshawChebyshev.inverse( //
      RealScalar.of(4), //
      Tensors.vectorDouble( //
          1.2201515410329777, //
          -0.0314481013119645, //
          0.0015698838857300533, //
          -1.2849549581627802E-4, //
          1.39498137188765E-5, //
          -1.8317555227191195E-6, //
          2.766813639445015E-7, //
          -4.660489897687948E-8, //
          8.574034017414225E-9, //
          -1.69753450938906E-9, //
          3.577397281400301E-10, //
          -7.957489244477107E-11, //
          1.8559491149547177E-11, //
          -4.514597883373944E-12, //
          1.140340588208475E-12, //
          -2.9800969231727303E-13, //
          8.032890775363575E-14, //
          -2.2275133269916698E-14, //
          6.3400764774050706E-15, //
          -1.848593377343779E-15, //
          5.512055978524319E-16, //
          -1.678231096805412E-16, //
          5.2103915050390274E-17, //
          -1.6475804301524212E-17, //
          5.300433772686263E-18 //
      ));

  /** Returns the modified Bessel function of the third kind
   * of order 0 of the argument.
   * <p>
   * The range is partitioned into the two intervals [0,8] and
   * (8, infinity). Chebyshev polynomial expansions are employed
   * in each interval.
   *
   * @param x the value to compute the bessel function of. */
  public static Scalar _0(Scalar x) {
    if (Sign.isNegative(x)) {
      /* the real part of BesselK[0, x] is a SYMMETRIC function */
      Scalar re = _0(x.negate());
      Scalar im = DoubleScalar.INDETERMINATE; // unknown to us
      ComplexScalar.of(re, im);
      throw new ArithmeticException();
    }
    if (x.equals(RealScalar.ZERO))
      return DoubleScalar.POSITIVE_INFINITY;
    if (Scalars.lessEquals(x, RealScalar.TWO))
      return A_k0.apply(x.multiply(x)).subtract(Log.FUNCTION.apply(x.multiply(RationalScalar.HALF)).multiply(BesselI._0(x)));
    return Exp.FUNCTION.apply(x.negate()).multiply(B_k0.apply(x)).divide(Sqrt.FUNCTION.apply(x));
  }

  public static Scalar _0(Number x) {
    return _0(RealScalar.of(x));
  }

  /* Chebyshev coefficients for x(K1(x) - log(x/2) I1(x))
   * in the interval [0,4].
   * 
   * lim(x->0){ x(K1(x) - log(x/2) I1(x)) } = 1. */
  // 11 elements
  private static final ScalarUnaryOperator A_k1 = ClenshawChebyshev.forward( //
      Clips.interval(0, 4), //
      Tensors.vectorDouble( //
          0.7626501136694739, //
          -0.3531559607765449, //
          -0.12261118082265715, //
          -0.006975723859639864, //
          -1.730288957513052E-4, //
          -2.4334061415659684E-6, //
          -2.213387630734726E-8, //
          -1.4114883926335278E-10, //
          -6.666901694199329E-13, //
          -2.427449850519366E-15, //
          -7.023863479386288E-18 //
      ));
  /* Chebyshev coefficients for exp(x) sqrt(x) K1(x)
   * in the interval [2,infinity].
   *
   * lim(x->inf){ exp(x) sqrt(x) K1(x) } = sqrt(pi/2). */
  // 25 elements
  private static final ScalarUnaryOperator B_k1 = ClenshawChebyshev.inverse( //
      RealScalar.of(4), //
      Tensors.vectorDouble( //
          1.3603130952422213, //
          0.10392373657681724, //
          -0.002857816859622779, //
          1.9521551847135162E-4, //
          -1.936197974166083E-5, //
          2.406484947837217E-6, //
          -3.5019606030878126E-7, //
          5.7410841254500495E-8, //
          -1.0345762465678097E-8, //
          2.015049755197033E-9, //
          -4.1903547593418965E-10, //
          9.218315187605006E-11, //
          -2.1299678384275683E-11, //
          5.13963967348173E-12, //
          -1.2891739609510289E-12, //
          3.3484196660784293E-13, //
          -8.976705182324994E-14, //
          2.4771544244813043E-14, //
          -7.019837090418314E-15, //
          2.038703165624334E-15, //
          -6.057047248373319E-16, //
          1.838093544366639E-16, //
          -5.689462558442859E-17, //
          1.7940508731475592E-17, //
          -5.756744483665017E-18 //
      ));

  /** Returns the modified Bessel function of the third kind
   * of order 1 of the argument.
   * <p>
   * The range is partitioned into the two intervals [0,2] and
   * (2, infinity). Chebyshev polynomial expansions are employed
   * in each interval.
   *
   * @param x the value to compute the bessel function of. */
  public static Scalar _1(Scalar x) {
    Scalar z = x.multiply(RationalScalar.HALF);
    if (Sign.isNegative(z)) {
      /* the real part of BesselK[0, x] is an ANTI-SYMMETRIC function */
      Scalar re = _0(x.negate()).negate();
      Scalar im = DoubleScalar.INDETERMINATE; // unknown to us
      ComplexScalar.of(re, im);
      throw new ArithmeticException();
    }
    if (x.equals(RealScalar.ZERO))
      return ComplexInfinity.INSTANCE;
    if (Scalars.lessEquals(x, RealScalar.TWO))
      return Log.FUNCTION.apply(z).multiply(BesselI._1(x)).add(A_k1.apply(x.multiply(x)).divide(x));
    return Exp.FUNCTION.apply(x.negate()).multiply(B_k1.apply(x)).divide(Sqrt.FUNCTION.apply(x));
  }

  public static Scalar _1(Number x) {
    return _1(RealScalar.of(x));
  }

  /** 1.11022302462515654042E-16; */
  private static final double EPS = (Math.nextUp(1.0) - 1.0) * 0.5;
  private static final double MAXLOG = 700.09782712893383996732;

  /** Returns the modified Bessel function of the third kind
   * of order <tt>nn</tt> of the argument.
   * <p>
   * The range is partitioned into the two intervals [0,9.55] and
   * (9.55, infinity). An ascending power series is used in the
   * low range, and an asymptotic expansion in the high range.
   *
   * @param nn the order of the Bessel function.
   * @param x the value to compute the bessel function of. */
  // TODO TENSOR IMPL ensure test coverage, investigate corner cases, use Scalar
  public static double kn(int nn, double x) {
    /* Algorithm for Kn.
     * n-1
     * -n - (n-k-1)! 2 k
     * K (x) = 0.5 (x/2) > -------- (-x /4)
     * n - k!
     * k=0
     * 
     * inf. 2 k
     * n n - (x /4)
     * + (-1) 0.5(x/2) > {p(k+1) + p(n+k+1) - 2log(x/2)} ---------
     * - k! (n+k)!
     * k=0
     * 
     * where p(m) is the psi function: p(1) = -EUL and
     * 
     * m-1
     * -
     * p(m) = -EUL + > 1/k
     * -
     * k=1
     * 
     * For large x,
     * 2 2 2
     * u-1 (u-1 )(u-3 )
     * K (z) = sqrt(pi/2z) exp(-z) { 1 + ------- + ------------ + ...}
     * v 1 2
     * 1! (8z) 2! (8z)
     * asymptotically, where
     * 
     * 2
     * u = 4 v . */
    final double EUL = Gamma.EULER.number().doubleValue(); // 0.5772156649015328606065;
    final double MAXNUM = Double.MAX_VALUE;
    final int MAXFAC = 31;
    double k, kf, nk1f, nkf, zn, t, s, z0, z;
    double ans, fn, pn, pk, zmn, tlg, tox;
    int i, n;
    if (nn < 0)
      n = -nn;
    else
      n = nn;
    if (n > MAXFAC)
      throw new ArithmeticException("Overflow");
    if (x <= 0.0)
      throw new IllegalArgumentException();
    if (x <= 9.55) {
      ans = 0.0;
      z0 = 0.25 * x * x;
      fn = 1.0;
      pn = 0.0;
      zmn = 1.0;
      tox = 2.0 / x;
      if (n > 0) {
        /* compute factorial of n and psi(n) */
        pn = -EUL;
        k = 1.0;
        for (i = 1; i < n; i++) {
          pn += 1.0 / k;
          k += 1.0;
          fn *= k;
        }
        zmn = tox;
        if (n == 1) {
          ans = 1.0 / x;
        } else {
          nk1f = fn / n;
          kf = 1.0;
          s = nk1f;
          z = -z0;
          zn = 1.0;
          for (i = 1; i < n; i++) {
            nk1f = nk1f / (n - i);
            kf = kf * i;
            zn *= z;
            t = nk1f * zn / kf;
            s += t;
            if ((MAXNUM - Math.abs(t)) < Math.abs(s))
              throw new ArithmeticException("Overflow");
            if ((tox > 1.0) && ((MAXNUM / tox) < zmn))
              throw new ArithmeticException("Overflow");
            zmn *= tox;
          }
          s *= 0.5;
          t = Math.abs(s);
          if ((zmn > 1.0) && ((MAXNUM / zmn) < t))
            throw new ArithmeticException("Overflow");
          if ((t > 1.0) && ((MAXNUM / t) < zmn))
            throw new ArithmeticException("Overflow");
          ans = s * zmn;
        }
      }
      tlg = 2.0 * Math.log(0.5 * x);
      pk = -EUL;
      if (n == 0) {
        pn = pk;
        t = 1.0;
      } else {
        pn = pn + 1.0 / n;
        t = 1.0 / fn;
      }
      s = (pk + pn - tlg) * t;
      k = 1.0;
      do {
        t *= z0 / (k * (k + n));
        pk += 1.0 / k;
        pn += 1.0 / (k + n);
        s += (pk + pn - tlg) * t;
        k += 1.0;
      } while (Math.abs(t / s) > EPS);
      s = 0.5 * s / zmn;
      if ((n & 1) > 0)
        s = -s;
      ans += s;
      return ans;
    }
    /* Asymptotic expansion for Kn(x) */
    /* Converges to 1.4e-17 for x > 18.4 */
    if (x > MAXLOG)
      return 0;
    k = n;
    pn = 4.0 * k * k;
    pk = 1.0;
    z0 = 8.0 * x;
    fn = 1.0;
    t = 1.0;
    s = t;
    nkf = MAXNUM;
    i = 0;
    do {
      z = pn - pk * pk;
      t = t * z / (fn * z0);
      nk1f = Math.abs(t);
      if ((i >= n) && (nk1f > nkf)) {
        ans = Math.exp(-x) * Math.sqrt(Math.PI / (2.0 * x)) * s;
        return ans;
      }
      nkf = nk1f;
      s += t;
      fn += 1.0;
      pk += 2.0;
      i += 1;
    } while (Math.abs(t / s) > EPS);
    return Math.exp(-x) * Math.sqrt(Math.PI / (2.0 * x)) * s;
  }
}
