// code by jph
package ch.alpine.tensor.pdf.d;

import java.math.BigInteger;

import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Scalars;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.Throw;
import ch.alpine.tensor.alg.Last;
import ch.alpine.tensor.io.MathematicaFormat;
import ch.alpine.tensor.pdf.Distribution;
import ch.alpine.tensor.sca.exp.Exp;

/** in Mathematica, the CDF of the Poisson-distribution is expressed as
 * 
 * CDF[PoissonDistribution[lambda], x] == GammaRegularized[1 + Floor[x], lambda]
 * 
 * inspired by
 * <a href="https://reference.wolfram.com/language/ref/PoissonDistribution.html">PoissonDistribution</a> */
public class PoissonDistribution extends EvaluatedDiscreteDistribution {
  /** probabilities are zero beyond P_EQUALS_MAX */
  private static final int P_EQUALS_MAX = 1950;
  /** lambda above max leads to incorrect results due to numerics */
  private static final Scalar LAMBDA_MAX = RealScalar.of(700);

  /** Example:
   * PDF[PoissonDistribution[lambda], n] == 1/(n!) Exp[-lambda] lambda^n
   * 
   * Because P[X==0] == Exp[-lambda], the implementation limits lambda to 700.
   * 
   * @param lambda strictly positive and <= 700
   * @return */
  public static Distribution of(Scalar lambda) {
    if (Scalars.lessEquals(lambda, RealScalar.ZERO))
      throw new Throw(lambda);
    if (Scalars.lessThan(LAMBDA_MAX, lambda))
      throw new Throw(lambda);
    return new PoissonDistribution(lambda);
  }

  /** @param lambda strictly positive and <= 700
   * @return */
  public static Distribution of(Number lambda) {
    return of(RealScalar.of(lambda));
  }

  // ---
  private final Scalar lambda;
  private final Tensor values = Tensors.reserve(32);

  private PoissonDistribution(Scalar lambda) {
    this.lambda = lambda;
    values.append(Exp.FUNCTION.apply(lambda.negate()));
    build(P_EQUALS_MAX);
  }

  @Override // from MeanInterface
  public Scalar mean() {
    return lambda;
  }

  @Override // from VarianceInterface
  public Scalar variance() {
    return lambda;
  }

  @Override // from DiscreteDistribution
  public BigInteger lowerBound() {
    return BigInteger.ZERO;
  }

  @Override // from AbstractDiscreteDistribution
  protected Scalar protected_p_equals(BigInteger __x) {
    int x = __x.intValueExact();
    if (P_EQUALS_MAX < x)
      return RealScalar.ZERO;
    if (values.length() <= x) {
      Scalar _x = Last.of(values);
      while (values.length() <= x) {
        Scalar factor = lambda.divide(RealScalar.of(values.length()));
        values.append(_x = _x.multiply(factor));
      }
    }
    return values.Get(x);
  }

  @Override // from Object
  public String toString() {
    return MathematicaFormat.concise("PoissonDistribution", lambda);
  }
}
