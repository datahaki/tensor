// code by jph
package ch.alpine.tensor.mat.pi;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.alg.VectorQ;
import ch.alpine.tensor.mat.HermitianMatrixQ;
import ch.alpine.tensor.mat.IdentityMatrix;
import ch.alpine.tensor.mat.Tolerance;
import ch.alpine.tensor.pdf.Distribution;
import ch.alpine.tensor.pdf.RandomVariate;
import ch.alpine.tensor.pdf.c.ExponentialDistribution;
import ch.alpine.tensor.pdf.c.NormalDistribution;
import ch.alpine.tensor.pdf.c.TrapezoidalDistribution;
import ch.alpine.tensor.sca.Chop;

class LagrangeMultiplierTest {
  @Test
  void testLagrange() {
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

  @Test
  void testLagrangeCholeskyFail() {
    int n = 8;
    int r = 1;
    Distribution distribution = ExponentialDistribution.of(1);
    Tensor eqsPre = RandomVariate.of(NormalDistribution.standard(), r, n);
    Tensor eqsMul = RandomVariate.of(distribution, 3, r);
    Tensor eqs = eqsMul.dot(eqsPre);
    Tensor target = RandomVariate.of(NormalDistribution.standard(), n);
    Tensor rhs = RandomVariate.of(distribution, 3);
    LagrangeMultiplier lagrangeMultiplier = new LagrangeMultiplier(IdentityMatrix.of(n), target, eqs, rhs);
    HermitianMatrixQ.require(lagrangeMultiplier.matrix());
    VectorQ.require(lagrangeMultiplier.b());
    assertThrows(Exception.class, () -> lagrangeMultiplier.usingCholesky());
    Tensor sol2 = lagrangeMultiplier.usingSvd();
    Tensor sol3 = lagrangeMultiplier.solve();
    Tolerance.CHOP.requireClose(sol2, sol3);
    assertEquals(sol2.length(), n);
  }

  @Test
  void testLagrange1Fail() {
    Tensor eqs = RandomVariate.of(NormalDistribution.standard(), 3, 10);
    Tensor target = RandomVariate.of(NormalDistribution.standard(), 9);
    Tensor rhs = RandomVariate.of(NormalDistribution.standard(), 3);
    assertThrows(Exception.class, () -> new LagrangeMultiplier(IdentityMatrix.of(10), target, eqs, rhs));
  }

  @Test
  void testLagrange2Fail() {
    Tensor eqs = RandomVariate.of(NormalDistribution.standard(), 3, 10);
    Tensor target = RandomVariate.of(NormalDistribution.standard(), 10);
    Tensor rhs = RandomVariate.of(NormalDistribution.standard(), 2);
    assertThrows(Exception.class, () -> new LagrangeMultiplier(IdentityMatrix.of(10), target, eqs, rhs));
  }
}
