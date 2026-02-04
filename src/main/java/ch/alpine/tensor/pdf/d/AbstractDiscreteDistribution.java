// code by jph
package ch.alpine.tensor.pdf.d;

import java.math.BigInteger;
import java.util.random.RandomGenerator;

import ch.alpine.tensor.DoubleScalar;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Scalars;
import ch.alpine.tensor.Throw;
import ch.alpine.tensor.pdf.DiscreteDistribution;

/** functionality and suggested base class for a discrete probability distribution */
public abstract class AbstractDiscreteDistribution implements DiscreteDistribution {
  @Override // from Distribution
  public Scalar randomVariate(RandomGenerator randomGenerator) {
    return protected_quantile(DoubleScalar.of(randomGenerator.nextDouble()));
  }

  @Override // from PDF
  public final Scalar at(Scalar x) {
    if (x instanceof RealScalar)
      return Scalars.optionalBigInteger(x) //
          .map(this::p_equals) //
          .orElse(RealScalar.ZERO);
    throw new Throw(x);
  }

  @Override // from DiscreteDistribution
  public final Scalar p_equals(BigInteger n) {
    BigInteger lo = Scalars.bigIntegerValueExact(support().min());
    return lo.compareTo(n) <= 0 //
        ? protected_p_equals(n)
        : RealScalar.ZERO;
  }

  /** @param p in the semi-open interval [0, 1)
   * @return */
  protected abstract Scalar protected_quantile(Scalar p);

  /** @param x with lowerBound() <= x
   * @return P(X == x), i.e. probability of random variable X == x */
  protected abstract Scalar protected_p_equals(BigInteger x);
}
