// code by jph
package ch.alpine.tensor.mat.pd;

import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Throw;
import ch.alpine.tensor.Unprotect;
import ch.alpine.tensor.alg.Dimensions;
import ch.alpine.tensor.alg.PadRight;
import ch.alpine.tensor.mat.ConjugateTranspose;
import ch.alpine.tensor.mat.OrthogonalMatrixQ;
import ch.alpine.tensor.mat.UnitaryMatrixQ;
import ch.alpine.tensor.mat.qr.QRDecomposition;
import ch.alpine.tensor.mat.qr.QRMathematica;
import ch.alpine.tensor.mat.qr.QRSignOperators;

/** API inspired by Mathematica convention:
 * The matrices that are input to the functions in Orthogonalize are interpreted as list of vectors
 * <pre>
 * matrix = { v0, v1, v2 }
 * </pre>
 * 
 * So that an orthogonal output would be for instance
 * <pre>
 * Orthogonalize[matrix] = { v0/|v0|, v1/|v1| - v0*(v0.v1)/|v0||v1|, ... }
 * </pre>
 * 
 * <p>inspired by
 * <a href="https://reference.wolfram.com/language/ref/Orthogonalize.html">Orthogonalize</a>
 * 
 * @see PolarDecomposition
 * @see OrthogonalMatrixQ
 * @see UnitaryMatrixQ */
public enum Orthogonalize {
  ;
  /** preserves the successive subspaces of the sequence of vectors in the order given in matrix
   * For instance, if matrix = {v0, ...} then Orthogonalize[matrix] = { v0/|v0|, ... }.
   * 
   * @param matrix of dimensions n x m
   * @return matrix of dimensions n x m with pairwise orthogonal row vectors
   * @throws Exception if given matrix is not a tensor of rank 2 */
  public static Tensor of(Tensor matrix) {
    QRDecomposition qrDecomposition = //
        QRDecomposition.of(ConjugateTranspose.of(matrix), QRSignOperators.ORIENTATION);
    return PadRight.zeros(Dimensions.of(matrix)).apply(QRMathematica.wrap(qrDecomposition).getQConjugateTranspose());
  }

  // ---
  /** Least square orthogonal fit to given matrix
   * 
   * for input of square matrix, the function returns an orthogonal matrix with determinant +1
   * regardless of whether the input matrix has positive determinant.
   * 
   * Reference:
   * "Least-Squares Rigid Motion Using SVD"
   * Olga Sorkine-Hornung and Michael Rabinovich, 2016
   * 
   * @param matrix of size k x n with k <= n
   * @return matrix of size k x n that satisfies {@link OrthogonalMatrixQ} */
  public static Tensor usingSvd(Tensor matrix) {
    int k = matrix.length();
    int n = Unprotect.dimension1Hint(matrix);
    if (n < k) // case is forbidden to avoid confusion (despite functionally permissive)
      throw Throw.of(matrix);
    return k < n //
        ? PolarDecompositionSvd.pu(ConjugateTranspose.of(matrix)).getConjugateTransposeUnitary()
        : PolarDecompositionSvd.pu(matrix).getUnitaryWithDetOne();
  }

  /** Least square orthogonal fit to given matrix
   * 
   * expression appears in geomstats - stiefel.py for the creation of uniform distributed
   * random samples of orthogonal frames in the stiefel manifold St(n, k).
   * Subsequently, the function was used to reproject affine combinations of orthogonal matrices
   * back to the Lie group o(n), and so(n)
   * 
   * @param matrix of size k x n with k <= n
   * @return matrix of size k x n that satisfies {@link OrthogonalMatrixQ}
   * @throws Exception if given matrix does not have maximal rank k */
  public static Tensor usingPD(Tensor matrix) {
    return PolarDecomposition.pu(matrix).getUnitary();
  }
}
