// code by jph
package ch.alpine.tensor.mat.re;

import ch.alpine.tensor.ExactTensorQ;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Scalars;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.TensorRuntimeException;
import ch.alpine.tensor.Unprotect;
import ch.alpine.tensor.alg.Array;
import ch.alpine.tensor.alg.Join;
import ch.alpine.tensor.alg.Partition;
import ch.alpine.tensor.alg.VectorQ;

/** inspired by
 * <a href="https://reference.wolfram.com/language/ref/LinearSolve.html">LinearSolve</a> */
public enum LinearSolve {
  ;
  /** gives solution to linear system of equations.
   * scalar entries are required to implement
   * Comparable<Scalar> for pivoting.
   * 
   * @param matrix square of {@link Scalar}s that implement absolute value abs()
   * @param b tensor with first dimension identical to size of matrix
   * @return x with matrix.dot(x) == b
   * @throws TensorRuntimeException if matrix m is singular */
  public static Tensor of(Tensor matrix, Tensor b) {
    return GaussianElimination.of(matrix, b, Pivots.ARGMAX_ABS);
  }

  /** method only checks for non-zero
   * and doesn't use Scalar::abs().
   * 
   * @param matrix square
   * @param b tensor with first dimension identical to size of matrix
   * @param pivot
   * @return x with matrix.dot(x) == b
   * @throws TensorRuntimeException if given matrix is singular */
  public static Tensor of(Tensor matrix, Tensor b, Pivot pivot) {
    return GaussianElimination.of(matrix, b, pivot);
  }

  /** API EXPERIMENTAL
   * 
   * function for matrix not necessarily invertible, or square
   * 
   * Example:
   * <pre>
   * Tensor matrix = Tensors.fromString("{{1}, {1}, {-1}}");
   * Tensor b = Tensors.vector(2, 2, -2);
   * LinearSolve.any(matrix, b) == {2}
   * </pre>
   * 
   * @param matrix with exact precision scalars
   * @param b vector with exact precision scalars
   * @return x with matrix.x == b
   * @throws TensorRuntimeException if such an x does not exist
   * @see ExactTensorQ */
  public static Tensor any(Tensor matrix, Tensor b) {
    return vector(matrix, VectorQ.require(b));
  }

  // helper function
  private static Tensor vector(Tensor matrix, Tensor b) {
    int cols = Unprotect.dimension1(matrix);
    Tensor r = RowReduce.of(ExactTensorQ.require(Join.of(1, matrix, Partition.of(b, 1))));
    Tensor x = Array.zeros(cols);
    int j = 0;
    int c0 = 0;
    while (c0 < cols) {
      if (Scalars.nonZero(r.Get(j, c0))) { // use chop for numeric input?
        x.set(r.Get(j, cols), c0);
        ++j;
      }
      ++c0;
    }
    for (; j < matrix.length(); ++j)
      if (Scalars.nonZero(r.Get(j, cols)))
        throw TensorRuntimeException.of(matrix, b);
    return x;
  }
}
