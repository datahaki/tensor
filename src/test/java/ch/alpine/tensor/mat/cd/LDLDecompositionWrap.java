// code by jph
package ch.alpine.tensor.mat.cd;

import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Unprotect;
import ch.alpine.tensor.mat.ConjugateTranspose;
import ch.alpine.tensor.mat.IdentityMatrix;
import ch.alpine.tensor.mat.Tolerance;
import ch.alpine.tensor.mat.re.Det;
import ch.alpine.tensor.mat.re.Inverse;
import ch.alpine.tensor.red.Times;
import ch.alpine.tensor.sca.Chop;

public enum LDLDecompositionWrap {
  ;
  public static LDLDecomposition of(Tensor matrix) {
    return of(matrix, Tolerance.CHOP);
  }

  /** @param matrix hermitian and positive semi-definite matrix
   * @param chop
   * @return Cholesky decomposition of matrix
   * @throws Exception if matrix is not hermitian, or decomposition failed */
  public static LDLDecomposition of(Tensor matrix, Chop chop) {
    // matrix == getL().dot(Times.of(diagonal(), ConjugateTranspose.of(getL())));
    LDLDecomposition ldlDecomposition = LDLDecomposition.of(matrix, chop);
    Scalar det = Det.of(matrix);
    chop.requireClose(ldlDecomposition.det(), det);
    Tensor L = ldlDecomposition.getL();
    Tensor diagonal = ldlDecomposition.diagonal();
    Tensor ctL = ConjugateTranspose.of(ldlDecomposition.getL());
    if (Unprotect.isUnitUnique(matrix)) {
      Tensor chk = L.dot(Times.of(diagonal, ctL));
      chop.requireClose(matrix, chk);
    }
    if (!chop.isZero(det)) {
      Tensor solve = ldlDecomposition.solve(IdentityMatrix.of(matrix));
      chop.requireClose(solve, Inverse.of(matrix));
    }
    return ldlDecomposition;
  }
}
