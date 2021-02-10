// code by jph
package ch.ethz.idsc.tensor.opt.rn;

import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.alg.VectorQ;
import ch.ethz.idsc.tensor.mat.HermitianMatrixQ;
import ch.ethz.idsc.tensor.mat.IdentityMatrix;
import ch.ethz.idsc.tensor.mat.Tolerance;
import ch.ethz.idsc.tensor.pdf.Distribution;
import ch.ethz.idsc.tensor.pdf.ExponentialDistribution;
import ch.ethz.idsc.tensor.pdf.NormalDistribution;
import ch.ethz.idsc.tensor.pdf.RandomVariate;
import ch.ethz.idsc.tensor.pdf.TrapezoidalDistribution;
import ch.ethz.idsc.tensor.sca.Chop;
import ch.ethz.idsc.tensor.usr.AssertFail;
import junit.framework.TestCase;

public class LagrangeMultiplierTest extends TestCase {
  public void testLagrange() {
    int n = 7;
    Distribution distribution = TrapezoidalDistribution.of(-2, -1, 1, 2);
    Tensor eqs = RandomVariate.of(distribution, 3, n);
    Tensor target = RandomVariate.of(NormalDistribution.standard(), n);
    Tensor rhs = RandomVariate.of(distribution, 3);
    LagrangeMultiplier lagrangeMultiplier = new LagrangeMultiplier(IdentityMatrix.of(n), target, eqs, rhs);
    HermitianMatrixQ.require(lagrangeMultiplier.matrix());
    VectorQ.require(lagrangeMultiplier.b());
    Tensor sol1 = lagrangeMultiplier.usingCholesky();
    Tensor sol2 = lagrangeMultiplier.usingSvd();
    Tensor sol3 = lagrangeMultiplier.solve();
    assertEquals(sol1.length(), n);
    Chop._08.requireClose(sol1, sol2);
    Tolerance.CHOP.requireClose(sol1, sol3);
  }

  public void testLagrangeCholeskyFail() {
    int n = 8;
    Distribution distribution = ExponentialDistribution.of(1);
    Tensor eqsPre = RandomVariate.of(NormalDistribution.standard(), 2, n);
    Tensor eqsMul = RandomVariate.of(distribution, 3, 2);
    Tensor eqs = eqsMul.dot(eqsPre);
    Tensor target = RandomVariate.of(NormalDistribution.standard(), n);
    Tensor rhs = RandomVariate.of(distribution, 3);
    LagrangeMultiplier lagrangeMultiplier = new LagrangeMultiplier(IdentityMatrix.of(n), target, eqs, rhs);
    HermitianMatrixQ.require(lagrangeMultiplier.matrix());
    VectorQ.require(lagrangeMultiplier.b());
    AssertFail.of(() -> lagrangeMultiplier.usingCholesky());
    Tensor sol2 = lagrangeMultiplier.usingSvd();
    Tensor sol3 = lagrangeMultiplier.solve();
    Tolerance.CHOP.requireClose(sol2, sol3);
    assertEquals(sol2.length(), n);
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
