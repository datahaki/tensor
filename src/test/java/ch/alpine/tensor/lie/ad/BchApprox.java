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
public class BchApprox implements BinaryOperator<Tensor>, Serializable {
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
      Tensor xxy = ad.dot(x).dot(ad.dot(x).dot(y)).multiply(RationalScalar.of(+1, 12));
      Tensor yyx = ad.dot(y).dot(ad.dot(y).dot(x)).multiply(RationalScalar.of(+1, 12));
      Tensor xyxy = ad.dot(x).dot(ad.dot(y).dot(ad.dot(x).dot(y))).multiply(RationalScalar.of(-1, 24));
      Tensor x1a = ad.dot(y).dot(ad.dot(y).dot(ad.dot(y).dot(ad.dot(y).dot(x))));
      Tensor x1b = ad.dot(x).dot(ad.dot(x).dot(ad.dot(x).dot(ad.dot(x).dot(y))));
      Tensor x2a = ad.dot(x).dot(ad.dot(y).dot(ad.dot(y).dot(ad.dot(y).dot(x))));
      Tensor x2b = ad.dot(y).dot(ad.dot(x).dot(ad.dot(x).dot(ad.dot(x).dot(y))));
      Tensor x3a = ad.dot(y).dot(ad.dot(x).dot(ad.dot(y).dot(ad.dot(x).dot(y))));
      Tensor x3b = ad.dot(x).dot(ad.dot(y).dot(ad.dot(x).dot(ad.dot(y).dot(x))));
      Tensor t1 = x1a.add(x1b).multiply(RationalScalar.of(-1, 720));
      Tensor t2 = x2a.add(x2b).multiply(RationalScalar.of(+1, 360));
      Tensor t3 = x3a.add(x3b).multiply(RationalScalar.of(+1, 120));
      sum = Tensors.of( //
          x.add(y), //
          ad.dot(x).dot(y).multiply(RationalScalar.HALF), //
          xxy.add(yyx), //
          xyxy, //
          t1.add(t2).add(t3));
    }

    private Tensor sum() {
      return Total.of(sum);
    }
  }
}
