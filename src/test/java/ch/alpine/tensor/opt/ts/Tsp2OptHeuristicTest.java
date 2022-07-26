// code by jph
package ch.alpine.tensor.opt.ts;

import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Random;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Throw;
import ch.alpine.tensor.ext.Integers;
import ch.alpine.tensor.lie.Symmetrize;
import ch.alpine.tensor.mat.HilbertMatrix;
import ch.alpine.tensor.pdf.Distribution;
import ch.alpine.tensor.pdf.RandomVariate;
import ch.alpine.tensor.pdf.c.UniformDistribution;
import ch.alpine.tensor.pdf.d.DiscreteUniformDistribution;
import ch.alpine.tensor.sca.Sign;

class Tsp2OptHeuristicTest {
  @Test
  void testSimple() {
    Distribution distribution = DiscreteUniformDistribution.of(-5, 5);
    Random random = new Random(1);
    for (int n = 1; n < 10; ++n) {
      Tensor tensor = Symmetrize.of(RandomVariate.of(distribution, random, n, n)).multiply(RealScalar.TWO);
      Tsp2OptHeuristic tsp2OptHeuristic = new Tsp2OptHeuristic(tensor, random);
      Scalar cost0 = tsp2OptHeuristic.cost();
      boolean next = tsp2OptHeuristic.next();
      Integers.requirePermutation(tsp2OptHeuristic.index());
      if (next) {
        Scalar cost1 = tsp2OptHeuristic.cost();
        Sign.requirePositive(cost0.subtract(cost1));
      }
    }
  }

  @Test
  void testIterate() {
    Distribution distribution = UniformDistribution.of(-5, 5);
    int n = 11;
    Random random = new Random(1);
    Tensor tensor = Symmetrize.of(RandomVariate.of(distribution, random, n, n));
    Tsp2OptHeuristic tsp2OptHeuristic = new Tsp2OptHeuristic(tensor, random);
    Scalar cost0 = tsp2OptHeuristic.cost();
    for (int count = 0; count < 10; ++count) {
      tsp2OptHeuristic.next();
      Integers.requirePermutation(tsp2OptHeuristic.index());
    }
    Scalar cost1 = tsp2OptHeuristic.cost();
    Sign.requirePositive(cost0.subtract(cost1));
  }

  @Test
  void testFail() {
    assertThrows(Throw.class, () -> new Tsp2OptHeuristic(HilbertMatrix.of(2, 3), new Random(1)));
  }
}
