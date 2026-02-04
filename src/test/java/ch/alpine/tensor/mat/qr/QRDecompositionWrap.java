// code by jph
package ch.alpine.tensor.mat.qr;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.HashMap;
import java.util.Map;

import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.mat.IdentityMatrix;
import ch.alpine.tensor.mat.LowerTriangularize;
import ch.alpine.tensor.mat.SquareMatrixQ;
import ch.alpine.tensor.mat.re.Det;
import ch.alpine.tensor.red.Diagonal;
import ch.alpine.tensor.sca.Chop;

public enum QRDecompositionWrap {
  ;
  public static QRDecomposition of(Tensor matrix) {
    return of(matrix, QRSignOperators.STABILITY);
  }

  public static QRDecomposition of(Tensor matrix, QRSignOperator qrSignOperator) {
    return of(matrix, IdentityMatrix.of(matrix.length()), qrSignOperator);
  }

  public static QRDecomposition of(Tensor A, Tensor qInv0, QRSignOperator qrSign) {
    Map<QRSignOperator, QRDecomposition> map = new HashMap<>();
    for (QRSignOperator qrSignOperator : QRSignOperators.values()) {
      QRDecomposition qrDecomposition = QRDecomposition.of(A, qInv0, qrSignOperator);
      Tensor Q = qrDecomposition.getQ();
      Tensor Qi = qrDecomposition.getQConjugateTranspose();
      Tensor R = qrDecomposition.getR();
      Chop._10.requireClose(Qi.dot(A), R);
      if (SquareMatrixQ.INSTANCE.isMember(A)) {
        // Scalar detA = Det.of(A);
        if (!Chop._10.isZero(qrDecomposition.det())) {
          // TODO TENSOR also check pseudoInverse
          // Chop._10.requireClose(A.dot(qrDecomposition.pseudoInverse()), Qi);
        }
      }
      if (qInv0.equals(IdentityMatrix.of(A.length()))) {
        // TODO TENSOR treat else case
        Chop._10.requireClose(Q.dot(R), A);
        Chop._10.requireClose(Q.dot(Qi), IdentityMatrix.of(A.length()));
        Scalar detR = Diagonal.of(R).stream().map(Scalar.class::cast).reduce(Scalar::multiply).get();
        Scalar qrDet = Det.of(Q).multiply(detR);
        if (SquareMatrixQ.INSTANCE.isMember(A)) {
          Scalar detA = Det.of(A);
          Chop._10.requireClose(qrDet, detA);
          Tensor lower = LowerTriangularize.of(R, -1);
          Chop.NONE.requireAllZero(lower);
          if (qrSignOperator.isDetExact()) {
            Chop._10.requireClose(qrDet, qrDecomposition.det());
          } else {
            assertTrue(Chop._10.isClose(qrDet, qrDecomposition.det()) || Chop._10.isClose(qrDet, qrDecomposition.det().negate()));
          }
        }
      }
      map.put(qrSignOperator, qrDecomposition);
    }
    return map.get(qrSign);
  }
}
