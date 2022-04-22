// concept by njw
// adapted by jph
package ch.alpine.tensor.jet;

import java.io.Serializable;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;

import ch.alpine.tensor.RationalScalar;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Scalars;
import ch.alpine.tensor.TensorRuntimeException;
import ch.alpine.tensor.api.AbsInterface;
import ch.alpine.tensor.api.MultiplexScalar;
import ch.alpine.tensor.nrm.Hypot;
import ch.alpine.tensor.pdf.Distribution;
import ch.alpine.tensor.pdf.MeanInterface;
import ch.alpine.tensor.pdf.c.NormalDistribution;
import ch.alpine.tensor.sca.Abs;
import ch.alpine.tensor.sca.AbsSquared;
import ch.alpine.tensor.sca.Sign;
import ch.alpine.tensor.sca.exp.Exp;
import ch.alpine.tensor.sca.exp.ExpInterface;
import ch.alpine.tensor.sca.exp.Log;
import ch.alpine.tensor.sca.exp.LogInterface;
import ch.alpine.tensor.sca.pow.Power;
import ch.alpine.tensor.sca.pow.PowerInterface;
import ch.alpine.tensor.sca.pow.Sqrt;
import ch.alpine.tensor.sca.pow.SqrtInterface;

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
public class Around extends MultiplexScalar implements //
    AbsInterface, ExpInterface, LogInterface, MeanInterface, //
    PowerInterface, SqrtInterface, Serializable {
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
    return scalar instanceof Around around //
        ? of(mean.multiply(around.mean), Hypot.of(mean.multiply(around.sigma), around.mean.multiply(sigma)))
        : of(mean.multiply(scalar), sigma.multiply(Abs.FUNCTION.apply(scalar)));
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
  protected Scalar plus(Scalar scalar) {
    return scalar instanceof Around around //
        ? of(mean.add(around.mean), Hypot.of(sigma, around.sigma))
        : of(mean.add(scalar), sigma);
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

  @Override // from PowerInterface
  public Scalar power(Scalar exponent) {
    if (exponent instanceof Around)
      throw TensorRuntimeException.of(this, exponent);
    Scalar scalar = Power.of(mean, exponent);
    return of(scalar, Abs.FUNCTION.apply(scalar.divide(mean).multiply(sigma).multiply(exponent)));
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
   * 
   * @return sigma */
  public Scalar uncertainty() {
    return sigma;
  }

  public Distribution distribution() {
    return NormalDistribution.of(mean, sigma);
  }

  @Override // from MultiplexScalar
  public Scalar eachMap(UnaryOperator<Scalar> unaryOperator) {
    return of( //
        unaryOperator.apply(mean), //
        unaryOperator.apply(sigma));
  }

  @Override // from MultiplexScalar
  public boolean allMatch(Predicate<Scalar> predicate) {
    return predicate.test(mean) //
        && predicate.test(sigma);
  }

  // ---
  @Override // from Object
  public int hashCode() {
    return mean.hashCode() + 31 * sigma.hashCode();
  }

  @Override // from Object
  public boolean equals(Object object) {
    return object instanceof Around around //
        && mean.equals(around.mean) //
        && sigma.equals(around.sigma);
  }

  @Override // from Object
  public String toString() {
    return mean + SEPARATOR + sigma;
  }
}
