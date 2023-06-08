// code by jph
package ch.alpine.tensor.lie.bch;

import ch.alpine.tensor.RationalScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;

/* package */ class BchSeries8 extends BchSeries {
  private static final Scalar P1_2 = RationalScalar.of(1, 2);
  private static final Scalar P1_12 = RationalScalar.of(1, 12);
  private static final Scalar N1_12 = RationalScalar.of(-1, 12);
  private static final Scalar N1_24 = RationalScalar.of(-1, 24);
  private static final Scalar N1_720 = RationalScalar.of(-1, 720);
  private static final Scalar N1_120 = RationalScalar.of(-1, 120);
  private static final Scalar N1_360 = RationalScalar.of(-1, 360);
  private static final Scalar P1_360 = RationalScalar.of(1, 360);
  private static final Scalar P1_120 = RationalScalar.of(1, 120);
  private static final Scalar P1_720 = RationalScalar.of(1, 720);
  private static final Scalar N1_1440 = RationalScalar.of(-1, 1440);
  private static final Scalar P1_240 = RationalScalar.of(1, 240);
  private static final Scalar P1_1440 = RationalScalar.of(1, 1440);
  private static final Scalar P1_30240 = RationalScalar.of(1, 30240);
  private static final Scalar P1_6720 = RationalScalar.of(1, 6720);
  private static final Scalar N1_15120 = RationalScalar.of(-1, 15120);
  private static final Scalar N1_10080 = RationalScalar.of(-1, 10080);
  private static final Scalar P1_10080 = RationalScalar.of(1, 10080);
  private static final Scalar N1_20160 = RationalScalar.of(-1, 20160);
  private static final Scalar P1_4032 = RationalScalar.of(1, 4032);
  private static final Scalar P1_1260 = RationalScalar.of(1, 1260);
  private static final Scalar P1_3360 = RationalScalar.of(1, 3360);
  private static final Scalar N1_7560 = RationalScalar.of(-1, 7560);
  private static final Scalar N1_1890 = RationalScalar.of(-1, 1890);
  private static final Scalar N1_672 = RationalScalar.of(-1, 672);
  private static final Scalar N1_3150 = RationalScalar.of(-1, 3150);
  private static final Scalar P1_6048 = RationalScalar.of(1, 6048);
  private static final Scalar P1_1680 = RationalScalar.of(1, 1680);
  private static final Scalar P1_4200 = RationalScalar.of(1, 4200);
  private static final Scalar N1_6048 = RationalScalar.of(-1, 6048);
  private static final Scalar N11_50400 = RationalScalar.of(-11, 50400);
  private static final Scalar N1_30240 = RationalScalar.of(-1, 30240);
  private static final Scalar P1_13440 = RationalScalar.of(1, 13440);
  private static final Scalar P1_12096 = RationalScalar.of(1, 12096);
  private static final Scalar N1_3360 = RationalScalar.of(-1, 3360);
  private static final Scalar N1_12096 = RationalScalar.of(-1, 12096);
  private static final Scalar P1_8064 = RationalScalar.of(1, 8064);
  private static final Scalar P1_2520 = RationalScalar.of(1, 2520);
  private static final Scalar P1_20160 = RationalScalar.of(1, 20160);
  private static final Scalar N1_3780 = RationalScalar.of(-1, 3780);
  private static final Scalar N1_1344 = RationalScalar.of(-1, 1344);
  private static final Scalar N1_6300 = RationalScalar.of(-1, 6300);
  private static final Scalar P1_8400 = RationalScalar.of(1, 8400);
  private static final Scalar N11_100800 = RationalScalar.of(-11, 100800);
  private static final Scalar N1_60480 = RationalScalar.of(-1, 60480);

  public BchSeries8(Tensor ad) {
    super(ad);
  }

  @Override
  public Tensor series(Tensor x, Tensor y) {
    Tensor adx = ad.dot(x);
    Tensor ady = ad.dot(y);
    // d = 1
    Tensor t1 = x.add(y);
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
    Tensor t5_1 = xxyxy.multiply(N1_120);
    Tensor yyxy = ady.dot(yxy);
    Tensor xyyxy = adx.dot(yyxy);
    Tensor t5_2 = xyyxy.multiply(N1_360);
    Tensor yxxxy = ady.dot(xxxy);
    Tensor t5_3 = yxxxy.multiply(P1_360);
    Tensor yxyxy = ady.dot(xyxy);
    Tensor t5_4 = yxyxy.multiply(P1_120);
    Tensor yyyxy = ady.dot(yyxy);
    Tensor t5_5 = yyyxy.multiply(P1_720);
    Tensor t5 = t5_0.add(t5_1).add(t5_2).add(t5_3).add(t5_4).add(t5_5);
    // d = 6
    Tensor xxxyxy = adx.dot(xxyxy);
    Tensor t6_0 = xxxyxy.multiply(N1_1440);
    Tensor xxyyxy = adx.dot(xyyxy);
    Tensor t6_1 = xxyyxy.multiply(N1_720);
    Tensor xyxxxy = adx.dot(yxxxy);
    Tensor t6_2 = xyxxxy.multiply(P1_720);
    Tensor xyxyxy = adx.dot(yxyxy);
    Tensor t6_3 = xyxyxy.multiply(P1_240);
    Tensor xyyyxy = adx.dot(yyyxy);
    Tensor t6_4 = xyyyxy.multiply(P1_1440);
    Tensor t6 = t6_0.add(t6_1).add(t6_2).add(t6_3).add(t6_4);
    // d = 7
    Tensor xxxxxy = adx.dot(xxxxy);
    Tensor xxxxxxy = adx.dot(xxxxxy);
    Tensor t7_0 = xxxxxxy.multiply(P1_30240);
    Tensor xxxxyxy = adx.dot(xxxyxy);
    Tensor t7_1 = xxxxyxy.multiply(P1_6720);
    Tensor xxxyyxy = adx.dot(xxyyxy);
    Tensor t7_2 = xxxyyxy.multiply(N1_15120);
    Tensor xxyxxxy = adx.dot(xyxxxy);
    Tensor t7_3 = xxyxxxy.multiply(N1_10080);
    Tensor xxyxyxy = adx.dot(xyxyxy);
    Tensor t7_4 = xxyxyxy.multiply(P1_10080);
    Tensor xxyyyxy = adx.dot(xyyyxy);
    Tensor t7_5 = xxyyyxy.multiply(N1_20160);
    Tensor yxxxxy = ady.dot(xxxxy);
    Tensor xyxxxxy = adx.dot(yxxxxy);
    Tensor t7_6 = xyxxxxy.multiply(P1_4032);
    Tensor yxxyxy = ady.dot(xxyxy);
    Tensor xyxxyxy = adx.dot(yxxyxy);
    Tensor t7_7 = xyxxyxy.multiply(P1_1260);
    Tensor yxyyxy = ady.dot(xyyxy);
    Tensor xyxyyxy = adx.dot(yxyyxy);
    Tensor t7_8 = xyxyyxy.multiply(P1_3360);
    Tensor yyxxxy = ady.dot(yxxxy);
    Tensor xyyxxxy = adx.dot(yyxxxy);
    Tensor t7_9 = xyyxxxy.multiply(N1_15120);
    Tensor yyxyxy = ady.dot(yxyxy);
    Tensor xyyxyxy = adx.dot(yyxyxy);
    Tensor t7_10 = xyyxyxy.multiply(P1_4032);
    Tensor yyyyxy = ady.dot(yyyxy);
    Tensor xyyyyxy = adx.dot(yyyyxy);
    Tensor t7_11 = xyyyyxy.multiply(P1_10080);
    Tensor yxxxxxy = ady.dot(xxxxxy);
    Tensor t7_12 = yxxxxxy.multiply(N1_10080);
    Tensor yxxxyxy = ady.dot(xxxyxy);
    Tensor t7_13 = yxxxyxy.multiply(N1_7560);
    Tensor yxxyyxy = ady.dot(xxyyxy);
    Tensor t7_14 = yxxyyxy.multiply(P1_3360);
    Tensor yxyxxxy = ady.dot(xyxxxy);
    Tensor t7_15 = yxyxxxy.multiply(N1_1890);
    Tensor yxyxyxy = ady.dot(xyxyxy);
    Tensor t7_16 = yxyxyxy.multiply(N1_672);
    Tensor yxyyyxy = ady.dot(xyyyxy);
    Tensor t7_17 = yxyyyxy.multiply(N1_3150);
    Tensor yyxxxxy = ady.dot(yxxxxy);
    Tensor t7_18 = yyxxxxy.multiply(P1_6048);
    Tensor yyxxyxy = ady.dot(yxxyxy);
    Tensor t7_19 = yyxxyxy.multiply(P1_1680);
    Tensor yyxyyxy = ady.dot(yxyyxy);
    Tensor t7_20 = yyxyyxy.multiply(P1_4200);
    Tensor yyyxxxy = ady.dot(yyxxxy);
    Tensor t7_21 = yyyxxxy.multiply(N1_6048);
    Tensor yyyxyxy = ady.dot(yyxyxy);
    Tensor t7_22 = yyyxyxy.multiply(N11_50400);
    Tensor yyyyyxy = ady.dot(yyyyxy);
    Tensor t7_23 = yyyyyxy.multiply(N1_30240);
    Tensor t7 = t7_0.add(t7_1).add(t7_2).add(t7_3).add(t7_4).add(t7_5).add(t7_6).add(t7_7).add(t7_8).add(t7_9).add(t7_10).add(t7_11).add(t7_12).add(t7_13)
        .add(t7_14).add(t7_15).add(t7_16).add(t7_17).add(t7_18).add(t7_19).add(t7_20).add(t7_21).add(t7_22).add(t7_23);
    // d = 8
    Tensor xxxxxyxy = adx.dot(xxxxyxy);
    Tensor t8_0 = xxxxxyxy.multiply(P1_13440);
    Tensor xxxxyyxy = adx.dot(xxxyyxy);
    Tensor t8_1 = xxxxyyxy.multiply(P1_12096);
    Tensor xxxyxxxy = adx.dot(xxyxxxy);
    Tensor t8_2 = xxxyxxxy.multiply(N1_6048);
    Tensor xxxyxyxy = adx.dot(xxyxyxy);
    Tensor t8_3 = xxxyxyxy.multiply(N1_3360);
    Tensor xxxyyyxy = adx.dot(xxyyyxy);
    Tensor t8_4 = xxxyyyxy.multiply(N1_12096);
    Tensor xxyxxxxy = adx.dot(xyxxxxy);
    Tensor t8_5 = xxyxxxxy.multiply(P1_8064);
    Tensor xxyxxyxy = adx.dot(xyxxyxy);
    Tensor t8_6 = xxyxxyxy.multiply(P1_2520);
    Tensor xxyxyyxy = adx.dot(xyxyyxy);
    Tensor t8_7 = xxyxyyxy.multiply(P1_6720);
    Tensor xxyyxxxy = adx.dot(xyyxxxy);
    Tensor t8_8 = xxyyxxxy.multiply(N1_30240);
    Tensor xxyyxyxy = adx.dot(xyyxyxy);
    Tensor t8_9 = xxyyxyxy.multiply(P1_8064);
    Tensor xxyyyyxy = adx.dot(xyyyyxy);
    Tensor t8_10 = xxyyyyxy.multiply(P1_20160);
    Tensor xyxxxxxy = adx.dot(yxxxxxy);
    Tensor t8_11 = xyxxxxxy.multiply(N1_20160);
    Tensor xyxxxyxy = adx.dot(yxxxyxy);
    Tensor t8_12 = xyxxxyxy.multiply(N1_15120);
    Tensor xyxxyyxy = adx.dot(yxxyyxy);
    Tensor t8_13 = xyxxyyxy.multiply(P1_6720);
    Tensor xyxyxxxy = adx.dot(yxyxxxy);
    Tensor t8_14 = xyxyxxxy.multiply(N1_3780);
    Tensor xyxyxyxy = adx.dot(yxyxyxy);
    Tensor t8_15 = xyxyxyxy.multiply(N1_1344);
    Tensor xyxyyyxy = adx.dot(yxyyyxy);
    Tensor t8_16 = xyxyyyxy.multiply(N1_6300);
    Tensor xyyxxxxy = adx.dot(yyxxxxy);
    Tensor t8_17 = xyyxxxxy.multiply(P1_12096);
    Tensor xyyxxyxy = adx.dot(yyxxyxy);
    Tensor t8_18 = xyyxxyxy.multiply(P1_3360);
    Tensor xyyxyyxy = adx.dot(yyxyyxy);
    Tensor t8_19 = xyyxyyxy.multiply(P1_8400);
    Tensor xyyyxxxy = adx.dot(yyyxxxy);
    Tensor t8_20 = xyyyxxxy.multiply(N1_12096);
    Tensor xyyyxyxy = adx.dot(yyyxyxy);
    Tensor t8_21 = xyyyxyxy.multiply(N11_100800);
    Tensor xyyyyyxy = adx.dot(yyyyyxy);
    Tensor t8_22 = xyyyyyxy.multiply(N1_60480);
    Tensor t8 = t8_0.add(t8_1).add(t8_2).add(t8_3).add(t8_4).add(t8_5).add(t8_6).add(t8_7).add(t8_8).add(t8_9).add(t8_10).add(t8_11).add(t8_12).add(t8_13)
        .add(t8_14).add(t8_15).add(t8_16).add(t8_17).add(t8_18).add(t8_19).add(t8_20).add(t8_21).add(t8_22);
    // ---
    return Tensors.of(t1, t2, t3, t4, t5, t6, t7, t8);
  }
}
