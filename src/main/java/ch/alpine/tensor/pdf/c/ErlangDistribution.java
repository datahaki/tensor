// code by jph
package ch.alpine.tensor.pdf.c;

import java.io.Serializable;

import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.ext.Integers;
import ch.alpine.tensor.pdf.Distribution;
import ch.alpine.tensor.pdf.MeanInterface;
import ch.alpine.tensor.pdf.PDF;
import ch.alpine.tensor.pdf.VarianceInterface;
import ch.alpine.tensor.qty.Quantity;
import ch.alpine.tensor.sca.Sign;
import ch.alpine.tensor.sca.exp.Exp;
import ch.alpine.tensor.sca.gam.Factorial;
import ch.alpine.tensor.sca.pow.Power;

/** ErlangDistribution[k, lambda] == GammaDistribution[k, 1 / lambda]
 * 
 * <p>The CDF of the Erlang-distribution[k, lambda] is the function
 * GammaRegularized[k, 0, x * lambda]
 * which is not yet available in the tensor library.
 * 
 * <p>inspired by
 * <a href="https://reference.wolfram.com/language/ref/ErlangDistribution.html">ErlangDistribution</a> */
public class ErlangDistribution implements Distribution, MeanInterface, PDF, VarianceInterface, Serializable {
  /** @param k positive integer
   * @param lambda, may be instance of {@link Quantity}
   * @return
   * @throws Exception if k is negative or zero */
  public static Distribution of(int k, Scalar lambda) {
    return new ErlangDistribution(Integers.requirePositive(k), lambda);
  }

  // ---
  private final Scalar k;
  private final Scalar lambda;
  private final Scalar factor;

  private ErlangDistribution(int k, Scalar lambda) {
    this.k = RealScalar.of(k);
    this.lambda = lambda;
    factor = Power.of(lambda, k).divide(Factorial.of(k - 1));
  }

  @Override // from PDF
  public Scalar at(Scalar x) {
    if (Sign.isNegativeOrZero(x))
      return lambda.zero();
    return Exp.FUNCTION.apply(x.negate().multiply(lambda)) //
        .multiply(Power.of(x, k.subtract(RealScalar.ONE))).multiply(factor);
  }

  @Override // from MeanInterface
  public Scalar mean() {
    return k.divide(lambda);
  }

  @Override // from VarianceInterface
  public Scalar variance() {
    return k.divide(lambda.multiply(lambda));
  }

  @Override // from Object
  public String toString() {
    return String.format("ErlangDistribution[%s, %s]", k, lambda);
  }
}
