// code by jph
package ch.alpine.tensor.mat.qr;

import java.io.Serializable;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.IntStream;

import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Scalars;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Unprotect;
import ch.alpine.tensor.ext.Integers;
import ch.alpine.tensor.mat.UpperTriangularize;
import ch.alpine.tensor.nrm.Vector2Norm;

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
    m = Integers.requirePositive(Unprotect.dimension1Hint(matrix));
    Tensor r = matrix;
    Tensor qInv = qInv0;
    for (int k = 0; k < m; ++k) { // m reflections
      // System.out.println(k);
      AtomicInteger atomicInteger = new AtomicInteger(-k);
      Tensor x = Tensor.of(r.get(Tensor.ALL, k).stream() // k-th column of R
          .map(Scalar.class::cast) //
          .map(scalar -> atomicInteger.getAndIncrement() < 0 ? scalar.zero() : scalar));
      Scalar xn = Vector2Norm.of(x);
      if (Scalars.nonZero(xn)) { // else reflection reduces to identity, hopefully => det == 0
        // System.out.println("---");
        Tensor signed = qrSignOperator.sign(x.Get(k)).multiply(xn);
        x.set(signed::add, k);
        QRReflection qrReflection = new QRReflection(k, x);
        qInv = qrReflection.forward(qInv);
        r = qrReflection.forward(r);
        // if (!FiniteTensorQ.of(r) || !FiniteTensorQ.of(qInv))
        // System.err.println("non-finite");
      }
    }
    this.r = r;
    this.qInv = qInv;
  }

  @Override // from QRDecomposition
  public Tensor getR() {
    return UpperTriangularize.of(r, 0); // n x m
  }

  @Override // from QRDecomposition
  public Tensor getQConjugateTranspose() {
    return qInv; // n x n
  }

  @Override // from QRDecomposition
  public int[] sigma() {
    return IntStream.range(0, m).toArray();
  }
}
