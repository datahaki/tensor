// code by jph
// adapted from wikipedia - Cholesky decomposition
package ch.ethz.idsc.tensor.mat;

import java.io.Serializable;

import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Scalars;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.TensorRuntimeException;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.Unprotect;
import ch.ethz.idsc.tensor.alg.Array;
import ch.ethz.idsc.tensor.red.Times;
import ch.ethz.idsc.tensor.sca.Chop;
import ch.ethz.idsc.tensor.sca.Conjugate;

/* package */ class CholeskyDecompositionImpl implements CholeskyDecomposition, Serializable {
  private static final long serialVersionUID = 7823195931193369647L;
  // ---
  private final Chop chop;
  private final Tensor l;
  private final Tensor d;

  /** @param matrix hermitian and positive semi-definite matrix
   * @param chop for check if given matrix is hermitian
   * @throws Exception if given matrix is not hermitian */
  public CholeskyDecompositionImpl(Tensor matrix, Chop chop) {
    this.chop = chop;
    int n = matrix.length();
    l = IdentityMatrix.of(matrix);
    Scalar zero = matrix.Get(0, 0).zero();
    d = Array.fill(() -> zero, n);
    for (int i = 0; i < n; ++i) {
      for (int j = 0; j < i; ++j) {
        Scalar mij = matrix.Get(i, j);
        chop.requireClose(Conjugate.FUNCTION.apply(mij), matrix.Get(j, i));
        for (int k = 0; k < j; ++k)
          mij = mij.subtract(l.Get(i, k).multiply(d.Get(k)).multiply(Conjugate.FUNCTION.apply(l.Get(j, k))));
        if (Scalars.nonZero(mij))
          l.set(mij.divide(d.Get(j)), i, j);
      }
      Scalar mii = matrix.Get(i, i);
      for (int k = 0; k < i; ++k) {
        Scalar lik = l.Get(i, k);
        mii = mii.subtract(lik.multiply(d.Get(k)).multiply(Conjugate.FUNCTION.apply(lik)));
      }
      d.set(mii, i);
    }
  }

  @Override // from CholeskyDecomposition
  public Tensor getL() {
    return l;
  }

  @Override // from CholeskyDecomposition
  public Tensor diagonal() {
    return d;
  }

  @Override // from CholeskyDecomposition
  public Scalar det() {
    return (Scalar) Times.pmul(d);
  }

  @Override // from CholeskyDecomposition
  public Tensor solve(Tensor b) {
    d.stream().map(Scalar.class::cast).forEach(chop::requireNonZero);
    int n = l.length();
    if (b.length() != n)
      throw TensorRuntimeException.of(l, b);
    Tensor[] x = b.stream().toArray(Tensor[]::new);
    for (int i = 0; i < n; ++i)
      for (int k = i - 1; 0 <= k; --k)
        x[i] = x[i].subtract(x[k].multiply(l.Get(i, k)));
    for (int i = 0; i < n; ++i)
      x[i] = x[i].divide(d.Get(i));
    for (int i = n - 1; 0 <= i; --i)
      for (int k = i + 1; k < n; ++k)
        x[i] = x[i].subtract(x[k].multiply(Conjugate.FUNCTION.apply(l.Get(k, i))));
    return Unprotect.byRef(x);
  }

  @Override // from Object
  public String toString() {
    return String.format("%s[%s]", //
        CholeskyDecomposition.class.getSimpleName(), //
        Tensors.message(diagonal(), getL()));
  }
}
