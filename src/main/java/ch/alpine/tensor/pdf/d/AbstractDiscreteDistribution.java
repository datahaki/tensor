// code by jph
package ch.alpine.tensor.pdf.d;

import java.util.OptionalInt;
import java.util.Random;

import ch.alpine.tensor.DoubleScalar;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Scalars;
import ch.alpine.tensor.pdf.DiscreteDistribution;
import ch.alpine.tensor.pdf.InverseCDF;
import ch.alpine.tensor.pdf.MeanInterface;

/** functionality and suggested base class for a discrete probability distribution */
public abstract class AbstractDiscreteDistribution implements DiscreteDistribution, //
    InverseCDF, MeanInterface {
  @Override // from RandomVariateInterface
  public final Scalar randomVariate(Random random) {
    return protected_quantile(DoubleScalar.of(random.nextDouble()));
  }

  @Override // from PDF
  public final Scalar at(Scalar x) {
    OptionalInt optionalInt = Scalars.optionalInt(x);
    return optionalInt.isPresent() //
        ? p_equals(optionalInt.getAsInt())
        : RealScalar.ZERO;
  }

  @Override // from DiscreteDistribution
  public final Scalar p_equals(int n) {
    return lowerBound() <= n //
        ? protected_p_equals(n)
        : RealScalar.ZERO;
  }

  /** @param p in the semi-open interval [0, 1)
   * @return */
  protected abstract Scalar protected_quantile(Scalar p);

  /** @param n with lowerBound() <= n
   * @return P(X == n), i.e. probability of random variable X == n */
  protected abstract Scalar protected_p_equals(int n);
}
