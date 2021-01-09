// code by jph
package ch.ethz.idsc.tensor.mat;

import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Dot;
import ch.ethz.idsc.tensor.io.Pretty;
import ch.ethz.idsc.tensor.sca.Chop;
import ch.ethz.idsc.tensor.sca.Round;
import junit.framework.TestCase;

public class QREigensystemTest extends TestCase {
  public void testSymmetric() {
    Tensor matrix = Tensors.fromString("{{52, 30, 49, 28}, {30, 50, 8, 44}, {49, 8, 46, 16}, {28, 44, 16, 22}}");
    QREigensystem qrEigensystem = new QREigensystem(matrix, Chop._12);
    System.out.println(Pretty.of(qrEigensystem.qrDecomposition.getR().map(Round._2)));
    Tensor qn = qrEigensystem.vectors();
    OrthogonalMatrixQ.require(qn);
    Chop._08.requireClose(Dot.of(Inverse.of(qn), matrix, qn), qrEigensystem.qrDecomposition.getR());
    // System.out.println(Pretty.of(qrEigensystem.qrDecomposition.getR().map(Round._2)));
  }
  // public void testNonSymmetric() {
  // Tensor matrix = Tensors.fromString("{{52, 20, 19, 38}, {30, 50, 8, 44}, {49, 12, 46, 16}, {28, 44, 16, 22}}");
  // QREigensystem qrEigensystem = new QREigensystem(matrix, Chop._12);
  // System.out.println(Pretty.of(qrEigensystem.qrDecomposition.getR().map(Round._2)));
  // // System.out.println(Pretty.of(qrEigensystem.qrDecomposition.getR().map(Round._2)));
  // }
}
