// code by jph
package ch.alpine.tensor.pdf.c;

import ch.alpine.tensor.DoubleScalar;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Scalars;
import ch.alpine.tensor.Throw;
import ch.alpine.tensor.io.MathematicaFormat;
import ch.alpine.tensor.itp.FindRoot;
import ch.alpine.tensor.pdf.Distribution;
import ch.alpine.tensor.qty.Quantity;
import ch.alpine.tensor.red.Times;
import ch.alpine.tensor.sca.Sign;
import ch.alpine.tensor.sca.exp.Exp;

/** inspired by
 * <a href="https://reference.wolfram.com/language/ref/ShiftedGompertzDistribution.html">ShiftedGompertzDistribution</a> */
public class ShiftedGompertzDistribution extends AbstractContinuousDistribution {
  /** @param lambda positive scale parameter, may be instance of {@link Quantity}
   * @param xi positive frailty parameter real scalar
   * @return */
  public static Distribution of(Scalar lambda, Scalar xi) {
    if (Scalars.lessThan(RealScalar.ZERO, xi))
      return new ShiftedGompertzDistribution(Sign.requirePositive(lambda), xi);
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
  private final Scalar dl;

  private ShiftedGompertzDistribution(Scalar lambda, Scalar xi) {
    this.lambda = lambda;
    this.xi = xi;
    dl = lambda.reciprocal();
  }

  @Override // from PDF
  public Scalar at(Scalar x) {
    if (Sign.isPositiveOrZero(x)) {
      Scalar xln = x.multiply(lambda).negate();
      Scalar exp = Exp.FUNCTION.apply(xln);
      return Times.of( //
          Exp.FUNCTION.apply(xln.subtract(exp.multiply(xi))), //
          lambda, //
          RealScalar.ONE.add(RealScalar.ONE.subtract(exp).multiply(xi)));
    }
    return RealScalar.ZERO;
  }

  @Override // from CDF
  public Scalar p_lessThan(Scalar x) {
    if (Sign.isPositiveOrZero(x)) {
      Scalar xln = x.multiply(lambda).negate();
      Scalar exp = Exp.FUNCTION.apply(xln);
      return Exp.FUNCTION.apply(exp.multiply(xi).negate()).multiply(RealScalar.ONE.subtract(exp));
    }
    return RealScalar.ZERO;
  }

  @Override // from AbstractContinuousDistribution
  protected Scalar protected_quantile(Scalar p) {
    if (p.equals(RealScalar.ONE))
      return DoubleScalar.POSITIVE_INFINITY;
    return FindRoot.of(x -> p_lessThan(x).subtract(p)).above(dl.zero(), dl);
  }

  @Override
  public Scalar mean() {
    throw new UnsupportedOperationException(); // requires Gamma[0, Xi]
  }

  @Override
  public Scalar variance() {
    throw new UnsupportedOperationException(); // requires Gamma[0, Xi] and HypergeometricPFQ
  }

  @Override
  public String toString() {
    return MathematicaFormat.concise("ShiftedGompertzDistribution", lambda, xi);
  }
}
