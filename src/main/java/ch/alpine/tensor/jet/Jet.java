// code by jph
package ch.alpine.tensor.jet;

import java.io.Serializable;
import java.util.OptionalInt;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;

import ch.alpine.tensor.MultiplexScalar;
import ch.alpine.tensor.Rational;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Scalars;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.Throw;
import ch.alpine.tensor.alg.Array;
import ch.alpine.tensor.alg.Drop;
import ch.alpine.tensor.alg.Join;
import ch.alpine.tensor.alg.TensorComparator;
import ch.alpine.tensor.alg.UnitVector;
import ch.alpine.tensor.alg.VectorQ;
import ch.alpine.tensor.api.AbsInterface;
import ch.alpine.tensor.api.ScalarUnaryOperator;
import ch.alpine.tensor.api.SignInterface;
import ch.alpine.tensor.ext.Cache;
import ch.alpine.tensor.num.Binomial;
import ch.alpine.tensor.pdf.CDF;
import ch.alpine.tensor.pdf.PDF;
import ch.alpine.tensor.pdf.UnivariateDistribution;
import ch.alpine.tensor.sca.Abs;
import ch.alpine.tensor.sca.AbsSquared;
import ch.alpine.tensor.sca.Sign;
import ch.alpine.tensor.sca.exp.Exp;
import ch.alpine.tensor.sca.exp.ExpInterface;
import ch.alpine.tensor.sca.exp.Log;
import ch.alpine.tensor.sca.exp.LogInterface;
import ch.alpine.tensor.sca.pow.Power;
import ch.alpine.tensor.sca.pow.PowerInterface;
import ch.alpine.tensor.sca.pow.SqrtInterface;
import ch.alpine.tensor.sca.tri.ArcTan;
import ch.alpine.tensor.sca.tri.ArcTanInterface;
import ch.alpine.tensor.sca.tri.Cos;
import ch.alpine.tensor.sca.tri.Cosh;
import ch.alpine.tensor.sca.tri.Sin;
import ch.alpine.tensor.sca.tri.Sinh;
import ch.alpine.tensor.sca.tri.TrigonometryInterface;

/** automatic differentiation
 * 
 * Applications:
 * JetScalar is used
 * 
 * to test the consistency between the {@link CDF} and {@link PDF}
 * of {@link UnivariateDistribution}s.
 * 
 * to validate the implementation of Hermite subdivision
 * 
 * to validate the implementation of forward and backward pass
 * of network layers
 * 
 * @implSpec
 * This class is immutable and thread-safe. */
