// code by jph
package ch.alpine.tensor.pdf;

import java.util.Random;

import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Scalars;
import ch.alpine.tensor.red.Mean;
import ch.alpine.tensor.red.Variance;
import ch.alpine.tensor.sca.Sign;
import junit.framework.Assert;

/** Reference:
 * "Linear Algebra and Learning from data" by Gilbert Strang, 2019 */
/* package */ enum TestHelper {
  ;
  public static void markov(Distribution distribution) {
    Random random = new Random();
    Scalar xbar = Mean.of(distribution);
    Sign.requirePositiveOrZero(xbar);
    Scalar a = Sign.isPositive(xbar) //
        ? RealScalar.of(2 * random.nextDouble()).multiply(xbar)
        : RealScalar.of(2 * random.nextDouble());
    Scalar prob = RealScalar.ONE.subtract(CDF.of(distribution).p_lessThan(a));
    Assert.assertTrue(Scalars.lessEquals(prob, xbar.divide(a)));
  }

  public static void chebyshev(Distribution distribution) {
    Random random = new Random();
    Scalar xbar = Mean.of(distribution);
    Scalar a = Scalars.nonZero(xbar) //
        ? RealScalar.of(2 * random.nextDouble()).multiply(xbar)
        : RealScalar.of(2 * random.nextDouble());
    Scalar lo = CDF.of(distribution).p_lessThan(xbar.subtract(a));
    Scalar hi = RealScalar.ONE.subtract(CDF.of(distribution).p_lessThan(xbar.add(a)));
    Scalar sigmaSq = Variance.of(distribution);
    Assert.assertTrue(Scalars.lessEquals(lo.add(hi), sigmaSq.divide(a).divide(a)));
  }
}
