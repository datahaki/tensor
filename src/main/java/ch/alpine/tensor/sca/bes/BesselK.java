/* Copyright 1999 CERN - European Organization for Nuclear Research.
 * Permission to use, copy, modify, distribute and sell this software and its documentation for any purpose
 * is hereby granted without fee, provided that the above copyright notice appear in all copies and
 * that both that copyright notice and this permission notice appear in supporting documentation.
 * CERN makes no representations about the suitability of this software for any purpose.
 * It is provided "as is" without expressed or implied warranty. */
// adapted from colt by jph
package ch.alpine.tensor.sca.bes;

import ch.alpine.tensor.RationalScalar;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Scalars;
import ch.alpine.tensor.api.ScalarUnaryOperator;
import ch.alpine.tensor.sca.Sign;
import ch.alpine.tensor.sca.exp.Exp;
import ch.alpine.tensor.sca.exp.Log;
import ch.alpine.tensor.sca.pow.Sqrt;

/** inspired by
 * <a href="https://reference.wolfram.com/language/ref/BesselK.html">BesselK</a> */
public enum BesselK {
  ;
  public static Scalar of(Scalar n, Scalar x) {
    return RealScalar.of(BesselK.kn(Scalars.intValueExact(n), x.number().doubleValue()));
  }

  public static Scalar of(Number n, Number x) {
    return of(RealScalar.of(n), RealScalar.of(x));
  }

  /* Chebyshev coefficients for K0(x) + log(x/2) I0(x)
   * in the interval [0,2]. The odd order coefficients are all
   * zero; only the even order coefficients are listed.
   * 
   * lim(x->0){ K0(x) + log(x/2) I0(x) } = -EUL. */
  // 10 elements
  private static final ScalarUnaryOperator A_k0 = ChebyshevClenshaw.forward( //
      RealScalar.TWO, RealScalar.ONE, //
      1.37446543561352307156E-16, //
      4.25981614279661018399E-14, //
      1.03496952576338420167E-11, //
      1.90451637722020886025E-9, //
      2.53479107902614945675E-7, //
      2.28621210311945178607E-5, //
      1.26461541144692592338E-3, //
      3.59799365153615016266E-2, //
      3.44289899924628486886E-1, //
      -5.35327393233902768720E-1 //
  );
  /* Chebyshev coefficients for exp(x) sqrt(x) K0(x)
   * in the inverted interval [2,infinity].
   * 
   * lim(x->inf){ exp(x) sqrt(x) K0(x) } = sqrt(pi/2). */
  // 25 elements
  private static final ScalarUnaryOperator B_k0 = ChebyshevClenshaw.reverse( //
      RealScalar.of(4), RealScalar.ONE, //
      5.30043377268626276149E-18, //
      -1.64758043015242134646E-17, //
      5.21039150503902756861E-17, //
      -1.67823109680541210385E-16, //
      5.51205597852431940784E-16, //
      -1.84859337734377901440E-15, //
      6.34007647740507060557E-15, //
      -2.22751332699166985548E-14, //
      8.03289077536357521100E-14, //
      -2.98009692317273043925E-13, //
      1.14034058820847496303E-12, //
      -4.51459788337394416547E-12, //
      1.85594911495471785253E-11, //
      -7.95748924447710747776E-11, //
      3.57739728140030116597E-10, //
      -1.69753450938905987466E-9, //
      8.57403401741422608519E-9, //
      -4.66048989768794782956E-8, //
      2.76681363944501510342E-7, //
      -1.83175552271911948767E-6, //
      1.39498137188764993662E-5, //
      -1.28495495816278026384E-4, //
      1.56988388573005337491E-3, //
      -3.14481013119645005427E-2, //
      2.44030308206595545468E0 //
  );

  /** Returns the modified Bessel function of the third kind
   * of order 0 of the argument.
   * <p>
   * The range is partitioned into the two intervals [0,8] and
   * (8, infinity). Chebyshev polynomial expansions are employed
   * in each interval.
   *
   * @param x the value to compute the bessel function of. */
  public static Scalar _0(Scalar x) {
    if (Sign.isNegativeOrZero(x))
      throw new ArithmeticException();
    if (Scalars.lessEquals(x, RealScalar.TWO))
      return A_k0.apply(x.multiply(x)).subtract(Log.FUNCTION.apply(x.multiply(RationalScalar.HALF)).multiply(BesselI._0(x)));
    return Exp.FUNCTION.apply(x.negate()).multiply(B_k0.apply(x)).divide(Sqrt.FUNCTION.apply(x));
  }

