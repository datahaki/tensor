// code by jph
package ch.alpine.tensor.mat.qr;

import java.util.Objects;

import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.alg.Transpose;
import ch.alpine.tensor.mat.DiagonalMatrix;
import ch.alpine.tensor.mat.DiagonalMatrixQ;
import ch.alpine.tensor.mat.ev.Eigensystem;
import ch.alpine.tensor.red.Diagonal;
import ch.alpine.tensor.sca.Chop;

/* package */ enum QREigensystem {
  ;
  private static final int MAX_ITERATIONS = 20;
  // ---

  public static Eigensystem of(Tensor matrix, Chop chop) {
    QRDecomposition qrDecomposition;
    Tensor qn = null;
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
    return new Eigensystem( //
        Diagonal.of(DiagonalMatrixQ.require(qrDecomposition.getR())), //
        Transpose.of(qn));
  }
}
