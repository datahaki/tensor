// code by jph
package ch.ethz.idsc.tensor.lie;

import java.io.Serializable;

import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.TensorRuntimeException;
import ch.ethz.idsc.tensor.mat.IdentityMatrix;
import ch.ethz.idsc.tensor.mat.Inverse;
import ch.ethz.idsc.tensor.sca.Chop;

/** iteration to converge to sqrt of matrix
 * 
 * Reference:
 * "Approximating the Logarithm of a Matrix to Specified Accuracy"
 * by Sheung Hun Cheng, Nicholas J. Higham, Charles S. Kenny, Alan J. Laub, 2001 */
/* package */ class MatrixSqrtImpl implements MatrixSqrt, Serializable {
  private static final long serialVersionUID = -3045996054583437102L;
  private static final int MAX_ITERATIONS = 100;
  private static final Scalar HALF = RealScalar.of(0.5);
  private static final Scalar _1_4 = RealScalar.of(0.25);
  // ---
  private Tensor mk;
  private Tensor yk;
  private Tensor zk;

  /** @param matrix square with no negative eigenvalues
   * @param chop */
  public MatrixSqrtImpl(Tensor matrix, Chop chop) {
    mk = matrix;
    yk = matrix;
    zk = IdentityMatrix.of(matrix.length());
    Tensor id2 = zk.multiply(HALF);
    for (int count = 0; count < MAX_ITERATIONS; ++count) {
      Tensor mki1 = Inverse.of(mk);
      Tensor mki4 = mki1.multiply(_1_4);
      Tensor mki2 = mki4.add(mki4);
      Tensor fc = id2.add(mki2);
      Tensor yn = yk.dot(fc); // original
      Tensor zn = zk.dot(fc);
      // Tensor yn = matrix.dot(zn); // alternative
      boolean isClose = chop.isClose(yk, yn) && chop.isClose(zk, zn);
      yk = yn;
      zk = zn;
      if (isClose)
        return;
      mk = id2.add(mki4).add(mk.multiply(_1_4));
    }
    throw TensorRuntimeException.of(matrix);
  }

  @Override // from MatrixSqrt
  public Tensor sqrt() {
    return yk;
  }

  @Override // from MatrixSqrt
  public Tensor sqrt_inverse() {
    return zk;
  }
}
