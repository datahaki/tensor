// code by jph
package ch.alpine.tensor.opt.fnd;

import java.io.Serializable;

import ch.alpine.tensor.RationalScalar;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Scalars;
import ch.alpine.tensor.api.ScalarUnaryOperator;
import ch.alpine.tensor.itp.LinearBinaryAverage;
import ch.alpine.tensor.sca.Chop;
import ch.alpine.tensor.sca.Clip;

abstract class FindBase implements Serializable {
  private static final Scalar RESPHI = RealScalar.TWO.subtract(GoldenRatio.VALUE);
  // ---
  private final ScalarUnaryOperator function;
  private final Chop chop;
  private final boolean max;

  protected FindBase(ScalarUnaryOperator function, Chop chop, boolean max) {
    this.function = function;
    this.chop = chop;
    this.max = max;
  }

  /** @param clip search interval
   * @return x inside clip so that function(x) == 0 with given chop accuracy
   * @throws Exception if function(clip.min()) and function(clip.max()) have the same sign unequal to zero */
  public Scalar inside(Clip clip) {
    return inside(clip.min(), clip.max());
  }

  /** @param clip search interval
   * @param y0 == function(clip.min())
   * @param y1 == function(clip.max())
   * @return x inside clip so that function(x) == 0 with given chop accuracy
   * @throws Exception if function(clip.min()) and function(clip.max()) have the same sign unequal to zero */
  final Scalar inside(Scalar a, Scalar b) {
    Scalar x1 = a.add(b.subtract(a).multiply(RESPHI));
    Scalar x2 = b.subtract(b.subtract(a).multiply(RESPHI));
    Scalar f1 = function.apply(x1);
    Scalar f2 = function.apply(x2);
    while (!chop.isClose(a, b))
      if (Scalars.lessThan(f1, f2) ^ max) {
        b = x2;
        x2 = x1;
        f2 = f1;
        x1 = a.add(b.subtract(a).multiply(RESPHI));
        f1 = function.apply(x1);
      } else {
        a = x1;
        x1 = x2;
        f1 = f2;
        x2 = b.subtract(b.subtract(a).multiply(RESPHI));
        f2 = function.apply(x2);
      }
    // TODO TENSOR improve IEEE search
    return (Scalar) LinearBinaryAverage.INSTANCE.split(a, b, RationalScalar.HALF);
  }
}
