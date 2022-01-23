// code by jph
package ch.alpine.tensor.mat.sv;

import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.qty.Quantity;

/** Quote: "The image of the unit sphere under any m x n matrix is a hyperellipse."
 * Numerical Linear Algebra
 * by Trefethen, Bau, 1997
 * 
 * <p>inspired by
 * <a href="https://reference.wolfram.com/language/ref/SingularValueDecomposition.html">SingularValueDecomposition</a> */
public interface SingularValueDecomposition {
  /** performs a singular value decomposition of a given matrix
   * <ul>
   * <li>u.dot(DiagonalMatrix[values()]).dot(Transpose.of(v)) == matrix
   * <li>Transpose.of(U).dot(U) == IdentityMatrix
   * <li>V.dot(Transpose.of(V) == IdentityMatrix
   * <li>Transpose.of(V).dot(V) == IdentityMatrix
   * </ul>
   * 
   * If matrix entries are of type {@link Quantity}, the unit must be unique.
   * The entries provided by {@link #values()} have the same unit.
   * 
   * @param matrix is rows x cols matrix with rows >= cols
   * @return singular value decomposition of given matrix
   * @throws Exception input is not a matrix, or if decomposition cannot be established */
  static SingularValueDecomposition of(Tensor matrix) {
    return new SingularValueDecompositionImpl(new Init(matrix));
  }

  // ---
  /** @return matrix of dimensions rows x cols with unitless entries */
  Tensor getU();

  /** Careful: the entries in the vector are not necessarily ordered
   * 
   * @return vector of non-negative singular values of length cols
   * with units as entries in matrix */
  Tensor values();

  /** @return square matrix of dimensions cols x cols with unitless entries */
  Tensor getV();
}
