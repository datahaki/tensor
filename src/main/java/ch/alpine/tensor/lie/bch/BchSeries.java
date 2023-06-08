// code by jph
package ch.alpine.tensor.lie.bch;

import java.io.Serializable;

import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.lie.JacobiIdentity;
import ch.alpine.tensor.red.Total;

/** the explicit computation of the terms in the series is about 8-10 times
 * faster than using {@link BakerCampbellHausdorff}
 * 
 * References:
 * 1) Neeb
 * 2) "Baker-Campbell-Hausdorff formula" Wikipedia */
/* package */ abstract class BchSeries implements SeriesInterface, Serializable {
  protected final Tensor ad;

  protected BchSeries(Tensor ad) {
    this.ad = JacobiIdentity.require(ad);
  }

  @Override // from BinaryOperator<Tensor>
  public final Tensor apply(Tensor x, Tensor y) {
    return Total.of(series(x, y));
  }
}
