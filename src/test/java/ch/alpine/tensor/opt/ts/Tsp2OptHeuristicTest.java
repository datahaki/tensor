// code by jph
package ch.alpine.tensor.opt.ts;

import java.util.Random;

import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.lie.Symmetrize;
import ch.alpine.tensor.pdf.DiscreteUniformDistribution;
import ch.alpine.tensor.pdf.Distribution;
import ch.alpine.tensor.pdf.RandomVariate;
import ch.alpine.tensor.sca.Sign;
import junit.framework.TestCase;

public class Tsp2OptHeuristicTest extends TestCase {
  public void testSimple() {
    Distribution distribution = DiscreteUniformDistribution.of(-5, 5);
    for (int n = 1; n < 10; ++n) {
      Tensor tensor = Symmetrize.of(RandomVariate.of(distribution, n, n)).multiply(RealScalar.TWO);
      Tsp2OptHeuristic tspHeuristic = new Tsp2OptHeuristic(tensor, new Random());
      Scalar cost0 = tspHeuristic.cost();
      boolean next = tspHeuristic.next();
      if (next) {
        Scalar cost1 = tspHeuristic.cost();
        Sign.requirePositive(cost0.subtract(cost1));
      }
    }
  }
}
