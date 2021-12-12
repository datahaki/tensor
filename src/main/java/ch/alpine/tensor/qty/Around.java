// concept by njw
// adapted by jph
package ch.alpine.tensor.qty;

import java.io.Serializable;
import java.math.MathContext;

import ch.alpine.tensor.AbstractScalar;
import ch.alpine.tensor.ExactScalarQ;
import ch.alpine.tensor.RationalScalar;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Scalars;
import ch.alpine.tensor.TensorRuntimeException;
import ch.alpine.tensor.api.AbsInterface;
import ch.alpine.tensor.api.ExactScalarQInterface;
import ch.alpine.tensor.api.ExpInterface;
import ch.alpine.tensor.api.LogInterface;
import ch.alpine.tensor.api.NInterface;
import ch.alpine.tensor.api.SqrtInterface;
import ch.alpine.tensor.nrm.Hypot;
import ch.alpine.tensor.pdf.Distribution;
import ch.alpine.tensor.pdf.MeanInterface;
import ch.alpine.tensor.pdf.NormalDistribution;
import ch.alpine.tensor.sca.Abs;
import ch.alpine.tensor.sca.AbsSquared;
import ch.alpine.tensor.sca.Exp;
import ch.alpine.tensor.sca.Log;
import ch.alpine.tensor.sca.N;
import ch.alpine.tensor.sca.Sign;
import ch.alpine.tensor.sca.Sqrt;

/** "Around[mean, sigma] represents an approximate number or quantity with a value around
 * mean and an uncertainty sigma."
 * 
 * The implementation of Around attempts to be consistent with Mathematica::Around.
 * 
 * However, Mathematica uses a first order approximation of the function that is applied
 * to Around in order to map mean and sigma. This results in seemingly inconsistent choices:
 * Example: Let a = Around[3, 4], then a a != a ^ 2.
 * 
 * Remark:
 * Around[0, 1] Around[0, 1] == 0
 * 
 * <p>inspired by
 * <a href="https://reference.wolfram.com/language/ref/Around.html">Around</a>
 * 
 * @implSpec
 * This class is immutable and thread-safe. */
public class Around extends AbstractScalar implements //
    AbsInterface, ExactScalarQInterface, ExpInterface, LogInterface, MeanInterface, //
    NInterface, SqrtInterface, Serializable {
  private static final String SEPARATOR = "\u00B1";

  /** Mathematica allows
   * Around[Quantity[1, "m"], 0] == Quantity[1, "m"]
   * but in this special case, the tensor library throws an Exception
   * 
   * @param mean
   * @param sigma non-negative
   * @return
   * @throws Exception if mean and sigma are quantities of different units */
  public static Scalar of(Scalar mean, Scalar sigma) {
    mean.add(sigma);
    if (Scalars.isZero(sigma))
      return mean;
    return new Around(mean, Sign.requirePositiveOrZero(sigma));
  }

  /** @param mean
   * @param sigma non-negative
   * @return
   * @throws Exception if mean and sigma are quantities of different units */
  public static Scalar of(Number mean, Number sigma) {
    return of(RealScalar.of(mean), RealScalar.of(sigma));
  }

  // ---
  private final Scalar mean;
  private final Scalar sigma;

  private Around(Scalar mean, Scalar sigma) {
    this.mean = mean;
    this.sigma = sigma;
  }

  @Override // from Scalar
  public Scalar multiply(Scalar scalar) {
    if (scalar instanceof Around) {
      Around around = (Around) scalar;
      return of(mean.multiply(around.mean), Hypot.of(mean.multiply(around.sigma), around.mean.multiply(sigma)));
    }
    return of(mean.multiply(scalar), sigma.multiply(Abs.FUNCTION.apply(scalar)));
  }

  @Override // from Scalar
  public Scalar negate() {
    return of(mean.negate(), sigma);
  }

  @Override // from Scalar
  public Scalar reciprocal() {
    return of(mean.reciprocal(), sigma.divide(mean).divide(mean));
  }

  @Override // from Scalar
  public Scalar zero() {
    return mean.zero();
  }

  @Override // from Scalar
  public Scalar one() {
    return mean.zero().one();
  }

  @Override // from Scalar
  public Number number() {
    throw TensorRuntimeException.of(this);
  }

  @Override // from Scalar
  protected Scalar plus(Scalar scalar) {
    if (scalar instanceof Around) {
      Around around = (Around) scalar;
      return of(mean.add(around.mean), Hypot.of(sigma, around.sigma));
    }
    return of(mean.add(scalar), sigma);
  }

  // ---
  @Override // from AbsInterface
  public Scalar abs() {
    return of(Abs.FUNCTION.apply(mean), sigma);
  }

  @Override // from AbsInterface
  public Scalar absSquared() {
    return of(AbsSquared.FUNCTION.apply(mean), //
        Sqrt.FUNCTION.apply(RealScalar.TWO).multiply(Abs.FUNCTION.apply(mean)).multiply(sigma));
  }

  @Override // from ExpInterface
  public Scalar exp() {
    Scalar exp = Exp.FUNCTION.apply(mean);
    return of(exp, exp.multiply(sigma));
  }

  @Override // from LogInterface
  public Scalar log() {
    return of(Log.FUNCTION.apply(mean), sigma.divide(mean));
  }

  @Override // from ExactScalarQInterface
  public boolean isExactScalar() {
    return ExactScalarQ.of(mean) //
        && ExactScalarQ.of(sigma);
  }

  @Override // from NInterface
  public Scalar n() {
    return of(N.DOUBLE.apply(mean), N.DOUBLE.apply(sigma));
  }

  @Override // from NInterface
  public Scalar n(MathContext mathContext) {
    N n = N.in(mathContext.getPrecision());
    return of(n.apply(mean), n.apply(sigma));
  }

  @Override // from SqrtInterface
  public Scalar sqrt() {
    Scalar sqrt = Sqrt.FUNCTION.apply(mean);
    return of(sqrt, RationalScalar.HALF.multiply(sigma).divide(sqrt));
  }

  @Override // from MeanInterface
  public Scalar mean() {
    return mean;
  }

  /** Around[mean, sigma]["Uncertainty"] == sigma
   * @return sigma */
  public Scalar uncertainty() {
    return sigma;
  }

  public Distribution distribution() {
    return NormalDistribution.of(mean, sigma);
  }

  // ---
  @Override
  public int hashCode() {
    return mean.hashCode() + 31 * sigma.hashCode();
  }

  @Override
  public boolean equals(Object object) {
    if (object instanceof Around) {
      Around around = (Around) object;
      return mean.equals(around.mean) //
          && sigma.equals(around.sigma);
    }
    return false;
  }

  @Override
  public String toString() {
    return mean + SEPARATOR + sigma;
  }
}