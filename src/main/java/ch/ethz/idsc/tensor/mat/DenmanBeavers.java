// code by jph
package ch.ethz.idsc.tensor.mat;

import ch.ethz.idsc.tensor.RationalScalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.TensorRuntimeException;
import ch.ethz.idsc.tensor.sca.Chop;

/** iteration to converge to sqrt of matrix */
/* package */ class DenmanBeavers {
  private static final int MAX_ITERATIONS = 100;
  /** sqrt of a */
  Tensor y0;
  /** inverse of sqrt of a */
  Tensor z0;

  public DenmanBeavers(Tensor a) {
    y0 = a;
    z0 = IdentityMatrix.of(a.length());
    for (int count = 0; count < MAX_ITERATIONS; ++count) {
      Tensor yn = y0.add(Inverse.of(z0)).multiply(RationalScalar.HALF);
      Tensor zn = z0.add(Inverse.of(y0)).multiply(RationalScalar.HALF);
      if (Chop._20.isClose(y0, yn) && //
          Chop._20.isClose(z0, zn))
        return;
      y0 = yn;
      z0 = zn;
    }
    throw TensorRuntimeException.of(a);
  }
}
