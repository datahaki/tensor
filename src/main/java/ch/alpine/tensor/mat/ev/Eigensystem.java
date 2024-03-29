// code by jph
package ch.alpine.tensor.mat.ev;

import ch.alpine.tensor.ComplexScalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Throw;
import ch.alpine.tensor.alg.Flatten;
import ch.alpine.tensor.mat.HermitianMatrixQ;
import ch.alpine.tensor.mat.OrthogonalMatrixQ;
import ch.alpine.tensor.mat.SymmetricMatrixQ;
import ch.alpine.tensor.mat.Tolerance;
import ch.alpine.tensor.sca.Chop;

/** <pre>
 * LinearSolve.of(vectors, Times.of(values(), vectors)) == matrix
 * Transpose.of(vectors).dot(Times.of(values, vectors)) == matrix
 * </pre>
 * 
 * The eigenvalues of a symmetric, or hermitian matrix are real.
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
    return ofSymmetric(matrix, Tolerance.CHOP);
  }

  /** @param matrix
   * @param chop threshold to check symmetry of matrix
   * @return */
  static Eigensystem ofSymmetric(Tensor matrix, Chop chop) {
    return new EigensystemImpl(JacobiReal.eigensystem(SymmetricMatrixQ.require(matrix, chop)));
  }

  /** @param matrix hermitian
   * @return eigenvalue decomposition of given matrix
   * @see HermitianMatrixQ */
  static Eigensystem ofHermitian(Tensor matrix) {
    return ofHermitian(matrix, Tolerance.CHOP);
  }

  /** @param matrix hermitian
   * @param chop
   * @return eigenvalue decomposition of given matrix
   * @see HermitianMatrixQ */
  static Eigensystem ofHermitian(Tensor matrix, Chop chop) {
    return new EigensystemImpl(JacobiComplex.eigensystem(HermitianMatrixQ.require(matrix, chop)));
  }

  /** Careful: the general case is only for use with small matrices
   * 
   * @param matrix
   * @return */
  static Eigensystem of(Tensor matrix) {
    boolean isComplex = Flatten.stream(matrix, 1) //
        .anyMatch(scalar -> scalar instanceof ComplexScalar);
    if (!isComplex)
      return SymmetricMatrixQ.of(matrix) //
          ? ofSymmetric(matrix)
          : new RealEigensystem(matrix);
    if (HermitianMatrixQ.of(matrix))
      return ofHermitian(matrix);
    throw new Throw(matrix);
  }

  /** Careful: Mathematica orders the eigenvalues according to absolute value.
   * However, the tensor library does not guarantee any particular ordering.
   * 
   * @return vector of eigenvalues corresponding to the eigenvectors TODO TENSOR DOC */
  Tensor values();

  /** A*V = V*D
   * 
   * where
   * A is the original matrix
   * V = Transpose.of(vectors())
   * D = diagonalMatrix() */
  Tensor diagonalMatrix();

  /** @return matrix with rows as eigenvectors of given matrix
   * The eigenvectors are not necessarily scaled to unit length.
   * @see OrthogonalMatrixQ */
  Tensor vectors();
}
