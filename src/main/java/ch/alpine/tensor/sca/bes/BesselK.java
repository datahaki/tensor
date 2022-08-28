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
    return RealScalar.of(Bessel.kn(Scalars.intValueExact(n), x.number().doubleValue()));
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
  private static final ScalarUnaryOperator A_k0 = Chebyshev.of( //
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
  private static final ScalarUnaryOperator B_k0 = Chebyshev.of( //
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
    if (Scalars.lessEquals(x, RealScalar.TWO)) {
      Scalar y = x.multiply(x).subtract(RealScalar.TWO);
      return A_k0.apply(y).subtract(Log.FUNCTION.apply(x.multiply(RationalScalar.HALF)).multiply(BesselI._0(x)));
    }
    Scalar z = RealScalar.of(8.0).divide(x).subtract(RealScalar.TWO);
    return Exp.FUNCTION.apply(x.negate()).multiply(B_k0.apply(z)).divide(Sqrt.FUNCTION.apply(x));
  }

  public static Scalar _0(Number x) {
    return _0(RealScalar.of(x));
  }

  /* Chebyshev coefficients for x(K1(x) - log(x/2) I1(x))
   * in the interval [0,2].
   * 
   * lim(x->0){ x(K1(x) - log(x/2) I1(x)) } = 1. */
  // 11 elements
  private static final ScalarUnaryOperator A_k1 = Chebyshev.of( //
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
  private static final ScalarUnaryOperator B_k1 = Chebyshev.of( //
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
    if (Scalars.lessEquals(x, RealScalar.TWO)) {
      Scalar y = x.multiply(x).subtract(RealScalar.TWO);
      return Log.FUNCTION.apply(z).multiply(BesselI._1(x)).add(A_k1.apply(y).divide(x));
    }
    return Exp.FUNCTION.apply(x.negate()).multiply(B_k1.apply(RealScalar.of(8.0).divide(x).subtract(RealScalar.TWO))).divide(Sqrt.FUNCTION.apply(x));
  }

  public static Scalar _1(Number x) {
    return _1(RealScalar.of(x));
  }
}
