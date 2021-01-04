// code by jph
package ch.ethz.idsc.tensor.lie;

import java.io.Serializable;

import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.TensorRuntimeException;
import ch.ethz.idsc.tensor.mat.IdentityMatrix;
import ch.ethz.idsc.tensor.mat.Inverse;
import ch.ethz.idsc.tensor.mat.Tolerance;

/** iteration to converge to sqrt of matrix */
/* package */ class DenmanBeavers implements MatrixSqrt, Serializable {
  private static final long serialVersionUID = -6255470234569408987L;
  private static final int MAX_ITERATIONS = 100;
  private static final Scalar HALF = RealScalar.of(0.5);
  /***************************************************/
  private Tensor yk;
  private Tensor zk;

  public DenmanBeavers(Tensor a) {
    yk = a;
    zk = IdentityMatrix.of(a.length());
    for (int count = 0; count < MAX_ITERATIONS; ++count) {
      Tensor yn = yk.add(Inverse.of(zk)).multiply(HALF);
      Tensor zn = zk.add(Inverse.of(yk)).multiply(HALF);
      if (Tolerance.CHOP.isClose(yk, yn) && //
          Tolerance.CHOP.isClose(zk, zn))
        return;
      yk = yn;
      zk = zn;
    }
    throw TensorRuntimeException.of(a);
  }

  @Override
  public Tensor sqrt() {
    return yk;
  }

  @Override
  public Tensor sqrt_inverse() {
    return zk;
  }
}
