/* Copyright 1999 CERN - European Organization for Nuclear Research.
 * Permission to use, copy, modify, distribute and sell this software and its documentation for any purpose
 * is hereby granted without fee, provided that the above copyright notice appear in all copies and
 * that both that copyright notice and this permission notice appear in supporting documentation.
 * CERN makes no representations about the suitability of this software for any purpose.
 * It is provided "as is" without expressed or implied warranty. */
package ch.alpine.tensor.sca.bes;

/** Bessel and Airy functions. */
enum Bessel {
  ;
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
