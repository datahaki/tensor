// code by jph
// https://en.wikipedia.org/wiki/Quaternion
package ch.ethz.idsc.tensor;

import java.io.Serializable;
import java.math.BigInteger;
import java.math.MathContext;
import java.util.Optional;

import ch.ethz.idsc.tensor.alg.Append;
import ch.ethz.idsc.tensor.lie.Cross;
import ch.ethz.idsc.tensor.red.Hypot;
import ch.ethz.idsc.tensor.red.Norm;
import ch.ethz.idsc.tensor.sca.Abs;
import ch.ethz.idsc.tensor.sca.ArcCos;
import ch.ethz.idsc.tensor.sca.Chop;
import ch.ethz.idsc.tensor.sca.ChopInterface;
import ch.ethz.idsc.tensor.sca.ComplexEmbedding;
import ch.ethz.idsc.tensor.sca.Cos;
import ch.ethz.idsc.tensor.sca.Exp;
import ch.ethz.idsc.tensor.sca.Log;
import ch.ethz.idsc.tensor.sca.N;
import ch.ethz.idsc.tensor.sca.NInterface;
import ch.ethz.idsc.tensor.sca.Power;
import ch.ethz.idsc.tensor.sca.Sin;
import ch.ethz.idsc.tensor.sca.Sqrt;

