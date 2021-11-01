// code by jph
package ch.alpine.tensor.sca;

import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;

/** clip to a single point
 * 
 * @implSpec
 * This class is immutable and thread-safe. */
/* package */ class ClipPoint extends ClipInterval {
  public ClipPoint(Scalar value, Scalar width) {
    super(value, value, width);
  }

  @Override // from ClipInterval
  public Scalar rescale(Scalar scalar) {
    apply(scalar);
    return RealScalar.ZERO;
  }
}
