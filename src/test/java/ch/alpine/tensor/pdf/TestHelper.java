// code by jph
package ch.alpine.tensor.pdf;

import java.util.Random;

import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Scalars;
import ch.alpine.tensor.red.Mean;
import ch.alpine.tensor.sca.Sign;
import junit.framework.Assert;

public enum TestHelper {
  ;
  public static void markov(Distribution distribution) {
    Random random = new Random();
    Scalar xbar = Mean.of(distribution);
    Scalar a = Sign.isPositive(xbar) //
        ? RealScalar.of(2 * random.nextDouble()).multiply(xbar)
        : RealScalar.of(2 * random.nextDouble());
    Scalar prob = RealScalar.ONE.subtract(CDF.of(distribution).p_lessThan(a));
    Assert.assertTrue(Scalars.lessEquals(prob, xbar.divide(a)));
  }
}
