// code by jph
package ch.alpine.tensor.lie.ad;

import java.io.Serializable;

import ch.alpine.tensor.RationalScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;

/** the explicit computation of the terms in the series is about 8-10 times
 * faster than using {@link BakerCampbellHausdorff}
 * 
 * References:
 * 1) Neeb
 * 2) "Baker-Campbell-Hausdorff formula" Wikipedia */
/* package */ class BchSeries06 extends BchSeries implements Serializable {
  private static final Scalar P1_1 = RationalScalar.of(1, 1);
  private static final Scalar P1_2 = RationalScalar.of(1, 2);
  private static final Scalar P1_12 = RationalScalar.of(1, 12);
  private static final Scalar N1_12 = RationalScalar.of(-1, 12);
  private static final Scalar N1_24 = RationalScalar.of(-1, 24);
  private static final Scalar N1_720 = RationalScalar.of(-1, 720);
  private static final Scalar N1_240 = RationalScalar.of(-1, 240);
  private static final Scalar N1_360 = RationalScalar.of(-1, 360);
  private static final Scalar P1_360 = RationalScalar.of(1, 360);
  private static final Scalar P1_90 = RationalScalar.of(1, 90);
  private static final Scalar P1_720 = RationalScalar.of(1, 720);
  private static final Scalar N1_480 = RationalScalar.of(-1, 480);
  private static final Scalar P1_180 = RationalScalar.of(1, 180);
  private static final Scalar P1_1440 = RationalScalar.of(1, 1440);
  // ---
  private final Tensor ad;

  public BchSeries06(Tensor ad) {
    this.ad = JacobiIdentity.require(ad);
  }

  @Override
  public Tensor series(Tensor x, Tensor y) {
    Tensor adx = ad.dot(x);
    Tensor ady = ad.dot(y);
    // d = 1
    Tensor t1_0 = x.multiply(P1_1);
    Tensor t1_1 = y.multiply(P1_1);
    Tensor t1 = t1_0.add(t1_1);
    // d = 2
    Tensor xy = adx.dot(y);
    Tensor t2_0 = xy.multiply(P1_2);
    Tensor t2 = t2_0;
    // d = 3
    Tensor xxy = adx.dot(xy);
    Tensor t3_0 = xxy.multiply(P1_12);
    Tensor yxy = ady.dot(xy);
    Tensor t3_1 = yxy.multiply(N1_12);
    Tensor t3 = t3_0.add(t3_1);
    // d = 4
    Tensor xyxy = adx.dot(yxy);
    Tensor t4_0 = xyxy.multiply(N1_24);
    Tensor t4 = t4_0;
    // d = 5
    Tensor xxxy = adx.dot(xxy);
    Tensor xxxxy = adx.dot(xxxy);
    Tensor t5_0 = xxxxy.multiply(N1_720);
    Tensor xxyxy = adx.dot(xyxy);
    Tensor t5_1 = xxyxy.multiply(N1_240);
    Tensor yxxy = ady.dot(xxy);
    Tensor xyxxy = adx.dot(yxxy);
    Tensor t5_2 = xyxxy.multiply(N1_240);
    Tensor yyxy = ady.dot(yxy);
    Tensor xyyxy = adx.dot(yyxy);
    Tensor t5_3 = xyyxy.multiply(N1_360);
    Tensor yxxxy = ady.dot(xxxy);
    Tensor t5_4 = yxxxy.multiply(P1_360);
    Tensor yxyxy = ady.dot(xyxy);
    Tensor t5_5 = yxyxy.multiply(P1_90);
    Tensor yyxxy = ady.dot(yxxy);
    Tensor t5_6 = yyxxy.multiply(N1_360);
    Tensor yyyxy = ady.dot(yyxy);
    Tensor t5_7 = yyyxy.multiply(P1_720);
    Tensor t5 = t5_0.add(t5_1).add(t5_2).add(t5_3).add(t5_4).add(t5_5).add(t5_6).add(t5_7);
    // d = 6
    Tensor xxxyxy = adx.dot(xxyxy);
    Tensor t6_0 = xxxyxy.multiply(P1_720);
    Tensor xxyxxy = adx.dot(xyxxy);
    Tensor t6_1 = xxyxxy.multiply(N1_480);
    Tensor xxyyxy = adx.dot(xyyxy);
    Tensor t6_2 = xxyyxy.multiply(N1_720);
    Tensor xyxxxy = adx.dot(yxxxy);
    Tensor t6_3 = xyxxxy.multiply(P1_720);
    Tensor xyxyxy = adx.dot(yxyxy);
    Tensor t6_4 = xyxyxy.multiply(P1_180);
    Tensor xyyxxy = adx.dot(yyxxy);
    Tensor t6_5 = xyyxxy.multiply(N1_720);
    Tensor xyyyxy = adx.dot(yyyxy);
    Tensor t6_6 = xyyyxy.multiply(P1_1440);
    Tensor t6 = t6_0.add(t6_1).add(t6_2).add(t6_3).add(t6_4).add(t6_5).add(t6_6);
    return Tensors.of(t1, t2, t3, t4, t5, t6);
  }
}
