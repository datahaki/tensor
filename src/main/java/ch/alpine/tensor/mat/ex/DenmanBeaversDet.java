// code by jph
package ch.alpine.tensor.mat.ex;

import java.io.Serializable;

import ch.alpine.tensor.RationalScalar;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.TensorRuntimeException;
import ch.alpine.tensor.api.ScalarUnaryOperator;
import ch.alpine.tensor.mat.HermitianMatrixQ;
import ch.alpine.tensor.mat.re.GaussianElimination;
import ch.alpine.tensor.mat.re.Inverse;
import ch.alpine.tensor.mat.re.Pivots;
import ch.alpine.tensor.sca.Abs;
import ch.alpine.tensor.sca.Chop;
import ch.alpine.tensor.sca.Power;

/** product form of DenmanBeavers iteration to converge to sqrt of matrix
 * coefficients based on determinant and size of matrix
 * 
 * Reference:
 * "Approximating the Logarithm of a Matrix to Specified Accuracy"
 * by Sheung Hun Cheng, Nicholas J. Higham, Charles S. Kenny, Alan J. Laub, 2001 */
/* package */ class DenmanBeaversDet implements MatrixSqrt, Serializable {
  private static final int MAX_ITERATIONS = 100;
  private static final Scalar HALF = RealScalar.of(0.5);
  private static final Scalar _1_4 = RealScalar.of(0.25);
  // ---
  private int count = 0;
  private Tensor mk;
  private Tensor yk;

  /** also tested for {@link HermitianMatrixQ}
   * 
   * @param matrix square with no negative eigenvalues
   * @param chop */
  public DenmanBeaversDet(Tensor matrix, Chop chop) {
    int n = matrix.length();
    mk = matrix;
    yk = matrix;
    Tensor id = StaticHelper.IDENTITY_MATRIX.apply(n);
    Tensor id2 = id.multiply(HALF);
    ScalarUnaryOperator power = Power.function(RationalScalar.of(-1, n << 1));
    for (; count < MAX_ITERATIONS; ++count) {
      /** the publication suggests to use |Det(mk)^(-1/2n)|
       * which would just take a detour via complex numbers!? */
      GaussianElimination gaussianElimination = new GaussianElimination(mk, id, Pivots.ARGMAX_ABS);
      Scalar gk = power.apply(Abs.FUNCTION.apply(gaussianElimination.det()));
      Scalar gk2 = gk.multiply(gk);
      Tensor mki = gaussianElimination.solve();
      Tensor mkg2 = mki.divide(gk2);
      Tensor yn = yk.dot(id.add(mkg2)).multiply(gk.multiply(HALF));
      boolean isClose = chop.isClose(yk, yn);
      mk = id2.add(mk.multiply(gk2).add(mkg2).multiply(_1_4));
      yk = yn;
      if (isClose)
        return;
    }
    throw TensorRuntimeException.of(matrix);
  }

  @Override // from MatrixSqrt
  public Tensor sqrt() {
    return yk;
  }

  @Override // from MatrixSqrt
  public Tensor sqrt_inverse() {
    return Inverse.of(yk);
  }

  public int count() {
    return count;
  }

  public Tensor mk() {
    return mk;
  }
}
