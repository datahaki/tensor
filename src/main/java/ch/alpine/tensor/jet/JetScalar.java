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
import ch.alpine.tensor.alg.Join;
import ch.alpine.tensor.alg.TensorComparator;
import ch.alpine.tensor.alg.UnitVector;
import ch.alpine.tensor.alg.VectorQ;
import ch.alpine.tensor.api.AbsInterface;
import ch.alpine.tensor.api.ScalarUnaryOperator;
import ch.alpine.tensor.api.SignInterface;
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
 * to validate the implementation of network layers
 * 
 * @implSpec
 * This class is immutable and thread-safe. */
public class JetScalar extends MultiplexScalar implements //
    AbsInterface, ArcTanInterface, ExpInterface, LogInterface, PowerInterface, //
    SignInterface, SqrtInterface, TrigonometryInterface, //
    Comparable<Scalar>, Serializable {
  /** @param jet {f[x], f'[x], f''[x], ...}
   * @return */
  public static JetScalar of(Tensor jet) {
    if (jet.stream().anyMatch(JetScalar.class::isInstance))
      throw new Throw(jet);
    return new JetScalar(VectorQ.require(jet).copy());
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
  public static JetScalar identity(Scalar scalar, int n) {
    if (scalar instanceof JetScalar)
      throw new Throw(scalar);
    if (n == 1)
      return new JetScalar(Tensors.of(scalar));
    Tensor vector = UnitVector.of(n, 1);
    vector.set(scalar, 0);
    return new JetScalar(vector);
  }

  // ---
  private final Tensor jet;

  JetScalar(Tensor vector) {
    this.jet = vector;
  }

  public Tensor jet() {
    return jet.unmodifiable();
  }

  @Override // from Scalar
  public JetScalar multiply(Scalar scalar) {
    return scalar instanceof JetScalar jetScalar //
        ? new JetScalar(JetScalars.product(jet, jetScalar.jet))
        : new JetScalar(jet.multiply(scalar));
  }

  @Override // from Scalar
  public JetScalar negate() {
    return new JetScalar(jet.negate());
  }

  @Override // from Scalar
  public JetScalar reciprocal() {
    return JetScalars.chain(jet, Scalar::reciprocal, s -> s.multiply(s).negate().reciprocal());
  }

  @Override // from Scalar
  public Scalar zero() {
    return new JetScalar(jet.maps(Scalar::zero));
  }

  @Override // from Scalar
  public JetScalar one() {
    return JetScalars.CACHE_ONE.apply(jet.length());
  }

  @Override // from AbstractScalar
  protected Scalar plus(Scalar scalar) {
    if (scalar instanceof JetScalar jetScalar)
      return new JetScalar(jet.add(jetScalar.jet));
    Tensor result = jet.copy();
    result.set(scalar::add, 0); // this + constant scalar
    return new JetScalar(result);
  }

  @Override // from AbsInterface
  public Scalar abs() {
    return JetScalars.chain(jet, Abs.FUNCTION, Sign.FUNCTION);
  }

  @Override // from AbsInterface
  public Scalar absSquared() {
    return JetScalars.chain(jet, AbsSquared.FUNCTION, AbsSquared.DERIVATIVE);
  }

  @Override // from ArcTanInterface
  public Scalar arcTan(Scalar x) {
    JetScalar quotient = (JetScalar) divide(x);
    return JetScalars.chain( //
        quotient.jet(), //
        ArcTan.FUNCTION, //
        s -> RealScalar.ONE.add(s.multiply(s)).reciprocal());
  }

  @Override // from ExpInterface
  public Scalar exp() {
    return JetScalars.chain(jet, Exp.FUNCTION, Exp.FUNCTION);
  }

  @Override // from LogInterface
  public Scalar log() {
    return JetScalars.chain(jet, Log.FUNCTION, Scalar::reciprocal);
  }

  @Override // from PowerInterface
  public Scalar power(Scalar exponent) {
    OptionalInt optionalInt = Scalars.optionalInt(exponent);
    if (optionalInt.isPresent())
      return Scalars.mul().raise(this, optionalInt.orElseThrow());
    ScalarUnaryOperator dPower = Power.withExponent(exponent.subtract(RealScalar.ONE));
    ScalarUnaryOperator df = scalar -> dPower.apply(scalar).multiply(exponent);
    return JetScalars.chain(jet, Power.withExponent(exponent), df);
  }

  @Override // from SignInterface
  public Scalar sign() {
    return JetScalars.chain(jet, Sign.FUNCTION, Scalar::zero);
  }

  @Override // from SqrtInterface
  public Scalar sqrt() {
    return power(Rational.HALF);
  }

  @Override // from TrigonometryInterface
  public Scalar cos() {
    return JetScalars.chain(jet, Cos.FUNCTION, scalar -> Sin.FUNCTION.apply(scalar).negate());
  }

  @Override // from TrigonometryInterface
  public Scalar cosh() {
    return JetScalars.chain(jet, Cosh.FUNCTION, Sinh.FUNCTION);
  }

  @Override // from TrigonometryInterface
  public Scalar sin() {
    return JetScalars.chain(jet, Sin.FUNCTION, Cos.FUNCTION);
  }

  @Override // from TrigonometryInterface
  public Scalar sinh() {
    return JetScalars.chain(jet, Sinh.FUNCTION, Cosh.FUNCTION);
  }

  @Override // from MultiplexScalar
  public Scalar eachMap(UnaryOperator<Scalar> unaryOperator) {
    return new JetScalar(jet.maps(unaryOperator));
  }

  @Override // from MultiplexScalar
  public boolean allMatch(Predicate<Scalar> predicate) {
    return jet.stream().map(Scalar.class::cast).allMatch(predicate);
  }

  @Override // from Comparable
  public int compareTo(Scalar scalar) {
    return scalar instanceof JetScalar jetScalar //
        ? TensorComparator.INSTANCE.compare(jet, jetScalar.jet)
        : TensorComparator.INSTANCE.compare(jet, Join.of(Tensors.of(scalar), Array.zeros(jet.length() - 1)));
  }

  // ---
  @Override
  public int hashCode() {
    return jet.hashCode();
  }

  @Override
  public boolean equals(Object object) {
    return object instanceof JetScalar jetScalar //
        && jet.equals(jetScalar.jet);
  }

  @Override
  public String toString() {
    return "J" + jet.toString();
  }
}
