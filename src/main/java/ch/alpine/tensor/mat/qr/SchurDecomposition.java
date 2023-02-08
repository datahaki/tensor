// code by jph
package ch.alpine.tensor.mat.qr;

import java.util.Objects;

import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Throw;
import ch.alpine.tensor.mat.LowerTriangularize;
import ch.alpine.tensor.mat.re.LinearSolve;
import ch.alpine.tensor.sca.Chop;

/** <p>inspired by
 * <a href="https://reference.wolfram.com/language/ref/SchurDecomposition.html">SchurDecomposition</a> */
public class SchurDecomposition {
  private static final int MAX_ITERATIONS = 50;

  public static SchurDecomposition of(Tensor matrix) {
    return new SchurDecomposition(matrix, Chop._10);
  }

  // ---
  private QRDecomposition qrDecomposition;
  private Tensor qn;

  private SchurDecomposition(final Tensor matrix, Chop chop) {
    Tensor m = matrix;
    qrDecomposition = QRDecomposition.of(m);
    for (int c0 = 0; c0 < m.length(); ++c0) {
      for (int index = 0; index < MAX_ITERATIONS; ++index) {
        Tensor r = qrDecomposition.getR();
        Tensor q = qrDecomposition.getQ();
        qn = Objects.isNull(qn) ? q : qn.dot(q);
        // rather check if difference converges to 0 !?
        m = r.dot(q);
        qrDecomposition = QRDecomposition.of(m);
      }
      Tensor res = LowerTriangularize.of(LinearSolve.of(qn, matrix).dot(qn), -1);
      if (chop.allZero(res))
        return;
    }
    throw new Throw(m);
  }

  public Tensor getR() {
    return qrDecomposition.getR();
  }

  public Tensor getQ() {
    return qn;
  }
}
