// code by jph
package ch.alpine.tensor.mat.qr;

import java.io.Serializable;
import java.util.Objects;

import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.mat.DiagonalMatrix;
import ch.alpine.tensor.mat.ev.Eigensystem;
import ch.alpine.tensor.red.Diagonal;
import ch.alpine.tensor.sca.Chop;

/* package */ class QREigensystem implements Eigensystem, Serializable {
  private static final int MAX_ITERATIONS = 20;
  // ---
  QRDecomposition qrDecomposition;
  Tensor qn;

  public QREigensystem(Tensor matrix, Chop chop) {
    int max = matrix.length() * MAX_ITERATIONS;
    qrDecomposition = QRDecomposition.of(matrix);
    int index = 0;
    for (; index < max; ++index) {
      Tensor r = qrDecomposition.getR();
      Tensor q = qrDecomposition.getQ();
      qn = Objects.isNull(qn) ? q : qn.dot(q);
      // rather check if difference converges to 0 !?
      if (chop.isClose(r, DiagonalMatrix.with(Diagonal.of(r)))) {
        // System.out.println("finish at " + index);
        break;
      }
      matrix = r.dot(q);
      qrDecomposition = QRDecomposition.of(matrix);
    }
  }

  @Override
  public Tensor values() {
    return qrDecomposition.getR();
  }

  @Override
  public Tensor diagonalMatrix() {
    return DiagonalMatrix.with(values());
  }

  @Override
  public Tensor vectors() {
    return qn;
  }
}
