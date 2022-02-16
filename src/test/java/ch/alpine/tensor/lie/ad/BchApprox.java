// code by jph
package ch.alpine.tensor.lie.ad;

import java.io.Serializable;
import java.util.function.BinaryOperator;

import ch.alpine.tensor.RationalScalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.alg.Dot;
import ch.alpine.tensor.red.Total;

/** the explicit computation of the terms in the series is about 8-10 times
 * faster than using {@link BakerCampbellHausdorff}
 * 
 * References:
 * 1) Neeb
 * 2) "Baker-Campbell-Hausdorff formula" Wikipedia */
/* package */ class BchApprox implements BinaryOperator<Tensor>, Serializable {
  public static final int DEGREE = 7;

  /** @param ad
   * @return */
  public static BinaryOperator<Tensor> of(Tensor ad) {
    return new BchApprox(JacobiIdentity.require(ad));
  }

  // ---
  private final Tensor ad;

  public BchApprox(Tensor ad) {
    this.ad = ad;
  }

  @Override
  public Tensor apply(Tensor x, Tensor y) {
    return new Inner(x, y).sum();
  }

  /* package */ Tensor series(Tensor x, Tensor y) {
    return new Inner(x, y).series;
  }

  private class Inner {
    private final Tensor series;

    Inner(Tensor x, Tensor y) {
      Tensor adx = ad.dot(x);
      Tensor ady = ad.dot(y);
      Tensor adxy = adx.dot(y);
      // ---
      Tensor adxxy = adx.dot(adxy);
      Tensor adyyx = ady.dot(adxy.negate());
      // ---
      Tensor adxyxy = adx.dot(adyyx.negate());
      // ---
      Tensor adyyyyx = ady.dot(ady.dot(adyyx));
      Tensor adxxxxy = adx.dot(adx.dot(adxxy));
      Tensor adxyyyx = adx.dot(ady.dot(adyyx));
      Tensor adyxxxy = ady.dot(adx.dot(adxxy));
      Tensor adyxyxy = ady.dot(adxyxy);
      Tensor adxyxyx = adx.dot(ady.dot(adxxy.negate()));
      Tensor t1 = adyyyyx.add(adxxxxy).multiply(RationalScalar.of(-1, 720));
      Tensor t2 = adxyyyx.add(adyxxxy).multiply(RationalScalar.of(+1, 360));
      Tensor t3 = adyxyxy.add(adxyxyx).multiply(RationalScalar.of(+1, 120));
      // ---
      Tensor adxxyxyx = adx.dot(adxyxyx);
      Tensor adxxyyyx = adx.dot(adxyyyx);
      Tensor adxyxxxy = adx.dot(adyxxxy);
      Tensor adxyxyxy = adx.dot(adyxyxy);
      Tensor adxyyyyx = adx.dot(adyyyyx);
      Tensor adyxyxyx = ady.dot(adxyxyx);
      Tensor a6_1 = adxxyxyx.subtract(adxyyyyx).multiply(RationalScalar.of(+1, 1440));
      Tensor a6_2 = adxxyyyx.add(adxyxxxy).add(adyxyxyx).multiply(RationalScalar.of(+1, 720));
      Tensor a6_3 = adxyxyxy.multiply(RationalScalar.of(+1, 180));
      Tensor a7;
      {
        Tensor a7_0 = Dot.of(adx, adx, adxxxxy).multiply(RationalScalar.of(1, 30240));
        Tensor a7_1 = Dot.of(adx, adx, adx, adxyxy).multiply(RationalScalar.of(1, 4032));
        Tensor a7_2 = Dot.of(adx, adxxyxyx).multiply(RationalScalar.of(1, 10080));
        Tensor a7_3 = Dot.of(adx, adx, adx, ady, ady, adx, y).multiply(RationalScalar.of(-1, 15120));
        Tensor a7_4 = Dot.of(adx, adxyxxxy).multiply(RationalScalar.of(-1, 10080));
        Tensor a7_5 = Dot.of(adx, adxyxyxy).multiply(RationalScalar.of(1, 2520));
        Tensor a7_6 = Dot.of(adx, adx, ady, ady, adx, adx, y).multiply(RationalScalar.of(-1, 3360));
        Tensor a7_7 = Dot.of(adx, adxyyyyx).multiply(RationalScalar.of(1, 20160));
        Tensor a7_8 = Dot.of(adx, ady, adxxxxy).multiply(RationalScalar.of(1, 4032));
        Tensor a7_9 = Dot.of(adx, ady, adx, adxyxy).multiply(RationalScalar.of(1, 2520));
        Tensor a7_10 = Dot.of(adx, adyxyxyx).multiply(RationalScalar.of(-1, 2520));
        Tensor a7_11 = Dot.of(adx, ady, adx, ady, ady, adx, y).multiply(RationalScalar.of(1, 3360));
        Tensor a7_12 = Dot.of(adx, ady, adyxxxy).multiply(RationalScalar.of(-1, 15120));
        Tensor a7_13 = Dot.of(adx, ady, ady, adxyxy).multiply(RationalScalar.of(1, 3360));
        Tensor a7_14 = Dot.of(adx, ady, ady, ady, adx, adx, y).multiply(RationalScalar.of(-1, 20160));
        Tensor a7_15 = Dot.of(adx, ady, adyyyyx).multiply(RationalScalar.of(-1, 10080));
        Tensor a7_16 = Dot.of(ady, adx, adxxxxy).multiply(RationalScalar.of(-1, 10080));
        Tensor a7_17 = Dot.of(ady, adx, adx, adxyxy).multiply(RationalScalar.of(-1, 1890));
        Tensor a7_18 = Dot.of(ady, adx, adx, ady, adx, adx, y).multiply(RationalScalar.of(1, 2520));
        Tensor a7_19 = Dot.of(ady, adxxyyyx).multiply(RationalScalar.of(-1, 3360));
        Tensor a7_20 = Dot.of(ady, adxyxxxy).multiply(RationalScalar.of(-1, 1890));
        Tensor a7_21 = Dot.of(ady, adxyxyxy).multiply(RationalScalar.of(-1, 560));
        Tensor a7_22 = Dot.of(ady, adx, ady, ady, adx, adx, y).multiply(RationalScalar.of(1, 3360));
        Tensor a7_23 = Dot.of(ady, adx, adyyyyx).multiply(RationalScalar.of(1, 3150));
        Tensor a7_24 = Dot.of(ady, ady, adxxxxy).multiply(RationalScalar.of(1, 6048));
        Tensor a7_25 = Dot.of(ady, ady, adx, adxyxy).multiply(RationalScalar.of(1, 3360));
        Tensor a7_26 = Dot.of(ady, ady, adx, ady, adx, adx, y).multiply(RationalScalar.of(1, 3360));
        Tensor a7_27 = Dot.of(ady, ady, adx, ady, ady, adx, y).multiply(RationalScalar.of(1, 4200));
        Tensor a7_28 = Dot.of(ady, ady, adyxxxy).multiply(RationalScalar.of(-1, 6048));
        Tensor a7_29 = Dot.of(ady, ady, ady, adxyxy).multiply(RationalScalar.of(-1, 3150));
        Tensor a7_30 = Dot.of(ady, ady, ady, ady, adx, adx, y).multiply(RationalScalar.of(1, 10080));
        Tensor a7_31 = Dot.of(ady, ady, adyyyyx).multiply(RationalScalar.of(1, 30240));
        // System.out.println(a7_31);
        a7 = a7_0.add(a7_1).add(a7_2).add(a7_3).add(a7_4).add(a7_5).add(a7_6).add(a7_7).add(a7_8).add(a7_9).add(a7_10).add(a7_11).add(a7_12).add(a7_13)
            .add(a7_14).add(a7_15).add(a7_16).add(a7_17).add(a7_18).add(a7_19).add(a7_20).add(a7_21).add(a7_22).add(a7_23).add(a7_24).add(a7_25).add(a7_26)
            .add(a7_27).add(a7_28).add(a7_29).add(a7_30).add(a7_31);
      }
      series = Tensors.of( //
          x.add(y), //
          adxy.multiply(RationalScalar.HALF), //
          adxxy.add(adyyx).multiply(RationalScalar.of(+1, 12)), //
          adxyxy.multiply(RationalScalar.of(-1, 24)), //
          t1.add(t2).add(t3), //
          a6_1.add(a6_2).add(a6_3), //
          a7);
    }

    private Tensor sum() {
      return Total.of(series);
    }
  }
}
