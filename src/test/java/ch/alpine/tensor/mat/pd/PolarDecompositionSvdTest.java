// code by jph
package ch.alpine.tensor.mat.pd;

import java.io.IOException;
import java.util.Random;

import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.ext.Serialization;
import ch.alpine.tensor.mat.OrthogonalMatrixQ;
import ch.alpine.tensor.mat.PositiveDefiniteMatrixQ;
import ch.alpine.tensor.mat.Tolerance;
import ch.alpine.tensor.pdf.RandomVariate;
import ch.alpine.tensor.pdf.c.CauchyDistribution;
import ch.alpine.tensor.sca.Sqrt;
import junit.framework.TestCase;

public class PolarDecompositionSvdTest extends TestCase {
  public void testSvd() {
    Random random = new Random(3);
    Tensor matrix = RandomVariate.of(CauchyDistribution.standard(), random, 5, 3);
    PolarDecomposition pd_qs = PolarDecomposition.pu(matrix);
    Tolerance.CHOP.requireClose(pd_qs.getPositiveSemidefinite().dot(pd_qs.getUnitary()), matrix);
    PolarDecomposition pd_kq = PolarDecompositionSvd.pu(matrix);
    Tolerance.CHOP.requireClose(pd_kq.getPositiveSemidefinite().dot(pd_kq.getUnitary()), matrix);
  }

  public void testStrang() throws ClassNotFoundException, IOException {
    Tensor matrix = Tensors.fromString("{{3, 0}, {4, 5}}");
    PolarDecomposition polarDecomposition = Serialization.copy(PolarDecompositionSvd.up(matrix));
    Tensor q = polarDecomposition.getUnitary();
    OrthogonalMatrixQ.require(q);
    Tensor qe = Tensors.fromString("{{2, -1}, {1, 2}}").divide(Sqrt.FUNCTION.apply(RealScalar.of(5)));
    Tolerance.CHOP.requireClose(q, qe);
    Tensor s = polarDecomposition.getPositiveSemidefinite();
    Tolerance.CHOP.requireClose(q.dot(s), matrix);
    assertTrue(PositiveDefiniteMatrixQ.ofHermitian(s));
    Tensor se = Tensors.fromString("{{2, 1}, {1, 2}}").multiply(Sqrt.FUNCTION.apply(RealScalar.of(5)));
    Tolerance.CHOP.requireClose(s, se);
  }
}
