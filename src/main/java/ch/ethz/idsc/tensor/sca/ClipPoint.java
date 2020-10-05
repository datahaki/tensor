// code by jph
package ch.ethz.idsc.tensor.sca;

import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;

/** clip to a single point */
/* package */ class ClipPoint extends ClipInterval {
  private static final long serialVersionUID = 1172798502501040684L;

  ClipPoint(Scalar value, Scalar width) {
    super(value, value, width);
  }

  @Override // from Clip
  public Scalar rescale(Scalar scalar) {
    apply(scalar);
    return RealScalar.ZERO;
  }
}
