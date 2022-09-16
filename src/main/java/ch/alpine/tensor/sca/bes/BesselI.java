// adapted from colt by jph
package ch.alpine.tensor.sca.bes;

import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Scalars;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.api.ScalarUnaryOperator;
import ch.alpine.tensor.sca.Abs;
import ch.alpine.tensor.sca.Clips;
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
  private static final ScalarUnaryOperator A_i0 = ChebyshevClenshaw.forward( //
      Clips.interval(0, 8), //
      Tensors.vectorDouble( //
          0.33839763720473803, //
          -0.3046826723431984, //
          0.17162090152220877, //
          -0.09490109704804764, //
          0.04930528423967071, //
          -0.02373741480589947, //
          0.010546460394594998, //
          -0.004324309995050576, //
          0.0016394756169413357, //
          -5.763755745385824E-4, //
          1.8850288509584165E-4, //
          -5.754195010082104E-5, //
          1.6448448070728896E-5, //
          -4.4167383584587505E-6, //
          1.1173875391201037E-6, //
          -2.670793853940612E-7, //
          6.046995022541919E-8, //
          -1.300025009986248E-8, //
          2.6598237246823866E-9, //
          -5.189795601635263E-10, //
          9.675809035373237E-11, //
          -1.726826291441556E-11, //
          2.95505266312964E-12, //
          -4.856446783111929E-13, //
          7.676185498604936E-14, //
          -1.1685332877993451E-14, //
          1.715391285555133E-15, //
          -2.431279846547955E-16, //
          3.3307945188222384E-17, //
          -4.4153416464793395E-18 //
      ));
  /** Chebyshev coefficients for exp(-x) sqrt(x) I0(x)
   * in the inverted interval [8,infinity].
   *
   * lim(x->inf){ exp(-x) sqrt(x) I0(x) } = 1/sqrt(2pi). */
  // 25 elements
  private static final ScalarUnaryOperator B_i0 = ChebyshevClenshaw.inverse( //
      RealScalar.of(16), //
      Tensors.vectorDouble( //
          0.4022452055070544, //
          0.0033691164782556943, //
          6.889758346916825E-5, //
          2.8913705208347567E-6, //
          2.0489185894690638E-7, //
          2.266668990498178E-8, //
          3.3962320257083865E-9, //
          4.94060238822497E-10, //
          1.1889147107846439E-11, //
          -3.1499165279632416E-11, //
          -1.3215811840447713E-11, //
          -1.7941785315068062E-12, //
          7.180124451383666E-13, //
          3.8527783827421426E-13, //
          1.54008621752141E-14, //
          -4.150569347287222E-14, //
          -9.554846698828307E-15, //
          3.8116806693526224E-15, //
          1.7725601330565263E-15, //
          -3.425485619677219E-16, //
          -2.8276239805165836E-16, //
          3.461222867697461E-17, //
          4.46562142029676E-17, //
          -4.830504485944182E-18, //
          -7.233180487874754E-18 //
      ));

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
    Scalar y = Scalars.lessEquals(x, RealScalar.of(8)) //
        ? A_i0.apply(x)
        : B_i0.apply(x).divide(Sqrt.FUNCTION.apply(x));
    return Exp.FUNCTION.apply(x).multiply(y);
  }

  public static Scalar _0(Number x) {
    return _0(RealScalar.of(x));
  }

  /** Chebyshev coefficients for exp(-x) I1(x) / x
   * in the interval [0,8].
   *
   * lim(x->0){ exp(-x) I1(x) / x } = 1/2. */
  // 29 elements
  private static final ScalarUnaryOperator A_i1 = ChebyshevClenshaw.forward( //
      Clips.interval(0, 8), //
      Tensors.vectorDouble( //
          0.12629359322181682, //
          -0.17641651835783406, //
          0.1026436586898471, //
          -0.05294598120809499, //
          0.024726449030626516, //
          -0.010564084894626197, //
          0.004156422944312888, //
          -0.0015135724506312532, //
          5.122859561685758E-4, //
          -1.6176081582589674E-4, //
          4.781565107550054E-5, //
          -1.3273163656039436E-5, //
          3.4702513081376785E-6, //
          -8.568720264695455E-7, //
          2.0032947535521353E-7, //
          -4.445059128796328E-8, //
          9.381537386495773E-9, //
          -1.8872497517228294E-9, //
          3.625590281552117E-10, //
          -6.663489723502027E-11, //
          1.1736186298890901E-11, //
          -1.9839743977649436E-12, //
          3.223793365945575E-13, //
          -5.042185504727912E-14, //
          7.600684294735408E-15, //
          -1.1055969477353862E-15, //
          1.5536319577362005E-16, //
          -2.111421214358166E-17, //
          2.7779141127610464E-18 //
      ));
  /* Chebyshev coefficients for exp(-x) sqrt(x) I1(x)
   * in the inverted interval [8,infinity].
   *
   * lim(x->inf){ exp(-x) sqrt(x) I1(x) } = 1/sqrt(2pi). */
  // 25 elements
  private static final ScalarUnaryOperator B_i1 = ChebyshevClenshaw.inverse( //
      RealScalar.of(16), //
      Tensors.vectorDouble( //
          0.38928811750914005, //
          -0.009761097491361469, //
          -1.1058893876262371E-4, //
          -3.882564808877691E-6, //
          -2.512236237870209E-7, //
          -2.6314688468895196E-8, //
          -3.835380385964237E-9, //
          -5.589743462196584E-10, //
          -1.8974958123505413E-11, //
          3.2526035830154884E-11, //
          1.4125807436613782E-11, //
          2.0356285441470896E-12, //
          -7.198551776245908E-13, //
          -4.0835511110921974E-13, //
          -2.1015418427726643E-14, //
          4.272440016711951E-14, //
          1.0420276984128802E-14, //
          -3.8144030724370075E-15, //
          -1.8803547755107825E-15, //
          3.3082023109209285E-16, //
          2.96262899764595E-16, //
          -3.209525921993424E-17, //
          -4.6503053684893586E-17, //
          4.414348323071708E-18, //
          7.517296310842105E-18 //
      ));

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
    Scalar z = Abs.FUNCTION.apply(x);
    Scalar r = Scalars.lessEquals(z, RealScalar.of(8)) //
        ? A_i1.apply(z).multiply(z)
        : B_i1.apply(z).divide(Sqrt.FUNCTION.apply(z));
    z = Exp.FUNCTION.apply(z).multiply(r);
    if (Sign.isNegative(x))
      z = z.negate();
    return z;
  }

  public static Scalar _1(Number x) {
    return _1(RealScalar.of(x));
  }
}
