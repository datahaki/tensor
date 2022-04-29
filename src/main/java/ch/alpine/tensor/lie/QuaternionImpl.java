// code by jph
// https://en.wikipedia.org/wiki/Quaternion
package ch.alpine.tensor.lie;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;

import ch.alpine.tensor.MultiplexScalar;
import ch.alpine.tensor.RationalScalar;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Scalars;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.TensorRuntimeException;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.alg.Append;
import ch.alpine.tensor.api.ComplexEmbedding;
import ch.alpine.tensor.chq.ExactScalarQ;
import ch.alpine.tensor.nrm.Hypot;
import ch.alpine.tensor.nrm.Vector2Norm;
import ch.alpine.tensor.num.BinaryPower;
import ch.alpine.tensor.num.ScalarProduct;
import ch.alpine.tensor.sca.Abs;
import ch.alpine.tensor.sca.N;
import ch.alpine.tensor.sca.exp.Exp;
import ch.alpine.tensor.sca.exp.Log;
import ch.alpine.tensor.sca.pow.Power;
import ch.alpine.tensor.sca.pow.Sqrt;
import ch.alpine.tensor.sca.tri.ArcCos;
import ch.alpine.tensor.sca.tri.Cos;
import ch.alpine.tensor.sca.tri.Sin;

/** @implSpec
 * This class is immutable and thread-safe. */
