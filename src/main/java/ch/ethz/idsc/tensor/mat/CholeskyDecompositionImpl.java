// code by jph
// adapted from wikipedia - Cholesky decomposition
package ch.ethz.idsc.tensor.mat;

import java.io.Serializable;

import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Scalars;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.TensorRuntimeException;
import ch.ethz.idsc.tensor.Unprotect;
import ch.ethz.idsc.tensor.alg.Array;
import ch.ethz.idsc.tensor.alg.Dimensions;
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
        Scalar aij = matrix.Get(i, j);
        chop.requireClose(Conjugate.FUNCTION.apply(aij), matrix.Get(j, i));
        final Scalar value;
        if (0 == j)
          value = aij;
        else {
          Tensor lik = l.get(i).extract(0, j);
          Tensor ljk = l.get(j).extract(0, j).map(Conjugate.FUNCTION);
          value = aij.subtract(lik.dot(d.extract(0, j).pmul(ljk)));
        }
        if (Scalars.nonZero(value))
          l.set(value.divide(d.Get(j)), i, j);
      }
      if (0 == i)
        d.set(matrix.Get(i, i), i);
      else {
        Tensor lik = l.get(i).extract(0, i);
        Tensor ljk = lik.map(Conjugate.FUNCTION); // variable name is deliberate
        d.set(matrix.Get(i, i).subtract(lik.dot(d.extract(0, i).pmul(ljk))), i);
      }
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
    return String.format("%s[L=%s, d=%s]", CholeskyDecomposition.class.getSimpleName(), Dimensions.of(getL()), Dimensions.of(diagonal()));
  }
}
