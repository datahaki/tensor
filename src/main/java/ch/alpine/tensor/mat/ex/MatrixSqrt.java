// code by jph
package ch.alpine.tensor.mat.ex;

import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.mat.HermitianMatrixQ;
import ch.alpine.tensor.mat.SymmetricMatrixQ;
import ch.alpine.tensor.mat.Tolerance;
import ch.alpine.tensor.mat.ev.Eigensystem;
import ch.alpine.tensor.qty.Quantity;
import ch.alpine.tensor.sca.Im;

/** The tensor library provides two algorithms to compute the square root of a
 * matrix. The first algorithm is restricted to real symmetric matrices. The
 * second is the Denman-Beavers iteration.
 * 
 * Generally, the {@link MatrixSqrt#sqrt_inverse()} is not available if the given
 * matrix is singular.
 * 
 * MatrixSqrt may work for matrices with {@link Quantity} and negative eigenvalues,
 * for instance MatrixSqrt[{{-10[m^2], -2[m^2]}, {-2[m^2], 4[m^2]}}] is handled
 * successfully. */
public interface MatrixSqrt {
  ThreadLocal<Integer> MAX_ITERATIONS = ThreadLocal.withInitial(() -> 100);

  /** @param matrix square
   * @return sqrt of given matrix */
  static MatrixSqrt of(Tensor matrix) {
    if (Im.allZero(matrix) && //
        SymmetricMatrixQ.INSTANCE.test(matrix))
      return ofSymmetric(matrix);
    if (HermitianMatrixQ.INSTANCE.test(matrix))
      return ofHermitian(matrix);
    return new DenmanBeaversDet(matrix, Tolerance.CHOP);
  }

  /** @param matrix symmetric and non-complex
   * @return sqrt of given matrix
   * @throws Exception if matrix is not symmetric, or has complex entries
   * @see SymmetricMatrixQ */
  static MatrixSqrt ofSymmetric(Tensor matrix) {
    return new MatrixSqrtEigensystem(Eigensystem.ofSymmetric(matrix));
  }

  /** @param matrix hermitian
   * @return sqrt of given matrix
   * @throws Exception if matrix is not hermitian
   * @see HermitianMatrixQ */
  static MatrixSqrt ofHermitian(Tensor matrix) {
    return new MatrixSqrtEigensystem(Eigensystem.ofHermitian(matrix));
  }

  // ---
  /** @return square root of a given matrix
   * @implSpec implementation may recompute return value each invocation */
  Tensor sqrt();

  /** @return inverse of square root of a given matrix
   * @implSpec implementation may recompute return value each invocation */
  Tensor sqrt_inverse();
}
