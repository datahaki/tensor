// code by jph
package ch.ethz.idsc.tensor.lie;

import java.io.Serializable;

import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Scalars;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.Unprotect;
import ch.ethz.idsc.tensor.mat.ConjugateTranspose;
import ch.ethz.idsc.tensor.mat.Tolerance;
import ch.ethz.idsc.tensor.red.Diagonal;
import ch.ethz.idsc.tensor.red.Norm;
import ch.ethz.idsc.tensor.red.Times;

/** decomposition Q.R = A with Det[Q] == +1
 * householder with even number of reflections
 * reproduces example on wikipedia */
/* package */ class QRDecompositionImpl implements QRDecomposition, Serializable {
  private static final long serialVersionUID = 3564186473851271309L;
  // ---
  private final int n;
  private final int m;
  private Tensor Qinv;
  private Tensor R;

  /** @param matrix n x m
   * @param b is rhs, for instance IdentityMatrix[n]
   * @param qrSignOperator
   * @throws Exception if input is not a matrix */
  public QRDecompositionImpl(Tensor matrix, Tensor b, QRSignOperator qrSignOperator) {
    n = matrix.length();
    m = Unprotect.dimension1(matrix);
    Qinv = b;
    R = matrix;
    // the m-th reflection is necessary in the case where A is non-square
    for (int k = 0; k < m; ++k) {
      final int fk = k;
      Tensor x = Tensors.vector(i -> i < fk ? R.Get(i, fk).zero() : R.get(i, fk), n);
      Scalar xn = Norm._2.ofVector(x);
      if (Scalars.nonZero(xn)) { // else reflection reduces to identity, hopefully => det == 0
        Tensor signed = qrSignOperator.sign(R.Get(k, k)).multiply(xn);
        x.set(signed::add, k);
        QRReflection qrReflection = new QRReflection(k, x);
        Qinv = qrReflection.forward(Qinv);
        R = qrReflection.forward(R);
      }
    }
    // chop lower entries to symbolic zero
    for (int k = 0; k < m; ++k)
      for (int i = k + 1; i < n; ++i)
        R.set(Tolerance.CHOP, i, k);
  }

  @Override // from QRDecomposition
  public Tensor getInverseQ() {
    return Qinv; // n x n
  }

  @Override // from QRDecomposition
  public Tensor getR() {
    return R; // n x m
  }

  @Override // from QRDecomposition
  public Tensor getQ() {
    return ConjugateTranspose.of(getInverseQ()); // n x n
  }

  @Override // from QRDecomposition
  public Scalar det() {
    return n == m //
        ? Times.pmul(Diagonal.of(R)).Get()
        : RealScalar.ZERO;
  }

  public Tensor eliminate() {
    Tensor[] x = Qinv.stream().limit(m).toArray(Tensor[]::new);
    for (int i = m - 1; i >= 0; --i) {
      for (int j = i + 1; j < m; ++j)
        x[i] = x[i].subtract(x[j].multiply(R.Get(i, j)));
      x[i] = x[i].divide(R.Get(i, i));
    }
    return Unprotect.byRef(x);
  }
}
