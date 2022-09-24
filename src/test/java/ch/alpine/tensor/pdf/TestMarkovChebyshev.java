// code by jph
package ch.alpine.tensor.pdf;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Random;
import java.util.stream.IntStream;

import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Scalars;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.alg.OrderedQ;
import ch.alpine.tensor.alg.Reverse;
import ch.alpine.tensor.red.Mean;
import ch.alpine.tensor.red.Variance;
import ch.alpine.tensor.sca.N;
import ch.alpine.tensor.sca.Sign;
import ch.alpine.tensor.sca.pow.Power;

/** Reference:
 * "Linear Algebra and Learning from data" by Gilbert Strang, 2019 */
public enum TestMarkovChebyshev {
  ;
  public static void markov(Distribution distribution) {
    Random random = new Random();
    Scalar xbar = Mean.of(distribution);
    Sign.requirePositiveOrZero(xbar);
    Scalar a = Sign.isPositive(xbar) //
        ? RealScalar.of(2 * random.nextDouble()).multiply(xbar)
        : RealScalar.of(2 * random.nextDouble());
    Scalar prob = RealScalar.ONE.subtract(CDF.of(distribution).p_lessThan(a));
    assertTrue(Scalars.lessEquals(prob, xbar.divide(a)));
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
    assertTrue(Scalars.lessEquals(lo.add(hi), sigmaSq.divide(a).divide(a)));
  }

  private static final Tensor LARGE = Tensor.of(IntStream.range(7, 200) //
      .mapToObj(RealScalar::of) //
      .map(Power.function(3)));
  private static final Tensor NUMER = LARGE.map(N.DOUBLE);

  public static void monotonous(Distribution distribution) {
    // System.out.println(LARGE.map(PDF.of(distribution)::at).extract(0, 10));
    OrderedQ.require(Reverse.of(LARGE.map(PDF.of(distribution)::at)));
    if (distribution instanceof CDF)
      OrderedQ.require(LARGE.map(CDF.of(distribution)::p_lessEquals));
    OrderedQ.require(Reverse.of(NUMER.map(PDF.of(distribution)::at)));
    if (distribution instanceof CDF)
      OrderedQ.require(NUMER.map(CDF.of(distribution)::p_lessEquals));
  }
}
