// code by jph
package ch.alpine.tensor.lie.bch;

import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.api.TensorBinaryOperator;
import ch.alpine.tensor.lie.JacobiIdentity;
import ch.alpine.tensor.red.Total;

/** the explicit computation of the terms in the series is about 8-10 times
 * faster than using {@link BakerCampbellHausdorff}
 * 
 * References:
 * 1) Neeb
 * 2) "Baker-Campbell-Hausdorff formula" Wikipedia
 * https://en.wikipedia.org/wiki/Baker%E2%80%93Campbell%E2%80%93Hausdorff_formula */
/* package */ abstract class BchSeries implements TensorBinaryOperator {
  protected final Tensor ad;

  protected BchSeries(Tensor ad) {
    this.ad = JacobiIdentity.INSTANCE.require(ad);
  }

  /** function allows to investigate the rate of convergence
   * 
   * apply == Total.of(series(x, y))
   * 
   * @param x
   * @param y
   * @return list of contributions up to given degree the sum of which is the
   * result of this binary operator */
  public abstract Tensor series(Tensor x, Tensor y);

  @Override // from TensorBinaryOperator
  public final Tensor apply(Tensor x, Tensor y) {
    return Total.of(series(x, y));
  }
}
