// code by jph
package ch.alpine.tensor.mat.qr;

import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.TensorRuntimeException;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.Unprotect;
import ch.alpine.tensor.mat.Tolerance;
import ch.alpine.tensor.mat.UpperTriangularize;
import ch.alpine.tensor.red.LenientAdd;
import ch.alpine.tensor.red.Max;
import ch.alpine.tensor.red.Min;
import ch.alpine.tensor.sca.Abs;

/** utility class for {@link QRDecompositionBase}
 * 
 * class is not public, since the input is not checked for correctness,
 * i.e. whether matrix is {@link UpperTriangularize} */
/* package */ enum RSolve {
  ;
  public static Tensor of(QRDecomposition qrDecomposition, Tensor rhs) {
    return of(qrDecomposition.getR(), qrDecomposition.sigma(), rhs);
  }

  /** method uses back substitution
   * 
   * @param r upper triangular matrix under given permutation sigma
   * @param rhs
   * @param sigma
   * @return LinearSolve[r, rhs] */
  public static Tensor of(Tensor r, int[] sigma, Tensor rhs) {
    int m = sigma.length;
    failFast(r, sigma);
    Tensor[] x = new Tensor[m];
    for (int i = m - 1; i >= 0; --i) {
      int k = sigma[i];
      x[k] = rhs.get(i);
      for (int j = i + 1; j < m; ++j)
        x[k] = LenientAdd.of(x[k], x[sigma[j]].multiply(r.Get(i, sigma[j]).negate()));
      x[k] = x[k].divide(r.Get(i, k));
    }
    return Unprotect.byRef(x);
  }

  private static void failFast(Tensor r, int[] sigma) {
    // TODO refactor with Pivot
    Tensor diag = Tensors.vector(i -> Abs.FUNCTION.apply(Unprotect.withoutUnit(r.Get(i, sigma[i]))), sigma.length);
    Scalar max = (Scalar) diag.stream().reduce(Max::of).orElseThrow();
    Scalar min = (Scalar) diag.stream().reduce(Min::of).orElseThrow();
    if (Tolerance.CHOP.isZero(min) || //
        Tolerance.CHOP.isZero(min.divide(max)))
      throw TensorRuntimeException.of(max, min);
  }
}
