// code by jph
package ch.alpine.tensor.sca.win;

import ch.alpine.tensor.RationalScalar;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.sca.Clip;
import ch.alpine.tensor.sca.Clips;
import ch.alpine.tensor.sca.tri.Cos;

/** helper functions to evaluate window functions */
/* package */ enum StaticHelper {
  ;
  public static final Clip SEMI = Clips.absolute(RationalScalar.HALF);
  // ---
  private static final Scalar _2_PI = RealScalar.of(2 * Math.PI);
  private static final Scalar _4_PI = RealScalar.of(4 * Math.PI);
  private static final Scalar _6_PI = RealScalar.of(6 * Math.PI);
  private static final Scalar _8_PI = RealScalar.of(8 * Math.PI);

  /** function is used in {@link HammingWindow}, and {@link HannWindow}
   * 
   * @param a0
   * @param a1
   * @param x
   * @return a0 + a1 Cos[2pi x] */
  public static Scalar deg1(Scalar a0, Scalar a1, Scalar x) {
    return a0.add(a1.multiply(Cos.FUNCTION.apply(x.multiply(_2_PI))));
  }

  /** function is used in {@link BlackmanWindow}
   * 
   * @param a0
   * @param a1
   * @param a2
   * @param x
   * @return a0 + a1 Cos[2pi x] + a2 Cos[4pi x] */
  public static Scalar deg2(Scalar a0, Scalar a1, Scalar a2, Scalar x) {
    return deg1(a0, a1, x).add(a2.multiply(Cos.FUNCTION.apply(x.multiply(_4_PI))));
  }

  /** function is used in {@link NuttallWindow}, {@link BlackmanHarrisWindow},
   * and {@link BlackmanNuttallWindow}
   * 
   * @param a0
   * @param a1
   * @param a2
   * @param a3
   * @param x
   * @return a0 + a1 Cos[2pi x] + a2 Cos[4pi x] + a3 Cos[6pi x] */
  public static Scalar deg3(Scalar a0, Scalar a1, Scalar a2, Scalar a3, Scalar x) {
    return deg2(a0, a1, a2, x).add(a3.multiply(Cos.FUNCTION.apply(x.multiply(_6_PI))));
  }

  /** function is used in {@link FlatTopWindow}
   * 
   * @param a0
   * @param a1
   * @param a2
   * @param a3
   * @param x
   * @return a0 + a1 Cos[2pi x] + a2 Cos[4pi x] + a3 Cos[6pi x] + a4 Cos[8pi x] */
  public static Scalar deg4(Scalar a0, Scalar a1, Scalar a2, Scalar a3, Scalar a4, Scalar x) {
    return deg3(a0, a1, a2, a3, x).add(a4.multiply(Cos.FUNCTION.apply(x.multiply(_8_PI))));
  }
}