public class Jet extends MultiplexScalar implements //
    AbsInterface, ArcTanInterface, ExpInterface, LogInterface, PowerInterface, //
    SignInterface, SqrtInterface, TrigonometryInterface, //
    Comparable<Scalar>, Serializable {
  /** @param jet {f[x], f'[x], f''[x], ...}
   * @return */
  public static Jet of(Tensor jet) {
    if (jet.stream().anyMatch(Jet.class::isInstance))
      throw new Throw(jet);
    return new Jet(VectorQ.require(jet).copy());
  }

  /** constructor for variables
   * 
   * Remark: do not use constructor for constants
   * which would have the form J{x, 0, 0, 0, ...}
   * 
   * @param scalar x
   * @param n strictly positive
   * @return vector {f[x], f'[x], f''[x], ...} where f = identity therefore
   * J{x, 1, 0, 0, ...} */
  public static Jet identity(Scalar scalar, int n) {
    if (scalar instanceof Jet)
      throw new Throw(scalar);
    if (n == 1)
      return new Jet(Tensors.of(scalar));
    Tensor vector = UnitVector.of(n, 1);
    vector.set(scalar, 0);
    return new Jet(vector);
  }

  private static final Jet EMPTY = new Jet(Tensors.empty());
  private static final Cache<Integer, Jet> CACHE_ONE = Cache.of(Jet::build_one, 16);

  private static Jet build_one(int n) {
    return new Jet(UnitVector.of(n, 0));
  }

  private static Jet chain(Tensor vector, ScalarUnaryOperator f, ScalarUnaryOperator df) {
    if (Tensors.isEmpty(vector))
      return EMPTY;
    return new Jet(Join.of( //
        Tensors.of(f.apply(vector.Get(0))), //
        product(((Jet) df.apply(new Jet(opF(vector)))).derivatives(), opD(vector))));
  }

  /** drop function, promote derivatives, and decrease order by 1
   * 
   * @param vector
   * @return */
  private static Tensor opD(Tensor vector) {
    return Drop.head(vector, 1);
  }

  /** keep function and derivatives, and decrease order by 1
   * 
   * @param vector
   * @return */
  private static Tensor opF(Tensor vector) {
    return Drop.tail(vector, 1);
  }

  /** implementation by gemini
   * 
   * @param f
   * @param g
   * @return */
  private static Tensor product(Tensor f, Tensor g) {
    int n = Math.min(f.length(), g.length());
    if (n == 0)
      return Tensors.empty();
    Tensor result = Tensors.reserve(n);
    for (int k = 0; k < n; ++k) {
      Scalar sum = f.Get(0).multiply(g.Get(k));
      for (int j = 1; j <= k; ++j) {
        Scalar term = f.Get(j).multiply(g.Get(k - j));
        sum = sum.add(term.multiply(Binomial.of(k, j)));
      }
      result.append(sum);
    }
    return result;
  }

  // ---
  private final Tensor derivatives;

  Jet(Tensor vector) {
    this.derivatives = vector;
  }

  public Tensor derivatives() {
    return derivatives.unmodifiable();
  }

  public Scalar derivative(int order) {
    return derivatives.Get(order);
  }

  @Override // from Scalar
  public Jet multiply(Scalar scalar) {
    return scalar instanceof Jet jetScalar //
        ? new Jet(product(derivatives, jetScalar.derivatives))
        : new Jet(derivatives.multiply(scalar));
  }

  @Override // from Scalar
  public Jet negate() {
    return new Jet(derivatives.negate());
  }

  @Override // from Scalar
  public Jet reciprocal() {
    return chain(derivatives, Scalar::reciprocal, s -> s.multiply(s).negate().reciprocal());
  }

  @Override // from Scalar
  public Scalar zero() {
    return new Jet(derivatives.maps(Scalar::zero));
  }

  @Override // from Scalar
  public Jet one() {
    return CACHE_ONE.apply(derivatives.length());
  }

  @Override // from AbstractScalar
  protected Scalar plus(Scalar scalar) {
    if (scalar instanceof Jet jetScalar)
      return new Jet(derivatives.add(jetScalar.derivatives));
    Tensor result = derivatives.copy();
    result.set(scalar::add, 0); // this + constant scalar
    return new Jet(result);
  }

  @Override // from AbsInterface
  public Scalar abs() {
    return chain(derivatives, Abs.FUNCTION, Sign.FUNCTION);
  }

  @Override // from AbsInterface
  public Scalar absSquared() {
    return chain(derivatives, AbsSquared.FUNCTION, AbsSquared.DERIVATIVE);
  }

  @Override // from ArcTanInterface
  public Scalar arcTan(Scalar x) {
    Jet quotient = (Jet) divide(x);
    return chain( //
        quotient.derivatives(), //
        ArcTan.FUNCTION, //
        s -> RealScalar.ONE.add(s.multiply(s)).reciprocal());
  }

  @Override // from ExpInterface
  public Scalar exp() {
    return chain(derivatives, Exp.FUNCTION, Exp.FUNCTION);
  }

  @Override // from LogInterface
  public Scalar log() {
    return chain(derivatives, Log.FUNCTION, Scalar::reciprocal);
  }

  @Override // from PowerInterface
  public Scalar power(Scalar exponent) {
    OptionalInt optionalInt = Scalars.optionalInt(exponent);
    if (optionalInt.isPresent())
      return Scalars.mul().raise(this, optionalInt.orElseThrow());
    ScalarUnaryOperator dPower = Power.withExponent(exponent.subtract(RealScalar.ONE));
    ScalarUnaryOperator df = scalar -> dPower.apply(scalar).multiply(exponent);
    return chain(derivatives, Power.withExponent(exponent), df);
  }

  @Override // from SignInterface
  public Scalar sign() {
    return chain(derivatives, Sign.FUNCTION, Scalar::zero);
  }

  @Override // from SqrtInterface
  public Scalar sqrt() {
    return power(Rational.HALF);
  }

  @Override // from TrigonometryInterface
  public Scalar cos() {
    return chain(derivatives, Cos.FUNCTION, scalar -> Sin.FUNCTION.apply(scalar).negate());
  }

  @Override // from TrigonometryInterface
  public Scalar cosh() {
    return chain(derivatives, Cosh.FUNCTION, Sinh.FUNCTION);
  }

  @Override // from TrigonometryInterface
  public Scalar sin() {
    return chain(derivatives, Sin.FUNCTION, Cos.FUNCTION);
  }

  @Override // from TrigonometryInterface
  public Scalar sinh() {
    return chain(derivatives, Sinh.FUNCTION, Cosh.FUNCTION);
  }

  @Override // from MultiplexScalar
  public Scalar eachMap(UnaryOperator<Scalar> unaryOperator) {
    return new Jet(derivatives.maps(unaryOperator));
  }

  @Override // from MultiplexScalar
  public boolean allMatch(Predicate<Scalar> predicate) {
    return derivatives.stream().map(Scalar.class::cast).allMatch(predicate);
  }

  @Override // from Comparable
  public int compareTo(Scalar scalar) {
    return scalar instanceof Jet jetScalar //
        ? TensorComparator.INSTANCE.compare(derivatives, jetScalar.derivatives)
        : TensorComparator.INSTANCE.compare(derivatives, Join.of(Tensors.of(scalar), Array.zeros(derivatives.length() - 1)));
  }

  // ---
  @Override
  public int hashCode() {
    return derivatives.hashCode();
  }

  @Override
  public boolean equals(Object object) {
    return object instanceof Jet jet //
        && derivatives.equals(jet.derivatives);
  }

  @Override
  public String toString() {
    return "Jet" + derivatives.toString();
  }
}