  public static Scalar _0(Number x) {
    return _0(RealScalar.of(x));
  }

  /* Chebyshev coefficients for x(K1(x) - log(x/2) I1(x))
   * in the interval [0,2]. (or 4 !?)
   * 
   * lim(x->0){ x(K1(x) - log(x/2) I1(x)) } = 1. */
  // 11 elements
  private static final ScalarUnaryOperator A_k1 = ChebyshevClenshaw.forward( //
      RealScalar.TWO, RealScalar.ONE, //
      -7.02386347938628759343E-18, //
      -2.42744985051936593393E-15, //
      -6.66690169419932900609E-13, //
      -1.41148839263352776110E-10, //
      -2.21338763073472585583E-8, //
      -2.43340614156596823496E-6, //
      -1.73028895751305206302E-4, //
      -6.97572385963986435018E-3, //
      -1.22611180822657148235E-1, //
      -3.53155960776544875667E-1, //
      1.52530022733894777053E0 //
  );
  /* Chebyshev coefficients for exp(x) sqrt(x) K1(x)
   * in the interval [2,infinity].
   *
   * lim(x->inf){ exp(x) sqrt(x) K1(x) } = sqrt(pi/2). */
  // 25 elements
  private static final ScalarUnaryOperator B_k1 = ChebyshevClenshaw.reverse( //
      RealScalar.of(4), RealScalar.ONE, //
      -5.75674448366501715755E-18, //
      1.79405087314755922667E-17, //
      -5.68946255844285935196E-17, //
      1.83809354436663880070E-16, //
      -6.05704724837331885336E-16, //
      2.03870316562433424052E-15, //
      -7.01983709041831346144E-15, //
      2.47715442448130437068E-14, //
      -8.97670518232499435011E-14, //
      3.34841966607842919884E-13, //
      -1.28917396095102890680E-12, //
      5.13963967348173025100E-12, //
      -2.12996783842756842877E-11, //
      9.21831518760500529508E-11, //
      -4.19035475934189648750E-10, //
      2.01504975519703286596E-9, //
      -1.03457624656780970260E-8, //
      5.74108412545004946722E-8, //
      -3.50196060308781257119E-7, //
      2.40648494783721712015E-6, //
      -1.93619797416608296024E-5, //
      1.95215518471351631108E-4, //
      -2.85781685962277938680E-3, //
      1.03923736576817238437E-1, //
      2.72062619048444266945E0 //
  );

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
    if (Sign.isNegativeOrZero(z))
      throw new ArithmeticException();
    if (Scalars.lessEquals(x, RealScalar.TWO))
      return Log.FUNCTION.apply(z).multiply(BesselI._1(x)).add(A_k1.apply(x.multiply(x)).divide(x));
    return Exp.FUNCTION.apply(x.negate()).multiply(B_k1.apply(x)).divide(Sqrt.FUNCTION.apply(x));
  }

  public static Scalar _1(Number x) {
    return _1(RealScalar.of(x));
  }

  protected static final double MACHEP = 1.11022302462515654042E-16;
  protected static final double MAXLOG = 7.09782712893383996732E2;

  /** Returns the modified Bessel function of the third kind
   * of order <tt>nn</tt> of the argument.
   * <p>
   * The range is partitioned into the two intervals [0,9.55] and
   * (9.55, infinity). An ascending power series is used in the
   * low range, and an asymptotic expansion in the high range.
   *
   * @param nn the order of the Bessel function.
   * @param x the value to compute the bessel function of. */
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
    final double EUL = 5.772156649015328606065e-1;
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
      } while (Math.abs(t / s) > MACHEP);
      s = 0.5 * s / zmn;
      if ((n & 1) > 0)
        s = -s;
      ans += s;
      return ans;
    }
    /* Asymptotic expansion for Kn(x) */
    /* Converges to 1.4e-17 for x > 18.4 */
    if (x > MAXLOG)
      throw new ArithmeticException("Underflow");
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
    } while (Math.abs(t / s) > MACHEP);
    return Math.exp(-x) * Math.sqrt(Math.PI / (2.0 * x)) * s;
  }
}
