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
import ch.alpine.tensor.alg.Subdivide;
import ch.alpine.tensor.chq.FiniteScalarQ;
import ch.alpine.tensor.mat.Tolerance;
import ch.alpine.tensor.red.Mean;
import ch.alpine.tensor.red.StandardDeviation;
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

  public static void symmetricAroundMean(Distribution distribution) {
    if (distribution instanceof MeanInterface) {
      final Scalar mean = Mean.of(distribution);
      if (FiniteScalarQ.of(mean)) {
        Scalar sigma = StandardDeviation.of(distribution);
        if (FiniteScalarQ.of(sigma)) {
          Scalar delta = sigma.add(sigma).add(sigma);
          {
            Tensor x = Subdivide.of(mean.subtract(delta), mean.add(delta), 13);
            {
              PDF pdf = PDF.of(distribution);
              Tensor ref = x.map(pdf::at);
              Tolerance.CHOP.requireClose(ref, Reverse.of(ref));
            }
            if (distribution instanceof CDF) {
              CDF cdf = CDF.of(distribution);
              Tensor ref = x.map(cdf::p_lessThan);
              Tolerance.CHOP.requireClose(ref, Reverse.of(ref.map(r -> RealScalar.ONE.subtract(r))));
            }
          }
          if (distribution instanceof InverseCDF) {
            Scalar two = mean.one().add(mean.one());
            InverseCDF inverseCDF = InverseCDF.of(distribution);
            for (Tensor _p : Subdivide.of(0, 0.5, 13)) {
              Scalar p = (Scalar) _p;
              Scalar q_lo = inverseCDF.quantile(p);
              if (FiniteScalarQ.of(q_lo)) {
                Scalar q_hi = inverseCDF.quantile(RealScalar.ONE.subtract(p));
                Tolerance.CHOP.requireClose(q_lo.add(q_hi).divide(two), mean);
              }
            }
          }
        }
      }
    }
  }
}