/* package */ class QuaternionImpl extends MultiplexScalar implements Quaternion, //
    Serializable {
  private static final BinaryPower<Scalar> BINARY_POWER = new BinaryPower<>(ScalarProduct.INSTANCE);
  // ---
  private final Scalar w;
  private final Tensor xyz;

  // ---
  public QuaternionImpl(Scalar w, Tensor xyz) {
    this.w = w;
    this.xyz = xyz;
  }

  @Override // from Quaternion
  public Quaternion multiply(Scalar scalar) {
    if (scalar instanceof Quaternion quaternion)
      return new QuaternionImpl( //
          w.multiply(quaternion.w()).subtract(xyz.dot(quaternion.xyz())), //
          xyz.multiply(quaternion.w()).add(quaternion.xyz().multiply(w())).add(Cross.of(xyz, quaternion.xyz())));
    if (scalar instanceof ComplexEmbedding complexEmbedding) {
      Scalar imag = complexEmbedding.imag();
      return multiply(new QuaternionImpl( //
          complexEmbedding.real(), //
          Tensors.of(imag, imag.zero(), imag.zero())));
    }
    return new QuaternionImpl(w.multiply(scalar), xyz.multiply(scalar));
  }

  @Override // from Quaternion
  public Quaternion negate() {
    return new QuaternionImpl(w.negate(), xyz.negate());
  }

  @Override // from Quaternion
  public Quaternion divide(Scalar scalar) {
    return scalar instanceof RealScalar //
        ? new QuaternionImpl(w.divide(scalar), xyz.divide(scalar))
        : multiply(scalar.reciprocal());
  }

  @Override // from Quaternion
  public Quaternion under(Scalar scalar) {
    return reciprocal().multiply(scalar);
  }

  @Override // from Quaternion
  public Quaternion reciprocal() {
    return conjugate().divide(absSquared());
  }

  @Override // from AbstractScalar
  protected Quaternion plus(Scalar scalar) {
    if (scalar instanceof Quaternion quaternion)
      return new QuaternionImpl(w.add(quaternion.w()), xyz.add(quaternion.xyz()));
    if (scalar instanceof RealScalar)
      return new QuaternionImpl(w.add(scalar), xyz);
    throw TensorRuntimeException.of(this, scalar);
  }

  @Override // from Scalar
  public Quaternion zero() {
    return new QuaternionImpl(w.zero(), xyz.map(Scalar::zero));
  }

  @Override // from Scalar
  public Scalar one() {
    return new QuaternionImpl(w.one(), xyz.map(Scalar::one).map(Scalar::zero));
  }

  // ---
  @Override // from AbsInterface
  public Scalar abs() {
    return Hypot.ofVector(Append.of(xyz, w));
  }

  @Override // from AbsInterface
  public Scalar absSquared() {
    // return Norm2Squared.ofVector(Append.of(xyz, w));
    return w.multiply(w).add(xyz.dot(xyz));
  }

  @Override // from ConjugateInterface
  public Quaternion conjugate() {
    return new QuaternionImpl(w, xyz.negate());
  }

  @Override // from ExpInterface
  public Quaternion exp() {
    Scalar vn = Vector2Norm.of(xyz);
    return new QuaternionImpl( //
        Cos.FUNCTION.apply(vn), //
        xyz.multiply(Sin.FUNCTION.apply(vn).divide(vn))) //
            .multiply(Exp.FUNCTION.apply(w));
  }

  @Override // from LogInterface
  public Quaternion log() {
    Scalar abs = abs();
    Scalar vn = Vector2Norm.of(xyz);
    return new QuaternionImpl( //
        Log.FUNCTION.apply(abs), //
        xyz.multiply(ArcCos.FUNCTION.apply(w.divide(abs))).divide(vn));
  }

  /** @param exponent
   * @return result in numeric precision */
  private Quaternion _power(Scalar exponent) {
    Scalar abs = abs();
    Scalar et = exponent.multiply(ArcCos.FUNCTION.apply(w.divide(abs)));
    Scalar qa = Power.of(abs, exponent);
    Scalar vn = Vector2Norm.of(xyz);
    return new QuaternionImpl( //
        Cos.FUNCTION.apply(et).multiply(qa), //
        Scalars.isZero(vn) //
            ? xyz.map(Scalar::zero)
            : xyz.multiply(Sin.FUNCTION.apply(et).multiply(qa).divide(vn)));
  }

  @Override // from PowerInterface
  public Quaternion power(Scalar exponent) {
    if (ExactScalarQ.of(this)) {
      Optional<BigInteger> optional = Scalars.optionalBigInteger(exponent);
      if (optional.isPresent())
        return (Quaternion) BINARY_POWER.raise(this, optional.orElseThrow());
    }
    return _power(exponent);
  }

  @Override // from SignInterface
  public Quaternion sign() {
    Quaternion scalar = divide(abs());
    return scalar.divide(Abs.FUNCTION.apply(scalar));
  }

  @Override // from SqrtInterface
  public Quaternion sqrt() {
    Scalar w_abs = w.add(abs());
    Scalar nre = Sqrt.FUNCTION.apply(w_abs.add(w_abs));
    return new QuaternionImpl(nre.multiply(RationalScalar.HALF), xyz.divide(nre));
  }

  private Scalar nonExact() {
    return ExactScalarQ.of(this) //
        ? new QuaternionImpl(N.DOUBLE.apply(w), xyz.map(N.DOUBLE))
        : this;
  }

  @Override // from TrigonometryInterface
  public Scalar cos() {
    return TrigonometrySeries.DEFAULT.cos(nonExact());
  }

  @Override // from TrigonometryInterface
  public Scalar cosh() {
    return TrigonometrySeries.DEFAULT.cosh(nonExact());
  }

  @Override // from TrigonometryInterface
  public Scalar sin() {
    return TrigonometrySeries.DEFAULT.sin(nonExact());
  }

  @Override // from TrigonometryInterface
  public Scalar sinh() {
    return TrigonometrySeries.DEFAULT.sinh(nonExact());
  }

  @Override // from MultiplexScalar
  public Scalar eachMap(UnaryOperator<Scalar> unaryOperator) {
    return new QuaternionImpl(unaryOperator.apply(w), xyz.map(unaryOperator));
  }

  @Override // from MultiplexScalar
  public boolean allMatch(Predicate<Scalar> predicate) {
    return predicate.test(w) //
        && xyz.stream().map(Scalar.class::cast).allMatch(predicate);
  }

  // ---
  @Override // from Quaternion
  public Scalar w() {
    return w;
  }

  @Override // from Quaternion
  public Tensor xyz() {
    return xyz.unmodifiable();
  }

  // ---
  @Override // from AbstractScalar
  public int hashCode() {
    return w.hashCode() + 31 * xyz.hashCode();
  }

  @Override // from AbstractScalar
  public boolean equals(Object object) {
    if (object instanceof Quaternion quaternion)
      return w.equals(quaternion.w()) //
          && xyz.equals(quaternion.xyz());
    if (object instanceof RealScalar scalar)
      return w.equals(scalar) //
          && xyz.stream().map(Scalar.class::cast).allMatch(Scalars::isZero);
    return false;
  }

  @Override // from AbstractScalar
  public String toString() {
    // Mathematica
    // return String.format("Quaternion[%s, %s, %s, %s]", w, xyz.get(0), xyz.get(1), xyz.get(2));
    return "{\"w\": " + w + ", \"xyz\": " + xyz + "}";
  }
}
