// code by jph
package ch.ethz.idsc.tensor.pdf;

import java.io.Serializable;

import ch.ethz.idsc.tensor.DoubleScalar;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Scalars;
import ch.ethz.idsc.tensor.TensorRuntimeException;
import ch.ethz.idsc.tensor.qty.Quantity;
import ch.ethz.idsc.tensor.sca.Clips;
import ch.ethz.idsc.tensor.sca.Exp;
import ch.ethz.idsc.tensor.sca.Log;
import ch.ethz.idsc.tensor.sca.Sign;

/** inspired by
 * <a href="https://reference.wolfram.com/language/ref/GompertzMakehamDistribution.html">GompertzMakehamDistribution</a> */
public class GompertzMakehamDistribution extends AbstractContinuousDistribution implements //
    InverseCDF, Serializable {
  /** @param lambda positive scale parameter, may be instance of {@link Quantity}
   * @param xi positive frailty parameter
   * @return */
  public static Distribution of(Scalar lambda, Scalar xi) {
    if (Scalars.lessEquals(xi, RealScalar.ZERO))
      throw TensorRuntimeException.of(xi);
    return new GompertzMakehamDistribution(Sign.requirePositive(lambda), xi);
  }

  /** @param lambda positive scale parameter
   * @param xi positive frailty parameter
   * @return */
  public static Distribution of(Number lambda, Number xi) {
    return of(RealScalar.of(lambda), RealScalar.of(xi));
  }

  /***************************************************/
  private final Scalar lambda;
  private final Scalar xi;
  private final Scalar lambda_xi;

  private GompertzMakehamDistribution(Scalar lambda, Scalar xi) {
    this.lambda = lambda;
    this.xi = xi;
    lambda_xi = lambda.multiply(xi);
    if (Scalars.isZero(lambda_xi))
      throw TensorRuntimeException.of(lambda, xi);
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

  @Override // from InverseCDF
  public Scalar quantile(Scalar p) {
    return _quantile(Clips.unit().requireInside(p));
  }

  private Scalar _quantile(Scalar p) {
    return Log.FUNCTION.apply(RealScalar.ONE.subtract( //
        Log.FUNCTION.apply(RealScalar.ONE.subtract(p)).divide(xi))).divide(lambda);
  }

  @Override // from AbstractContinuousDistribution
  protected Scalar randomVariate(double reference) {
    return _quantile(DoubleScalar.of(reference));
  }

  @Override // from Object
  public String toString() {
    return String.format("%s[%s, %s]", getClass().getSimpleName(), lambda, xi);
  }
}
