// adapted from colt by jph
package ch.alpine.tensor.sca.bes;

import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Scalars;
import ch.alpine.tensor.api.ScalarUnaryOperator;
import ch.alpine.tensor.sca.Abs;
import ch.alpine.tensor.sca.Sign;
import ch.alpine.tensor.sca.exp.Exp;
import ch.alpine.tensor.sca.pow.Sqrt;

/** inspired by
 * <a href="https://reference.wolfram.com/language/ref/BesselI.html">BesselI</a> */
public enum BesselI {
  ;
  /** Chebyshev coefficients for exp(-x) I0(x)
   * in the interval [0,8].
   *
   * lim(x->0){ exp(-x) I0(x) } = 1. */
  // 30 elements
  private static final ScalarUnaryOperator A_i0 = Chebyshev.of( //
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
  private static final ScalarUnaryOperator B_i0 = Chebyshev.of( //
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
  public static Scalar _0(Scalar x) {
    if (Sign.isNegative(x))
      x = x.negate();
    if (Scalars.lessEquals(x, RealScalar.of(8))) {
      Scalar y = x.divide(RealScalar.TWO).subtract(RealScalar.TWO);
      return Exp.FUNCTION.apply(x).multiply(A_i0.apply(y));
    }
    return Exp.FUNCTION.apply(x).multiply(B_i0.apply(RealScalar.of(32.0).divide(x).subtract(RealScalar.of(2)))).divide(Sqrt.FUNCTION.apply(x));
  }

  public static Scalar _0(Number x) {
    return _0(RealScalar.of(x));
  }

  /** Chebyshev coefficients for exp(-x) I1(x) / x
   * in the interval [0,8].
   *
   * lim(x->0){ exp(-x) I1(x) / x } = 1/2. */
  // 29 elements
  private static final ScalarUnaryOperator A_i1 = Chebyshev.of( //
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
  private static final ScalarUnaryOperator B_i1 = Chebyshev.of( //
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
  public static Scalar _1(Scalar x) {
    Scalar y;
    Scalar z = Abs.FUNCTION.apply(x);
    if (Scalars.lessEquals(z, RealScalar.of(8))) {
      y = z.divide(RealScalar.TWO).subtract(RealScalar.TWO);
      z = A_i1.apply(y).multiply(z).multiply(Exp.FUNCTION.apply(z));
    } else {
      z = Exp.FUNCTION.apply(z).multiply(B_i1.apply(RealScalar.of(32.0).divide(z).subtract(RealScalar.TWO))).divide(Sqrt.FUNCTION.apply(z));
    }
    if (Sign.isNegative(x))
      z = z.negate();
    return z;
  }

  public static Scalar _1(Number x) {
    return _1(RealScalar.of(x));
  }
}
