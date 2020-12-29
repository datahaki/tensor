// code by jph
package ch.ethz.idsc.tensor.mat;

import ch.ethz.idsc.tensor.Tensor;

/** <pre>
 * LinearSolve.of(vectors, values.pmul(vectors)) == matrix
 * Transpose.of(vectors).dot(values.pmul(vectors))
 * </pre>
 * 
 * <p>inspired by
 * <a href="https://reference.wolfram.com/language/ref/Eigensystem.html">Eigensystem</a> */
public interface Eigensystem {
  /** Quote from Compact Numerical Methods:
   * 1) All the eigenvalues of a real symmetric matrix are real.
   * 2) It is possible to find a complete set of n eigenvectors for an order-n real
   * symmetric matrix and these can be made mutually orthogonal.
   * 
   * @param matrix real symmetric, non-empty, and real valued
   * @return eigensystem with vectors scaled to unit length
   * @throws Exception if input is not a real symmetric matrix */
  static Eigensystem ofSymmetric(Tensor matrix) {
    return new JacobiMethod(matrix);
  }

  /** Careful: Mathematica orders the eigenvalues according to absolute value.
   * However, the tensor library does not guarantee any particular ordering.
   * 
   * @return vector of eigenvalues corresponding to the eigenvectors */
  Tensor values();

  /** @return matrix with rows as eigenvectors of given matrix
   * The eigenvectors are not necessarily scaled to unit length.
   * @see OrthogonalMatrixQ */
  Tensor vectors();
}
