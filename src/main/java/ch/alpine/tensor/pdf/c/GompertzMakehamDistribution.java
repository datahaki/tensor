// code by jph
package ch.alpine.tensor.pdf.c;

import java.io.Serializable;

import ch.alpine.tensor.DoubleScalar;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Scalars;
import ch.alpine.tensor.Throw;
import ch.alpine.tensor.io.MathematicaFormat;
import ch.alpine.tensor.pdf.Distribution;
import ch.alpine.tensor.qty.Quantity;
import ch.alpine.tensor.qty.QuantityUnit;
import ch.alpine.tensor.sca.Clip;
import ch.alpine.tensor.sca.Clips;
import ch.alpine.tensor.sca.Sign;
import ch.alpine.tensor.sca.exp.Exp;
import ch.alpine.tensor.sca.exp.Log;

/** inspired by
 * <a href="https://reference.wolfram.com/language/ref/GompertzMakehamDistribution.html">GompertzMakehamDistribution</a> */
public class GompertzMakehamDistribution extends AbstractContinuousDistribution implements Serializable {
  /** @param lambda positive scale parameter, may be instance of {@link Quantity}
   * @param xi positive frailty parameter real scalar
   * @return */
  public static Distribution of(Scalar lambda, Scalar xi) {
    if (Scalars.lessThan(RealScalar.ZERO, xi))
      return new GompertzMakehamDistribution(Sign.requirePositive(lambda), xi);
    throw new Throw(xi);
  }

  /** @param lambda positive scale parameter
   * @param xi positive frailty parameter
   * @return */
  public static Distribution of(Number lambda, Number xi) {
    return of(RealScalar.of(lambda), RealScalar.of(xi));
  }

  // ---
  private final Scalar lambda;
  private final Scalar xi;
  private final Scalar lambda_xi;

  private GompertzMakehamDistribution(Scalar lambda, Scalar xi) {
    this.lambda = lambda;
    this.xi = xi;
    lambda_xi = lambda.multiply(xi);
    if (Scalars.isZero(lambda_xi))
      throw new Throw(lambda, xi);
  }

  @Override
  public Clip support() {
    return Clips.positive(Quantity.of(DoubleScalar.POSITIVE_INFINITY, QuantityUnit.of(lambda).negate()));
  }

  @Override // from PDF
  public Scalar at(Scalar x) {
    Scalar x_lambda = x.multiply(lambda);
    Scalar exp = Exp.FUNCTION.apply(x_lambda);
    return Sign.isPositiveOrZero(x) //
        ? Exp.FUNCTION.apply(RealScalar.ONE.subtract(exp).multiply(xi).add(x_lambda)).multiply(lambda_xi)
        : lambda.zero();
  }

  @Override // from CDF
  public Scalar p_lessThan(Scalar x) {
    Scalar x_lambda = x.multiply(lambda);
    Scalar exp = Exp.FUNCTION.apply(x_lambda);
    return Sign.isPositive(x) //
        ? RealScalar.ONE.subtract(Exp.FUNCTION.apply(RealScalar.ONE.subtract(exp).multiply(xi)))
        : RealScalar.ZERO;
  }

  @Override // from AbstractContinuousDistribution
  protected Scalar protected_quantile(Scalar p) {
    return Log.FUNCTION.apply(RealScalar.ONE.subtract( //
        Log.FUNCTION.apply(RealScalar.ONE.subtract(p)).divide(xi))).divide(lambda);
  }

  @Override // from MeanInterface
  public Scalar mean() {
    // Mean[GompertzMakehamDistribution[a, b]] == (E^b Gamma[0, b]) / a
    throw new UnsupportedOperationException();
  }

  @Override // from VarianceInterface
  public Scalar variance() {
    throw new UnsupportedOperationException();
  }

  @Override // from Object
  public String toString() {
    return MathematicaFormat.concise("GompertzMakehamDistribution", lambda, xi);
  }
}
