// code by jph
package ch.alpine.tensor.mat.pi;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.alg.VectorQ;
import ch.alpine.tensor.mat.HermitianMatrixQ;
import ch.alpine.tensor.mat.Tolerance;
import ch.alpine.tensor.pdf.Distribution;
import ch.alpine.tensor.pdf.RandomVariate;
import ch.alpine.tensor.pdf.c.ExponentialDistribution;
import ch.alpine.tensor.pdf.c.NormalDistribution;
import ch.alpine.tensor.pdf.c.TrapezoidalDistribution;
import ch.alpine.tensor.sca.Chop;

class LagrangeMultiplierTest {
  @ParameterizedTest
  @ValueSource(ints = { 5, 6, 7 })
  void testLagrange(int n) {
    Distribution distribution = TrapezoidalDistribution.of(-2, -1, 1, 2);
    Tensor eqs = RandomVariate.of(distribution, 3, n);
    Tensor target = RandomVariate.of(NormalDistribution.standard(), n);
    Tensor rhs = RandomVariate.of(distribution, 3);
    LagrangeMultiplier lagrangeMultiplier = LagrangeMultiplier.id(eqs);
    HermitianMatrixQ.INSTANCE.require(lagrangeMultiplier.matrix());
    VectorQ.require(lagrangeMultiplier.b(target, rhs));
    Tensor sol1 = lagrangeMultiplier.usingCholesky(target, rhs);
    Tensor sol2 = lagrangeMultiplier.usingSvd(target, rhs);
    Tensor sol3 = lagrangeMultiplier.solve(target, rhs);
    assertEquals(sol1.length(), n);
    Chop._08.requireClose(sol1, sol2);
    Tolerance.CHOP.requireClose(sol1, sol3);
  }

  @ParameterizedTest
  @ValueSource(ints = { 5, 6, 8 })
  void testLagrangeCholeskyFail(int n) {
    int r = 1;
    Distribution distribution = ExponentialDistribution.of(1);
    Tensor eqsPre = RandomVariate.of(NormalDistribution.standard(), r, n);
    Tensor eqsMul = RandomVariate.of(distribution, 3, r);
    Tensor eqs = eqsMul.dot(eqsPre);
    Tensor target = RandomVariate.of(NormalDistribution.standard(), n);
    Tensor rhs = RandomVariate.of(distribution, 3);
    LagrangeMultiplier lagrangeMultiplier = LagrangeMultiplier.id(eqs);
    HermitianMatrixQ.INSTANCE.require(lagrangeMultiplier.matrix());
    VectorQ.require(lagrangeMultiplier.b(target, rhs));
    assertThrows(Exception.class, () -> lagrangeMultiplier.usingCholesky(target, rhs));
    Tensor sol2 = lagrangeMultiplier.usingSvd(target, rhs);
    Tensor sol3 = lagrangeMultiplier.solve(target, rhs);
    Tolerance.CHOP.requireClose(sol2, sol3);
    assertEquals(sol2.length(), n);
    assertTrue(lagrangeMultiplier.toString().startsWith("LagrangeMultiplier["));
  }

  @Test
  void testLagrange1Fail() {
    Tensor eqs = RandomVariate.of(NormalDistribution.standard(), 3, 10);
    Tensor target = RandomVariate.of(NormalDistribution.standard(), 9);
    Tensor rhs = RandomVariate.of(NormalDistribution.standard(), 3);
    assertThrows(Exception.class, () -> LagrangeMultiplier.id(eqs).b(target, rhs));
  }

  @Test
  void testLagrange2Fail() {
    Tensor eqs = RandomVariate.of(NormalDistribution.standard(), 3, 10);
    Tensor target = RandomVariate.of(NormalDistribution.standard(), 10);
    Tensor rhs = RandomVariate.of(NormalDistribution.standard(), 2);
    assertThrows(Exception.class, () -> LagrangeMultiplier.id(eqs).b(target, rhs));
  }
}
