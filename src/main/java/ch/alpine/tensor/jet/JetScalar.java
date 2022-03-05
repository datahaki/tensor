// code by jph
package ch.alpine.tensor.jet;

import java.io.Serializable;
import java.util.OptionalInt;

import ch.alpine.tensor.AbstractScalar;
import ch.alpine.tensor.RationalScalar;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Scalars;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.TensorRuntimeException;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.alg.Array;
import ch.alpine.tensor.alg.Join;
import ch.alpine.tensor.alg.TensorComparator;
import ch.alpine.tensor.alg.UnitVector;
import ch.alpine.tensor.alg.VectorQ;
import ch.alpine.tensor.api.AbsInterface;
import ch.alpine.tensor.api.ExpInterface;
import ch.alpine.tensor.api.LogInterface;
import ch.alpine.tensor.api.PowerInterface;
import ch.alpine.tensor.api.SignInterface;
import ch.alpine.tensor.api.SqrtInterface;
import ch.alpine.tensor.api.TrigonometryInterface;
import ch.alpine.tensor.pdf.CDF;
import ch.alpine.tensor.pdf.PDF;
import ch.alpine.tensor.pdf.UnivariateDistribution;
import ch.alpine.tensor.sca.Abs;
import ch.alpine.tensor.sca.AbsSquared;
import ch.alpine.tensor.sca.Cos;
import ch.alpine.tensor.sca.Cosh;
import ch.alpine.tensor.sca.Exp;
import ch.alpine.tensor.sca.Log;
import ch.alpine.tensor.sca.Power;
import ch.alpine.tensor.sca.Sign;
import ch.alpine.tensor.sca.Sin;
import ch.alpine.tensor.sca.Sinh;

/** API EXPERIMENTAL
 * 
 * automatic differentiation
 * 
 * The JetScalar is used to test the consistency between the {@link CDF} and {@link PDF}
 * of {@link UnivariateDistribution}s.
 * 
 * @implSpec
 * This class is immutable and thread-safe. */
// TODO TENSOR NUM general makeover and more tests
public class JetScalar extends AbstractScalar implements //
    AbsInterface, ExpInterface, LogInterface, PowerInterface, //
    SignInterface, SqrtInterface, TrigonometryInterface, //
    Comparable<Scalar>, Serializable {
  /** @param vector {f[x], f'[x], f''[x], ...}
   * @return */
  public static JetScalar of(Tensor vector) {
    if (vector.stream().anyMatch(JetScalar.class::isInstance))
      throw TensorRuntimeException.of(vector);
    return new JetScalar(VectorQ.require(vector).copy());
  }

  /** TODO TENSOR NUM important:
   * Distinguish between constants with value js == {v,0,...}
   * ... and variables with value js == {v,1,0,...}
   * 
   * @param scalar
   * @param n strictly positive
   * @return J{scalar, 1, 0, 0, ...} */
  public static JetScalar of(Scalar scalar, int n) {
    if (scalar instanceof JetScalar)
      throw TensorRuntimeException.of(scalar);
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
        ? new JetScalar(StaticHelper.product(vector, jetScalar.vector))
        : new JetScalar(vector.multiply(scalar));
  }

  @Override // from Scalar
  public Scalar negate() {
    return new JetScalar(vector.negate());
  }

  @Override // from Scalar
  public Scalar reciprocal() {
    return new JetScalar(StaticHelper.reciprocal(vector));
  }

  @Override // from Scalar
  public Scalar zero() {
    return new JetScalar(vector.map(Scalar::zero));
  }

  @Override // from Scalar
  public Scalar one() {
    return StaticHelper.CACHE_ONE.apply(vector.length());
  }

  @Override // from Scalar
  public Number number() {
    throw TensorRuntimeException.of(this);
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
    return StaticHelper.chain(vector, Abs.FUNCTION, Sign.FUNCTION);
  }

  @Override // from AbsInterface
  public Scalar absSquared() {
    return StaticHelper.chain(vector, AbsSquared.FUNCTION, s -> {
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
    return StaticHelper.chain(vector, Exp.FUNCTION, Exp.FUNCTION);
  }

  @Override // from LogInterface
  public Scalar log() {
    return StaticHelper.chain(vector, Log.FUNCTION, Scalar::reciprocal);
  }

  @Override // from PowerInterface
  public JetScalar power(Scalar exponent) {
    OptionalInt optionalInt = Scalars.optionalInt(exponent);
    if (optionalInt.isPresent()) {
      int expInt = optionalInt.getAsInt();
      if (0 <= expInt) // TODO TENSOR NUM exponent == zero!?
        return new JetScalar(StaticHelper.power(vector, expInt));
    }
    return StaticHelper.chain(vector, Power.function(exponent), //
        scalar -> Power.function(exponent.subtract(RealScalar.ONE)).apply(scalar).multiply(exponent));
  }

  @Override // from SignInterface
  public Scalar sign() {
    return StaticHelper.chain(vector, Sign.FUNCTION, Scalar::zero);
  }

  @Override // from SqrtInterface
  public Scalar sqrt() {
    return power(RationalScalar.HALF);
  }

  @Override // from TrigonometryInterface
  public Scalar cos() {
    return StaticHelper.chain(vector, Cos.FUNCTION, scalar -> Sin.FUNCTION.apply(scalar).negate());
  }

  @Override // from TrigonometryInterface
  public Scalar cosh() {
    return StaticHelper.chain(vector, Cosh.FUNCTION, Sinh.FUNCTION);
  }

  @Override // from TrigonometryInterface
  public Scalar sin() {
    return StaticHelper.chain(vector, Sin.FUNCTION, Cos.FUNCTION);
  }

  @Override // from TrigonometryInterface
  public Scalar sinh() {
    return StaticHelper.chain(vector, Sinh.FUNCTION, Cosh.FUNCTION);
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
