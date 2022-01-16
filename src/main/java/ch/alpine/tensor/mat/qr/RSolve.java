// code by jph
package ch.alpine.tensor.mat.qr;

import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.TensorRuntimeException;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.Unprotect;
import ch.alpine.tensor.mat.Tolerance;
import ch.alpine.tensor.red.Max;
import ch.alpine.tensor.red.Min;
import ch.alpine.tensor.sca.Abs;

/** utility class for {@link QRDecompositionBase} */
/* package */ enum RSolve {
  ;
  /** @param r upper triangular matrix under given permutation sigma
   * @param rhs
   * @param sigma
   * @return LinearSolve[r, rhs] */
  public static Tensor of(Tensor r, Tensor rhs, int[] sigma) {
    int m = sigma.length;
    failFast(r, sigma);
    Tensor[] x = new Tensor[m];
    for (int i = m - 1; i >= 0; --i) {
      int k = sigma[i];
      x[k] = rhs.get(i);
      for (int j = i + 1; j < m; ++j)
        x[k] = x[k].subtract(x[sigma[j]].multiply(r.Get(i, sigma[j])));
      x[k] = x[k].divide(r.Get(i, k));
    }
    return Unprotect.byRef(x);
  }

  private static void failFast(Tensor r, int[] sigma) {
    Tensor diag = Tensors.vector(i -> Abs.FUNCTION.apply(r.Get(i, sigma[i])), sigma.length);
    Scalar max = (Scalar) diag.stream().reduce(Max::of).orElseThrow();
    Scalar min = (Scalar) diag.stream().reduce(Min::of).orElseThrow();
    if (Tolerance.CHOP.isZero(min) || //
        Tolerance.CHOP.isZero(min.divide(max)))
      throw TensorRuntimeException.of(max, min);
  }
}
