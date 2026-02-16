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
import ch.alpine.tensor.sca.tri.Cos;
import ch.alpine.tensor.sca.tri.Cosh;
import ch.alpine.tensor.sca.tri.Sin;
import ch.alpine.tensor.sca.tri.Sinh;
import ch.alpine.tensor.sca.tri.TrigonometryInterface;

/** EXPERIMENTAL TENSOR
 * 
 * automatic differentiation
 * 
 * Applications:
 * JetScalar is used to test the consistency between the {@link CDF} and {@link PDF}
 * of {@link UnivariateDistribution}s.
 * JetScalar validates Hermite subdivision in repo sophus.
 * 
 * @implSpec
 * This class is immutable and thread-safe. */
public class JetScalar extends MultiplexScalar implements //
    AbsInterface, ExpInterface, LogInterface, PowerInterface, //
    SignInterface, SqrtInterface, TrigonometryInterface, //
    Comparable<Scalar>, Serializable {
  /** @param vector {f[x], f'[x], f''[x], ...}
   * @return */
  public static JetScalar of(Tensor vector) {
    if (vector.stream().anyMatch(JetScalar.class::isInstance))
      throw new Throw(vector);
    return new JetScalar(VectorQ.require(vector).copy());
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
  public static JetScalar of(Scalar scalar, int n) {
    if (scalar instanceof JetScalar)
      throw new Throw(scalar);
    if (n == 1)
      return new JetScalar(Tensors.of(scalar));
    Tensor vector = UnitVector.of(n, 1);
    vector.set(scalar, 0);
    return new JetScalar(vector);
  }

  // ---
  private final Tensor vector;

  /* package */ JetScalar(Tensor vector) {
    this.vector = vector;
  }

  public Tensor vector() {
    return vector.unmodifiable();
  }

  @Override // from Scalar
  public Scalar multiply(Scalar scalar) {
    return scalar instanceof JetScalar jetScalar //
        ? new JetScalar(JetScalars.product(vector, jetScalar.vector))
        : new JetScalar(vector.multiply(scalar));
  }

  @Override // from Scalar
  public Scalar negate() {
    return new JetScalar(vector.negate());
  }

  @Override // from Scalar
  public Scalar reciprocal() {
    return new JetScalar(JetScalars.reciprocal(vector));
  }

  @Override // from Scalar
  public Scalar zero() {
    return new JetScalar(vector.maps(Scalar::zero));
  }

  @Override // from Scalar
  public Scalar one() {
    return JetScalars.CACHE_ONE.apply(vector.length());
  }

  @Override // from AbstractScalar
  protected Scalar plus(Scalar scalar) {
    if (scalar instanceof JetScalar jetScalar)
      return new JetScalar(vector.add(jetScalar.vector));
    Tensor result = vector.copy();
    result.set(scalar::add, 0); // this + constant scalar
    return new JetScalar(result);
  }
  // ---

  @Override // from AbsInterface
  public Scalar abs() {
    return JetScalars.chain(vector, Abs.FUNCTION, Sign.FUNCTION);
  }

  @Override // from AbsInterface
  public Scalar absSquared() {
    return JetScalars.chain(vector, AbsSquared.FUNCTION, s -> {
      Scalar v1 = Abs.FUNCTION.apply(s);
      Scalar v2 = Sign.FUNCTION.apply(s);
      Scalar v3 = v1.multiply(v2);
      return v3.add(v3);
    });
  }
  // @Override // from ArcTanInterface
  // public Scalar arcTan(Scalar x) {
  // JetScalar divide = (JetScalar) divide(x);
  // return StaticHelper.chain(divide.vector, ArcTan.FUNCTION, s -> RealScalar.ONE.divide(RealScalar.ONE.add(s.multiply(s))));
  // }

  @Override // from ExpInterface
  public Scalar exp() {
    return JetScalars.chain(vector, Exp.FUNCTION, Exp.FUNCTION);
  }

  @Override // from LogInterface
  public Scalar log() {
    return JetScalars.chain(vector, Log.FUNCTION, Scalar::reciprocal);
  }

  @Override // from PowerInterface
  public JetScalar power(Scalar exponent) {
    OptionalInt optionalInt = Scalars.optionalInt(exponent);
    if (optionalInt.isPresent()) {
      int expInt = optionalInt.orElseThrow();
      if (0 <= expInt) // TODO TENSOR JET exponent == zero!?
        return new JetScalar(JetScalars.power(vector, expInt));
    }
    return JetScalars.chain(vector, Power.function(exponent), //
        scalar -> Power.function(exponent.subtract(RealScalar.ONE)).apply(scalar).multiply(exponent));
  }

  @Override // from SignInterface
  public Scalar sign() {
    return JetScalars.chain(vector, Sign.FUNCTION, Scalar::zero);
  }

  @Override // from SqrtInterface
  public Scalar sqrt() {
    return power(Rational.HALF);
  }

  @Override // from TrigonometryInterface
  public Scalar cos() {
    return JetScalars.chain(vector, Cos.FUNCTION, scalar -> Sin.FUNCTION.apply(scalar).negate());
  }

  @Override // from TrigonometryInterface
  public Scalar cosh() {
    return JetScalars.chain(vector, Cosh.FUNCTION, Sinh.FUNCTION);
  }

  @Override // from TrigonometryInterface
  public Scalar sin() {
    return JetScalars.chain(vector, Sin.FUNCTION, Cos.FUNCTION);
  }

  @Override // from TrigonometryInterface
  public Scalar sinh() {
    return JetScalars.chain(vector, Sinh.FUNCTION, Cosh.FUNCTION);
  }

  @Override // from MultiplexScalar
  public Scalar eachMap(UnaryOperator<Scalar> unaryOperator) {
    return new JetScalar(vector.maps(unaryOperator));
  }

  @Override // from MultiplexScalar
  public boolean allMatch(Predicate<Scalar> predicate) {
    return vector.stream().map(Scalar.class::cast).allMatch(predicate);
  }

  @Override // from Comparable
  public int compareTo(Scalar scalar) {
    return scalar instanceof JetScalar jetScalar //
        ? TensorComparator.INSTANCE.compare(vector, jetScalar.vector)
        : TensorComparator.INSTANCE.compare(vector, Join.of(Tensors.of(scalar), Array.zeros(vector.length() - 1)));
  }

  // ---
  @Override
  public int hashCode() {
    return vector.hashCode();
  }

  @Override
  public boolean equals(Object object) {
    return object instanceof JetScalar jetScalar //
        && vector.equals(jetScalar.vector);
  }

  @Override
  public String toString() {
    return "J" + vector.toString();
  }
}
