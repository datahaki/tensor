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
  /**************************************** COEFFICIENTS FOR METHODS i0, i0e * ****************************************/
  /** Chebyshev coefficients for exp(-x) I0(x)
   * in the interval [0,8].
   *
   * lim(x->0){ exp(-x) I0(x) } = 1. */
  // 30 elements
  protected static final Chebyshev A_i0 = Chebyshev.of( //
      -4.41534164647933937950E-18, //
      3.33079451882223809783E-17, //
      -2.43127984654795469359E-16, //
      1.71539128555513303061E-15, //
      -1.16853328779934516808E-14, //
      7.67618549860493561688E-14, //
      -4.85644678311192946090E-13, //
      2.95505266312963983461E-12, //
      -1.72682629144155570723E-11, //
      9.67580903537323691224E-11, //
      -5.18979560163526290666E-10, //
      2.65982372468238665035E-9, //
      -1.30002500998624804212E-8, //
      6.04699502254191894932E-8, //
      -2.67079385394061173391E-7, //
      1.11738753912010371815E-6, //
      -4.41673835845875056359E-6, //
      1.64484480707288970893E-5, //
      -5.75419501008210370398E-5, //
      1.88502885095841655729E-4, //
      -5.76375574538582365885E-4, //
      1.63947561694133579842E-3, //
      -4.32430999505057594430E-3, //
      1.05464603945949983183E-2, //
      -2.37374148058994688156E-2, //
      4.93052842396707084878E-2, //
      -9.49010970480476444210E-2, //
      1.71620901522208775349E-1, //
      -3.04682672343198398683E-1, //
      6.76795274409476084995E-1 //
  );
  /** Chebyshev coefficients for exp(-x) sqrt(x) I0(x)
   * in the inverted interval [8,infinity].
   *
   * lim(x->inf){ exp(-x) sqrt(x) I0(x) } = 1/sqrt(2pi). */
  // 25 elements
  protected static final Chebyshev B_i0 = Chebyshev.of( //
      -7.23318048787475395456E-18, //
      -4.83050448594418207126E-18, //
      4.46562142029675999901E-17, //
      3.46122286769746109310E-17, //
      -2.82762398051658348494E-16, //
      -3.42548561967721913462E-16, //
      1.77256013305652638360E-15, //
      3.81168066935262242075E-15, //
      -9.55484669882830764870E-15, //
      -4.15056934728722208663E-14, //
      1.54008621752140982691E-14, //
      3.85277838274214270114E-13, //
      7.18012445138366623367E-13, //
      -1.79417853150680611778E-12, //
      -1.32158118404477131188E-11, //
      -3.14991652796324136454E-11, //
      1.18891471078464383424E-11, //
      4.94060238822496958910E-10, //
      3.39623202570838634515E-9, //
      2.26666899049817806459E-8, //
      2.04891858946906374183E-7, //
      2.89137052083475648297E-6, //
      6.88975834691682398426E-5, //
      3.36911647825569408990E-3, //
      8.04490411014108831608E-1 //
  );
  /**************************************** COEFFICIENTS FOR METHODS i1, i1e * ****************************************/
  /** Chebyshev coefficients for exp(-x) I1(x) / x
   * in the interval [0,8].
   *
   * lim(x->0){ exp(-x) I1(x) / x } = 1/2. */
  // 29 elements
  protected static final Chebyshev A_i1 = Chebyshev.of( //
      2.77791411276104639959E-18, //
      -2.11142121435816608115E-17, //
      1.55363195773620046921E-16, //
      -1.10559694773538630805E-15, //
      7.60068429473540693410E-15, //
      -5.04218550472791168711E-14, //
      3.22379336594557470981E-13, //
      -1.98397439776494371520E-12, //
      1.17361862988909016308E-11, //
      -6.66348972350202774223E-11, //
      3.62559028155211703701E-10, //
      -1.88724975172282928790E-9, //
      9.38153738649577178388E-9, //
      -4.44505912879632808065E-8, //
      2.00329475355213526229E-7, //
      -8.56872026469545474066E-7, //
      3.47025130813767847674E-6, //
      -1.32731636560394358279E-5, //
      4.78156510755005422638E-5, //
      -1.61760815825896745588E-4, //
      5.12285956168575772895E-4, //
      -1.51357245063125314899E-3, //
      4.15642294431288815669E-3, //
      -1.05640848946261981558E-2, //
      2.47264490306265168283E-2, //
      -5.29459812080949914269E-2, //
      1.02643658689847095384E-1, //
      -1.76416518357834055153E-1, //
      2.52587186443633654823E-1 //
  );
  /* Chebyshev coefficients for exp(-x) sqrt(x) I1(x)
   * in the inverted interval [8,infinity].
   *
   * lim(x->inf){ exp(-x) sqrt(x) I1(x) } = 1/sqrt(2pi). */
  // 25 elements
  protected static final Chebyshev B_i1 = Chebyshev.of( //
      7.51729631084210481353E-18, //
      4.41434832307170791151E-18, //
      -4.65030536848935832153E-17, //
      -3.20952592199342395980E-17, //
      2.96262899764595013876E-16, //
      3.30820231092092828324E-16, //
      -1.88035477551078244854E-15, //
      -3.81440307243700780478E-15, //
      1.04202769841288027642E-14, //
      4.27244001671195135429E-14, //
      -2.10154184277266431302E-14, //
      -4.08355111109219731823E-13, //
      -7.19855177624590851209E-13, //
      2.03562854414708950722E-12, //
      1.41258074366137813316E-11, //
      3.25260358301548823856E-11, //
      -1.89749581235054123450E-11, //
      -5.58974346219658380687E-10, //
      -3.83538038596423702205E-9, //
      -2.63146884688951950684E-8, //
      -2.51223623787020892529E-7, //
      -3.88256480887769039346E-6, //
      -1.10588938762623716291E-4, //
      -9.76109749136146840777E-3, //
      7.78576235018280120474E-1 //
  );
  /**************************************** COEFFICIENTS FOR METHODS k0, k0e * ****************************************/
  /* Chebyshev coefficients for K0(x) + log(x/2) I0(x)
   * in the interval [0,2]. The odd order coefficients are all
   * zero; only the even order coefficients are listed.
   * 
   * lim(x->0){ K0(x) + log(x/2) I0(x) } = -EUL. */
  // 10 elements
  protected static final Chebyshev A_k0 = Chebyshev.of( //
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
  protected static final Chebyshev B_k0 = Chebyshev.of( //
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
  /**************************************** COEFFICIENTS FOR METHODS k1, k1e * ****************************************/
  /* Chebyshev coefficients for x(K1(x) - log(x/2) I1(x))
   * in the interval [0,2].
   * 
   * lim(x->0){ x(K1(x) - log(x/2) I1(x)) } = 1. */
  // 11 elements
  protected static final Chebyshev A_k1 = Chebyshev.of( //
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
  protected static final Chebyshev B_k1 = Chebyshev.of( //
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

  /** Returns the modified Bessel function of order 0 of the
   * argument.
   * <p>
   * The function is defined as <tt>i0(x) = j0( ix )</tt>.
   * <p>
   * The range is partitioned into the two intervals [0,8] and
   * (8, infinity). Chebyshev polynomial expansions are employed
   * in each interval.
   *
   * @param x the value to compute the bessel function of. */
  public static double i0(double x) {
    double y;
    if (x < 0)
      x = -x;
    if (x <= 8.0) {
      y = (x / 2.0) - 2.0;
      return Math.exp(x) * A_i0.run(y);
    }
    return Math.exp(x) * B_i0.run(32.0 / x - 2.0) / Math.sqrt(x);
  }

  

  /** Returns the modified Bessel function of order 1 of the
   * argument.
   * <p>
   * The function is defined as <tt>i1(x) = -i j1( ix )</tt>.
   * <p>
   * The range is partitioned into the two intervals [0,8] and
   * (8, infinity). Chebyshev polynomial expansions are employed
   * in each interval.
   *
   * @param x the value to compute the bessel function of. */
  public static double i1(double x) {
    double y, z;
    z = Math.abs(x);
    if (z <= 8.0) {
      y = (z / 2.0) - 2.0;
      z = A_i1.run(y) * z * Math.exp(z);
    } else {
      z = Math.exp(z) * B_i1.run(32.0 / z - 2.0) / Math.sqrt(z);
    }
    if (x < 0.0)
      z = -z;
    return z;
  }


  /** Returns the modified Bessel function of the third kind
   * of order 0 of the argument.
   * <p>
   * The range is partitioned into the two intervals [0,8] and
   * (8, infinity). Chebyshev polynomial expansions are employed
   * in each interval.
   *
   * @param x the value to compute the bessel function of. */
  public static double k0(double x) {
    double y, z;
    if (x <= 0.0)
      throw new ArithmeticException();
    if (x <= 2.0) {
      y = x * x - 2.0;
      y = A_k0.run(y) - Math.log(0.5 * x) * i0(x);
      return (y);
    }
    z = 8.0 / x - 2.0;
    return Math.exp(-x) * B_k0.run(z) / Math.sqrt(x);
  }

 

  /** Returns the modified Bessel function of the third kind
   * of order 1 of the argument.
   * <p>
   * The range is partitioned into the two intervals [0,2] and
   * (2, infinity). Chebyshev polynomial expansions are employed
   * in each interval.
   *
   * @param x the value to compute the bessel function of. */
  public static double k1(double x) {
    double y, z;
    z = 0.5 * x;
    if (z <= 0.0)
      throw new ArithmeticException();
    if (x <= 2.0) {
      y = x * x - 2.0;
      y = Math.log(z) * i1(x) + A_k1.run(y) / x;
      return y;
    }
    return Math.exp(-x) * B_k1.run(8.0 / x - 2.0) / Math.sqrt(x);
  }

  

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