/* package */ class QuaternionImpl extends AbstractScalar implements Quaternion, //
    ChopInterface, ExactScalarQInterface, NInterface, Serializable {
  private static final long serialVersionUID = 5384670613322108553L;
  // ---
  private final Scalar w;
  private final Tensor xyz;

  public QuaternionImpl(Scalar w, Tensor xyz) {
    this.w = w;
    this.xyz = xyz;
  }

  @Override // from Quaternion
  public Quaternion multiply(Scalar scalar) {
    if (scalar instanceof Quaternion) {
      Quaternion quaternion = (Quaternion) scalar;
      return new QuaternionImpl( //
          w.multiply(quaternion.w()).subtract(xyz.dot(quaternion.xyz())), //
          xyz.multiply(quaternion.w()).add(quaternion.xyz().multiply(w())).add(Cross.of(xyz, quaternion.xyz())));
    }
    if (scalar instanceof ComplexEmbedding) {
      ComplexEmbedding complexEmbedding = (ComplexEmbedding) scalar;
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
    if (scalar instanceof RealScalar)
      return new QuaternionImpl(w.divide(scalar), xyz.divide(scalar));
    return multiply(scalar.reciprocal());
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
    if (scalar instanceof Quaternion) {
      Quaternion quaternion = (Quaternion) scalar;
      return new QuaternionImpl(w.add(quaternion.w()), xyz.add(quaternion.xyz()));
    }
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
    return new QuaternionImpl(w.one(), xyz.map(Scalar::zero));
  }

  @Override // from Scalar
  public Number number() {
    throw TensorRuntimeException.of(this);
  }

  /***************************************************/
  @Override // from AbsInterface
  public Scalar abs() {
    return Hypot.ofVector(Append.of(xyz, w));
  }

  @Override // from AbsInterface
  public Scalar absSquared() {
    // return Norm2Squared.ofVector(Append.of(xyz, w));
    return w.multiply(w).add(xyz.dot(xyz));
  }

  @Override // from ChopInterface
  public Quaternion chop(Chop chop) {
    return new QuaternionImpl(chop.apply(w), xyz.map(chop));
  }

  @Override // from ConjugateInterface
  public Quaternion conjugate() {
    return new QuaternionImpl(w, xyz.negate());
  }

  @Override // from ExpInterface
  public Quaternion exp() {
    Scalar vn = Norm._2.ofVector(xyz);
    return new QuaternionImpl( //
        Cos.FUNCTION.apply(vn), //
        xyz.multiply(Sin.FUNCTION.apply(vn).divide(vn))) //
            .multiply(Exp.FUNCTION.apply(w));
  }

  @Override // from ExactScalarQInterface
  public boolean isExactScalar() {
    return ExactScalarQ.of(w) //
        && ExactTensorQ.of(xyz);
  }

  @Override // from LogInterface
  public Quaternion log() {
    Scalar abs = abs();
    Scalar vn = Norm._2.ofVector(xyz);
    return new QuaternionImpl( //
        Log.FUNCTION.apply(abs), //
        xyz.multiply(ArcCos.FUNCTION.apply(w.divide(abs))).divide(vn));
  }

  @Override // from NInterface
  public Scalar n() {
    return new QuaternionImpl(N.DOUBLE.apply(w), xyz.map(N.DOUBLE));
  }

  @Override // from NInterface
  public Scalar n(MathContext mathContext) {
    N n = N.in(mathContext.getPrecision());
    return new QuaternionImpl(n.apply(w), xyz.map(n));
  }

  /** @param exponent
   * @return result in numeric precision */
  private Quaternion _power(Scalar exponent) {
    Scalar abs = abs();
    Scalar et = exponent.multiply(ArcCos.FUNCTION.apply(w.divide(abs)));
    Scalar qa = Power.of(abs, exponent);
    Scalar vn = Norm._2.ofVector(xyz);
    return new QuaternionImpl( //
        Cos.FUNCTION.apply(et).multiply(qa), //
        Scalars.isZero(vn) //
            ? xyz.map(Scalar::zero)
            : xyz.multiply(Sin.FUNCTION.apply(et).multiply(qa).divide(vn)));
  }

  @Override // from PowerInterface
  public Quaternion power(Scalar exponent) {
    if (isExactScalar()) {
      Optional<BigInteger> optional = Scalars.optionalBigInteger(exponent);
      if (optional.isPresent())
        return (Quaternion) StaticHelper.BINARY_POWER.raise(this, optional.get());
    }
    return _power(exponent);
  }

  @Override // from SignInterface
  public Quaternion sign() {
    Quaternion scalar = divide(abs());
    return scalar.divide(Abs.FUNCTION.apply(scalar));
  }

  @Override // from SignInterface
  public int signInt() {
    throw TensorRuntimeException.of(this);
  }

  @Override // from SqrtInterface
  public Quaternion sqrt() {
    Scalar w_abs = w.add(abs());
    Scalar nre = Sqrt.FUNCTION.apply(w_abs.add(w_abs));
    return new QuaternionImpl(nre.multiply(RationalScalar.HALF), xyz.divide(nre));
  }

  @Override // from TrigonometryInterface
  public Scalar cos() {
    return TrigonometrySeries.DEFAULT.cos(isExactScalar() ? n() : this);
  }

  @Override // from TrigonometryInterface
  public Scalar cosh() {
    return TrigonometrySeries.DEFAULT.cosh(isExactScalar() ? n() : this);
  }

  @Override // from TrigonometryInterface
  public Scalar sin() {
    return TrigonometrySeries.DEFAULT.sin(isExactScalar() ? n() : this);
  }

  @Override // from TrigonometryInterface
  public Scalar sinh() {
    return TrigonometrySeries.DEFAULT.sinh(isExactScalar() ? n() : this);
  }

  /***************************************************/
  @Override // from Quaternion
  public Scalar w() {
    return w;
  }

  @Override // from Quaternion
  public Tensor xyz() {
    return xyz.unmodifiable();
  }

  /***************************************************/
  @Override // from AbstractScalar
  public int hashCode() {
    return w.hashCode() + 31 * xyz.hashCode();
  }

  @Override // from AbstractScalar
  public boolean equals(Object object) {
    if (object instanceof Quaternion) {
      Quaternion quaternion = (Quaternion) object;
      return w.equals(quaternion.w()) //
          && xyz.equals(quaternion.xyz());
    }
    if (object instanceof RealScalar) {
      Scalar scalar = (RealScalar) object;
      return w.equals(scalar) //
          && xyz.stream().map(Scalar.class::cast).allMatch(Scalars::isZero);
    }
    return false;
  }

  @Override // from AbstractScalar
  public String toString() {
    // Mathematica
    // return String.format("Quaternion[%s, %s, %s, %s]", w, xyz.get(0), xyz.get(1), xyz.get(2));
    return "{\"w\": " + w + ", \"xyz\": " + xyz + "}";
  }
}
