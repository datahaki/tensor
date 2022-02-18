// code by jph
package ch.alpine.tensor.mat;

import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.mat.sv.SingularValueDecomposition;
import ch.alpine.tensor.sca.Sqrt;
import junit.framework.TestCase;

public class PolarDecompositionSvdTest extends TestCase {
  public void testStrang() {
    Tensor matrix = Tensors.fromString("{{3, 0}, {4, 5}}");
    PolarDecompositionSvd polarDecompositionSvd = new PolarDecompositionSvd(SingularValueDecomposition.of(matrix));
    Tensor q = polarDecompositionSvd.getQ();
    OrthogonalMatrixQ.require(q);
    Tensor qe = Tensors.fromString("{{2, -1}, {1, 2}}").divide(Sqrt.FUNCTION.apply(RealScalar.of(5)));
    Tolerance.CHOP.requireClose(q, qe);
    Tensor s = polarDecompositionSvd.getS();
    Tolerance.CHOP.requireClose(q.dot(s), matrix);
  }
}
