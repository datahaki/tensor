// code by jph
package ch.alpine.tensor.mat.qr;

import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.mat.OrthogonalMatrixQ;
import ch.alpine.tensor.mat.Tolerance;
import ch.alpine.tensor.pdf.NormalDistribution;
import ch.alpine.tensor.pdf.RandomVariate;
import junit.framework.TestCase;

public class GramSchmidtTest extends TestCase {
  public void testSimple() {
    Tensor matrix = RandomVariate.of(NormalDistribution.standard(), 5, 4);
    GramSchmidt gramSchmidt = new GramSchmidt(matrix);
    Tensor res = gramSchmidt.getQ().dot(gramSchmidt.getR());
    Tolerance.CHOP.requireClose(matrix, res);
    OrthogonalMatrixQ.require(gramSchmidt.getQConjugateTranspose());
    // System.out.println(Pretty.of(gramSchmidt.getR().map(Round._3)));
  }
}
