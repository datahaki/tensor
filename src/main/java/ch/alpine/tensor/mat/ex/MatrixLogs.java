// code by jph
package ch.alpine.tensor.mat.ex;

import java.util.LinkedList;
import java.util.List;

import ch.alpine.tensor.DoubleScalar;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Scalars;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Throw;
import ch.alpine.tensor.ext.Integers;
import ch.alpine.tensor.ext.PackageTestAccess;
import ch.alpine.tensor.mat.MatrixQ;
import ch.alpine.tensor.mat.Tolerance;
import ch.alpine.tensor.mat.ev.Eigensystem;
import ch.alpine.tensor.nrm.Matrix2Norm;
import ch.alpine.tensor.sca.exp.Log;

/* package */ enum MatrixLogs {
  ;
  /** @param matrix of size 1 x 1
   * @return */
  public static Tensor _1(Tensor matrix) {
    MatrixQ.requireSize(matrix, 1, 1);
    return matrix.maps(Log.FUNCTION);
  }

  /** @param matrix of size 2 x 2
   * @return */
  public static Tensor _2(Tensor matrix) {
    MatrixQ.requireSize(matrix, 2, 2);
    try {
      // the final Tolerance.CHOP was discovered to be necessary when testing
      // StiefelManifold Exponential Log
      return Eigensystem.of(matrix).map(Log.FUNCTION).maps(Tolerance.CHOP);
    } catch (Exception exception) {
      // ---
    }
    return of(matrix);
  }

  // TODO TENSOR do the same as in MatrixExp: finite steps with exact precision, then give up
  @PackageTestAccess
  static Tensor of(Tensor matrix) {
    // TODO TENSOR make function to subtract from reference?
    Tensor id = StaticHelper.IDENTITY_MATRIX.apply(matrix.length());
    Tensor rem = matrix.subtract(id);
    List<DenmanBeaversDet> deque = new LinkedList<>();
    int max = MatrixLog.MatrixLog_MAX_EXPONENT.get();
    for (int count = 0; count < max; ++count) {
      Scalar rho_max = Matrix2Norm.bound(rem);
      if (Scalars.lessThan(rho_max, MatrixLog.RHO_MAX)) {
        Tensor sum = matrix.maps(Scalar::zero);
        Scalar factor = RealScalar.ONE;
        for (DenmanBeaversDet denmanBeaversDet : deque) {
          sum = sum.add(denmanBeaversDet.mk().subtract(id).multiply(factor));
          factor = factor.add(factor);
        }
        return sum.add(series1P(rem).multiply(factor));
      }
      DenmanBeaversDet denmanBeaversDet = new DenmanBeaversDet(matrix, Tolerance.CHOP);
      deque.add(denmanBeaversDet);
      matrix = denmanBeaversDet.sqrt();
      rem = matrix.subtract(id);
    }
    throw new Throw(matrix);
  }

  private static final int MAX_ITERATIONS = 96;

  /** @param x square matrix with spectral radius below 1
   * @return log[ I + x ]
   * @throws Exception if given matrix is non-square
   * @see Math#log1p(double) */
  public static Tensor series1P(Tensor x) {
    Tensor nxt = x;
    Tensor sum = nxt;
    for (int k = 2; k < MAX_ITERATIONS; ++k) {
      nxt = nxt.dot(x);
      Scalar den = DoubleScalar.of(Integers.isEven(k) ? -k : k);
      if (sum.equals(sum = sum.add(nxt.divide(den))))
        return sum;
    }
    throw new Throw(x); // insufficient convergence
  }
}
