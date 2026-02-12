// code by jph
package ch.alpine.tensor.mat.qr;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.mat.OrthogonalMatrixQ;
import ch.alpine.tensor.mat.ev.Eigensystem;
import ch.alpine.tensor.sca.Chop;
import test.wrap.EigensystemQ;

class QREigensystemTest {
  @Test
  void testSymmetric() {
    Tensor matrix = Tensors.fromString("{{52, 30, 49, 28}, {30, 50, 8, 44}, {49, 8, 46, 16}, {28, 44, 16, 22}}");
    Eigensystem qrEigensystem = QREigensystem.of(matrix, Chop._12);
    // System.out.println(Pretty.of(qrEigensystem.qrDecomposition.getR().map(Round._2)));
    // IO.println(qrEigensystem.values());
    Tensor qn = qrEigensystem.vectors();
    OrthogonalMatrixQ.INSTANCE.require(qn);
    // Chop._08.requireClose(Dot.of(Inverse.of(qn), matrix, qn), qrEigensystem.qrDecomposition.getR());
    // System.out.println(Pretty.of(qrEigensystem.qrDecomposition.getR().map(Round._2)));
    new EigensystemQ(matrix).require(qrEigensystem, Chop._08);
  }
  // public void testNonSymmetric() {
  // Tensor matrix = Tensors.fromString("{{52, 20, 19, 38}, {30, 50, 8, 44}, {49, 12, 46, 16}, {28, 44, 16, 22}}");
  // QREigensystem qrEigensystem = new QREigensystem(matrix, Chop._12);
  // System.out.println(Pretty.of(qrEigensystem.qrDecomposition.getR().map(Round._2)));
  // // System.out.println(Pretty.of(qrEigensystem.qrDecomposition.getR().map(Round._2)));
  // }
}
