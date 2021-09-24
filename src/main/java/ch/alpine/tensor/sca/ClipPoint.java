// code by jph
package ch.alpine.tensor.sca;

import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;

/** clip to a single point */
/* package */ class ClipPoint extends ClipInterval {
  ClipPoint(Scalar value, Scalar width) {
    super(value, value, width);
  }

  @Override // from ClipInterval
  public Scalar rescale(Scalar scalar) {
    apply(scalar);
    return RealScalar.ZERO;
  }
}
