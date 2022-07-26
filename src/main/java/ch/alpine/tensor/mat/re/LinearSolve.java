// code by jph
package ch.alpine.tensor.mat.re;

import java.util.concurrent.atomic.AtomicInteger;

import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Scalars;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Throw;
import ch.alpine.tensor.Unprotect;
import ch.alpine.tensor.alg.Array;
import ch.alpine.tensor.alg.VectorQ;
import ch.alpine.tensor.chq.ExactTensorQ;
import ch.alpine.tensor.ext.Integers;
import ch.alpine.tensor.mat.Tolerance;
import ch.alpine.tensor.mat.pi.LeastSquares;

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
   * @throws Throw if matrix m is singular */
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
   * @throws Throw if given matrix is singular */
  public static Tensor of(Tensor matrix, Tensor b, Pivot pivot) {
    return GaussianElimination.of(matrix, b, pivot);
  }

  /** function for matrix not necessarily invertible, or square
   * 
   * Example:
   * <pre>
   * Tensor matrix = Tensors.fromString("{{1}, {1}, {-1}}");
   * Tensor b = Tensors.vector(2, 2, -2);
   * LinearSolve.any(matrix, b) == {2}
   * </pre>
   * 
   * Known issues:
   * does not work well for input consisting of mixed units
   * 
   * @param matrix
   * @param b vector
   * @return x with matrix.x == b
   * @throws Throw if such an x does not exist
   * @see ExactTensorQ
   * @see LeastSquares */
  public static Tensor any(Tensor matrix, Tensor b) {
    if (ExactTensorQ.of(matrix) && //
        ExactTensorQ.of(b) && //
        VectorQ.of(b))
      return vector(matrix, b);
    Tensor x = LeastSquares.of(matrix, b);
    // check is necessary since least squares does not guarantee equality
    Tolerance.CHOP.requireClose(matrix.dot(x), b);
    return x;
  }

  // helper function
  private static Tensor vector(Tensor matrix, Tensor b) {
    Integers.requireEquals(matrix.length(), b.length());
    AtomicInteger atomicInteger = new AtomicInteger();
    Tensor reduce = RowReduce.of(Tensor.of(matrix.stream() //
        .map(Tensor::copy) //
        .map(row -> row.append(b.Get(atomicInteger.getAndIncrement())))));
    int last = Unprotect.dimension1(matrix);
    Tensor x = Array.zeros(last); // initial solution
    int row = 0;
    int col = 0;
    while (col < last) {
      if (Scalars.nonZero(reduce.Get(row, col))) { // use chop for numeric input?
        x.set(reduce.Get(row, last), col);
        ++row;
      }
      ++col;
    }
    for (; row < matrix.length(); ++row)
      if (Scalars.nonZero(reduce.Get(row, last)))
        throw new Throw(matrix, b);
    return x;
  }
}
