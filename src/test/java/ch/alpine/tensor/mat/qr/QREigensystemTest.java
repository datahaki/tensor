// code by jph
package ch.alpine.tensor.mat.qr;

import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.alg.Dot;
import ch.alpine.tensor.mat.OrthogonalMatrixQ;
import ch.alpine.tensor.mat.re.Inverse;
import ch.alpine.tensor.sca.Chop;
import junit.framework.TestCase;

public class QREigensystemTest extends TestCase {
  public void testSymmetric() {
    Tensor matrix = Tensors.fromString("{{52, 30, 49, 28}, {30, 50, 8, 44}, {49, 8, 46, 16}, {28, 44, 16, 22}}");
    QREigensystem qrEigensystem = new QREigensystem(matrix, Chop._12);
    // System.out.println(Pretty.of(qrEigensystem.qrDecomposition.getR().map(Round._2)));
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
