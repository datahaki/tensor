// code by jph
package ch.ethz.idsc.tensor.opt.rn;

import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.alg.VectorQ;
import ch.ethz.idsc.tensor.mat.HermitianMatrixQ;
import ch.ethz.idsc.tensor.mat.IdentityMatrix;
import ch.ethz.idsc.tensor.pdf.NormalDistribution;
import ch.ethz.idsc.tensor.pdf.RandomVariate;
import ch.ethz.idsc.tensor.sca.Chop;
import ch.ethz.idsc.tensor.usr.AssertFail;
import junit.framework.TestCase;

public class LagrangeMultiplierTest extends TestCase {
  public void testLagrange() {
    Tensor eqs = RandomVariate.of(NormalDistribution.standard(), 3, 10);
    Tensor target = RandomVariate.of(NormalDistribution.standard(), 10);
    Tensor rhs = RandomVariate.of(NormalDistribution.standard(), 3);
    LagrangeMultiplier lagrangeMultiplier = new LagrangeMultiplier(IdentityMatrix.of(10), target, eqs, rhs);
    HermitianMatrixQ.require(lagrangeMultiplier.matrix());
    VectorQ.require(lagrangeMultiplier.b());
    Tensor sol1 = lagrangeMultiplier.linearSolve();
    Tensor sol2 = lagrangeMultiplier.usingSvd();
    assertEquals(sol1.length(), 10);
    Chop._08.requireClose(sol1, sol2);
  }

  public void testLagrange1Fail() {
    Tensor eqs = RandomVariate.of(NormalDistribution.standard(), 3, 10);
    Tensor target = RandomVariate.of(NormalDistribution.standard(), 9);
    Tensor rhs = RandomVariate.of(NormalDistribution.standard(), 3);
    AssertFail.of(() -> new LagrangeMultiplier(IdentityMatrix.of(10), target, eqs, rhs));
  }

  public void testLagrange2Fail() {
    Tensor eqs = RandomVariate.of(NormalDistribution.standard(), 3, 10);
    Tensor target = RandomVariate.of(NormalDistribution.standard(), 10);
    Tensor rhs = RandomVariate.of(NormalDistribution.standard(), 2);
    AssertFail.of(() -> new LagrangeMultiplier(IdentityMatrix.of(10), target, eqs, rhs));
  }
}
