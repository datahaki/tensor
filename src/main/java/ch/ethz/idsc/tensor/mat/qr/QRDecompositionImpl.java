// code by jph
package ch.ethz.idsc.tensor.mat.qr;

import java.io.Serializable;
import java.util.concurrent.atomic.AtomicInteger;

import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Scalars;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Unprotect;
import ch.ethz.idsc.tensor.ext.Integers;
import ch.ethz.idsc.tensor.mat.Tolerance;
import ch.ethz.idsc.tensor.nrm.Vector2Norm;
import ch.ethz.idsc.tensor.red.Diagonal;
import ch.ethz.idsc.tensor.red.Times;

/** decomposition Q.R = A with Det[Q] == +1
 * householder with even number of reflections
 * reproduces example on wikipedia */
/* package */ class QRDecompositionImpl extends QRDecompositionBase implements Serializable {
  private final int m;
  private final Tensor r;
  private final Tensor qInv;

  /** @param matrix n x m
   * @param qInv0 for initialization of "Q-Inverse", for instance IdentityMatrix[n]
   * or b when the least squares solution x to matrix.x ~ b should be found.
   * @param qrSignOperator
   * @throws Exception if input is not a matrix */
  public QRDecompositionImpl(Tensor matrix, Tensor qInv0, QRSignOperator qrSignOperator) {
    int n = matrix.length();
    m = Integers.requirePositive(Unprotect.dimension1Hint(matrix));
    Tensor r = matrix;
    Tensor qInv = qInv0;
    for (int k = 0; k < m; ++k) { // m reflections
      AtomicInteger atomicInteger = new AtomicInteger(-k);
      Tensor x = Tensor.of(r.get(Tensor.ALL, k).stream() // k-th column of R
          .map(Scalar.class::cast) //
          .map(scalar -> atomicInteger.getAndIncrement() < 0 ? scalar.zero() : scalar));
      Scalar xn = Vector2Norm.of(x);
      if (Scalars.nonZero(xn)) { // else reflection reduces to identity, hopefully => det == 0
        Tensor signed = qrSignOperator.sign(x.Get(k)).multiply(xn);
        x.set(signed::add, k);
        QRReflection qrReflection = new QRReflection(k, x);
        qInv = qrReflection.forward(qInv);
        r = qrReflection.forward(r);
      }
    }
    // chop lower entries to symbolic zero
    for (int k = 0; k < m; ++k)
      for (int i = k + 1; i < n; ++i)
        r.set(Tolerance.CHOP, i, k);
    this.r = r;
    this.qInv = qInv;
  }

  @Override // from QRDecomposition
  public Tensor getR() {
    return r; // n x m
  }

  @Override // from QRDecomposition
  public Tensor getQTranspose() {
    return qInv; // n x n
  }

  @Override // from QRDecomposition
  public Scalar det() {
    return r.length() == m // check if R is square
        ? (Scalar) Times.pmul(Diagonal.of(r))
        : RealScalar.ZERO;
  }
}
