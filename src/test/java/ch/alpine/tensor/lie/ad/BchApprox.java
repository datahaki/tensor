// code by jph
package ch.alpine.tensor.lie.ad;

import java.io.Serializable;
import java.util.function.BinaryOperator;

import ch.alpine.tensor.RationalScalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.red.Total;

/** References:
 * Neeb
 * 
 * "Baker-Campbell-Hausdorff formula" Wikipedia */
/* package */ class BchApprox implements BinaryOperator<Tensor>, Serializable {
  public static final int DEGREE = 5;

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
    return new Inner(x, y).sum;
  }

  private class Inner {
    private final Tensor sum;

    Inner(Tensor x, Tensor y) {
      Tensor adx = ad.dot(x);
      Tensor ady = ad.dot(y);
      Tensor adxy = adx.dot(y);
      Tensor adxxy = adx.dot(adxy);
      Tensor adyyx = ady.dot(adxy.negate());
      Tensor adxyxy = adx.dot(adyyx.negate());
      Tensor adyyyyx = ady.dot(ady.dot(adyyx));
      Tensor adxxxxy = adx.dot(adx.dot(adxxy));
      Tensor adxyyyx = adx.dot(ady.dot(adyyx));
      Tensor adyxxxy = ady.dot(adx.dot(adxxy));
      Tensor adyxyxy = ady.dot(adxyxy);
      Tensor adxyxyx = adx.dot(ady.dot(adxxy.negate()));
      Tensor t1 = adyyyyx.add(adxxxxy).multiply(RationalScalar.of(-1, 720));
      Tensor t2 = adxyyyx.add(adyxxxy).multiply(RationalScalar.of(+1, 360));
      Tensor t3 = adyxyxy.add(adxyxyx).multiply(RationalScalar.of(+1, 120));
      sum = Tensors.of( //
          x.add(y), //
          adxy.multiply(RationalScalar.HALF), //
          adxxy.add(adyyx).multiply(RationalScalar.of(+1, 12)), //
          adxyxy.multiply(RationalScalar.of(-1, 24)), //
          t1.add(t2).add(t3));
    }

    private Tensor sum() {
      return Total.of(sum);
    }
  }
}
