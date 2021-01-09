// code by jph
package ch.ethz.idsc.tensor.mat;

import ch.ethz.idsc.tensor.Tensor;

/** inspired by
 * <a href="https://reference.wolfram.com/language/ref/SingularValueDecomposition.html">SingularValueDecomposition</a> */
public interface SingularValueDecomposition {
  /** performs a singular value decomposition of a given matrix
   * <ul>
   * <li>u.dot(DiagonalMatrix.of(values())).dot(Transpose.of(v)) == matrix
   * <li>Transpose.of(U).dot(U) == IdentityMatrix
   * <li>V.dot(Transpose.of(V) == IdentityMatrix
   * <li>Transpose.of(V).dot(V) == IdentityMatrix
   * </ul>
   * 
   * @param matrix is rows x cols matrix with rows >= cols
   * @return singular value decomposition of given matrix
   * @throws Exception input is not a matrix, or if decomposition cannot be established */
  static SingularValueDecomposition of(Tensor matrix) {
    return new SingularValueDecompositionImpl(matrix);
  }

  /***************************************************/
  /** @return matrix of dimensions rows x cols */
  Tensor getU();

  /** Careful: the entries in the vector are not necessarily ordered
   * 
   * @return vector of non-negative singular values of length cols */
  Tensor values();

  /** @return square matrix of dimensions cols x cols */
  Tensor getV();
}
