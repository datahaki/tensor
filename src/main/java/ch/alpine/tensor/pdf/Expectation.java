// code by jph
package ch.alpine.tensor.pdf;

import java.math.BigInteger;
import java.util.Objects;
import java.util.function.Function;

import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.api.ScalarUnaryOperator;
import ch.alpine.tensor.pdf.d.GeometricDistribution;
import ch.alpine.tensor.sca.AbsSquared;

/** Careful:
 * {@link Expectation} does not work well for distributions that have
 * infinite support and at the same time exact probabilities,
 * for example: {@link GeometricDistribution}
 * 
 * <p>inspired by
 * <a href="https://reference.wolfram.com/language/ref/Expectation.html">Expectation</a> */
public enum Expectation {
  ;
  /** @param function
   * @param distribution
   * @return */
  public static <T extends Tensor> T of(Function<Scalar, T> function, Distribution distribution) {
    if (distribution instanceof DiscreteDistribution discreteDistribution)
      return _of(function, discreteDistribution);
    Objects.requireNonNull(distribution);
    throw new IllegalArgumentException(distribution.toString());
  }

  /** @param distribution
   * @return mean of distribution, E[X] */
  public static Scalar mean(Distribution distribution) {
    return distribution instanceof MeanInterface meanInterface //
        ? meanInterface.mean()
        : of(Function.identity(), distribution);
  }

  /** @param distribution
   * @return variance of distribution, E[ |X-E[X]|^2 ] */
  public static Scalar variance(Distribution distribution) {
    if (distribution instanceof VarianceInterface varianceInterface)
      return varianceInterface.variance();
    Scalar mean = mean(distribution);
    ScalarUnaryOperator scalarUnaryOperator = scalar -> AbsSquared.between(scalar, mean);
    return of(scalarUnaryOperator, distribution);
  }

  @SuppressWarnings("unchecked")
  private static <T extends Tensor> T _of(Function<Scalar, T> function, DiscreteDistribution discreteDistribution) {
    T value = null;
    Scalar p_equals = RealScalar.ZERO;
    Scalar cumprob = RealScalar.ZERO;
    BigInteger sample = discreteDistribution.lowerBound();
    while (!StaticHelper.isFinished(p_equals, cumprob)) {
      Scalar x = RealScalar.of(sample);
      p_equals = discreteDistribution.p_equals(sample);
      cumprob = cumprob.add(p_equals);
      T delta = (T) function.apply(x).multiply(p_equals);
      value = Objects.isNull(value) //
          ? delta
          : (T) value.add(delta);
      sample = sample.add(BigInteger.ONE);
    }
    return value;
  }
}
