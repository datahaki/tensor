// code by jph
package ch.alpine.tensor.lie.ad;

import java.io.Serializable;

import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.red.Total;

/** the explicit computation of the terms in the series is about 8-10 times
 * faster than using {@link BakerCampbellHausdorff}
 * 
 * References:
 * 1) Neeb
 * 2) "Baker-Campbell-Hausdorff formula" Wikipedia */
/* package */ abstract class BchSeries implements BakerCampbellHausdorffSeries, Serializable {
  protected final Tensor ad;

  public BchSeries(Tensor ad) {
    this.ad = JacobiIdentity.require(ad);
  }

  @Override // from BinaryOperator<Tensor>
  public final Tensor apply(Tensor x, Tensor y) {
    return Total.of(series(x, y));
  }
}
