// code by jph
package ch.ethz.idsc.tensor.mat;

import java.io.Serializable;
import java.util.concurrent.atomic.AtomicInteger;

import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Scalars;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Unprotect;
import ch.ethz.idsc.tensor.alg.Dimensions;
import ch.ethz.idsc.tensor.ext.Integers;
import ch.ethz.idsc.tensor.red.Diagonal;
import ch.ethz.idsc.tensor.red.Norm;
import ch.ethz.idsc.tensor.red.Times;
import ch.ethz.idsc.tensor.sca.Chop;

/** decomposition Q.R = A with Det[Q] == +1
 * householder with even number of reflections
 * reproduces example on wikipedia */
/* package */ class QRDecompositionImpl implements QRDecomposition, Serializable {
  private static final long serialVersionUID = -4880290968594939778L;
  // ---
  private final int m;
  private final Tensor R;
  private final Tensor Qinv;

  /** @param matrix n x m
   * @param b is rhs, for instance IdentityMatrix[n]
   * @param qrSignOperator
   * @throws Exception if input is not a matrix */
  public QRDecompositionImpl(Tensor matrix, Tensor b, QRSignOperator qrSignOperator) {
    int n = matrix.length();
    m = Integers.requirePositive(Unprotect.dimension1Hint(matrix));
    Tensor R = matrix;
    Tensor Qinv = b;
    for (int k = 0; k < m; ++k) { // m reflections
      AtomicInteger atomicInteger = new AtomicInteger(-k);
      Tensor x = Tensor.of(R.get(Tensor.ALL, k).stream() // k-th column of R
          .map(Scalar.class::cast) //
          .map(scalar -> atomicInteger.getAndIncrement() < 0 ? scalar.zero() : scalar));
      Scalar xn = Norm._2.ofVector(x);
      if (Scalars.nonZero(xn)) { // else reflection reduces to identity, hopefully => det == 0
        Tensor signed = qrSignOperator.sign(x.Get(k)).multiply(xn);
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
    this.R = R;
    this.Qinv = Qinv;
  }

  @Override // from QRDecomposition
  public Tensor getR() {
    return R; // n x m
  }

  @Override // from QRDecomposition
  public Tensor getInverseQ() {
    return Qinv; // n x n
  }

  @Override // from QRDecomposition
  public Tensor getQ() {
    return ConjugateTranspose.of(getInverseQ()); // n x n
  }

  @Override // from QRDecomposition
  public Scalar det() {
    return R.length() == m // check if R is square
        ? Times.pmul(Diagonal.of(R)).Get()
        : RealScalar.ZERO;
  }

  /** @param chop
   * @return PseudoInverse[matrix] . b
   * @throws Exception if division by zero occurs */
  public Tensor pseudoInverse(Chop chop) {
    Tensor[] x = Qinv.stream().limit(m).toArray(Tensor[]::new);
    for (int i = m - 1; i >= 0; --i) {
      for (int j = i + 1; j < m; ++j)
        x[i] = x[i].subtract(x[j].multiply(R.Get(i, j)));
      x[i] = x[i].divide(chop.requireNonZero(R.Get(i, i)));
    }
    return Unprotect.byRef(x);
  }

  @Override // from Object
  public String toString() {
    return String.format("%s[Q=%s, R=%s]", QRDecomposition.class.getSimpleName(), Dimensions.of(getQ()), Dimensions.of(getR()));
  }
}
