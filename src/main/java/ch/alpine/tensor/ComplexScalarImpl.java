// code by jph
package ch.alpine.tensor;

import java.io.Serializable;
import java.math.BigInteger;
import java.math.MathContext;
import java.util.Optional;

import ch.alpine.tensor.api.ChopInterface;
import ch.alpine.tensor.api.ComplexEmbedding;
import ch.alpine.tensor.api.ExactScalarQInterface;
import ch.alpine.tensor.api.MachineNumberQInterface;
import ch.alpine.tensor.api.NInterface;
import ch.alpine.tensor.nrm.Hypot;
import ch.alpine.tensor.num.BinaryPower;
import ch.alpine.tensor.num.ScalarProduct;
import ch.alpine.tensor.qty.Quantity;
import ch.alpine.tensor.sca.Abs;
import ch.alpine.tensor.sca.Ceiling;
import ch.alpine.tensor.sca.Chop;
import ch.alpine.tensor.sca.Floor;
import ch.alpine.tensor.sca.N;
import ch.alpine.tensor.sca.Round;
import ch.alpine.tensor.sca.exp.Exp;
import ch.alpine.tensor.sca.exp.Log;
import ch.alpine.tensor.sca.tri.ArcTan;
import ch.alpine.tensor.sca.tri.Cos;
import ch.alpine.tensor.sca.tri.Cosh;
import ch.alpine.tensor.sca.tri.Sin;
import ch.alpine.tensor.sca.tri.Sinh;

/** @implSpec
 * This class is immutable and thread-safe. */
/* package */ class ComplexScalarImpl extends AbstractScalar implements ComplexScalar, //
    ChopInterface, ExactScalarQInterface, MachineNumberQInterface, NInterface, Serializable {
  private static final BinaryPower<Scalar> BINARY_POWER = new BinaryPower<>(ScalarProduct.INSTANCE);

  /** creator with package visibility
   * 
   * @param re neither a {@link ComplexScalar}, or {@link Quantity}
   * @param im neither a {@link ComplexScalar}, or {@link Quantity}
   * @return */
  public static Scalar of(Scalar re, Scalar im) {
    return Scalars.isZero(im) //
        ? re
        : new ComplexScalarImpl(re, im);
  }

  /** @param scalar
   * @return true if operation is carried out in {@link ComplexScalarImpl} */
  private static boolean isLocal(Scalar scalar) {
    return scalar instanceof ComplexEmbedding //
        && !(scalar instanceof Quantity);
  }

  // ---
  private final Scalar re;
  private final Scalar im;

  private ComplexScalarImpl(Scalar re, Scalar im) {
    this.re = re;
    this.im = im;
  }

  @Override // from Scalar
  public Scalar negate() {
    return new ComplexScalarImpl(re.negate(), im.negate());
  }

  @Override // from Scalar
  public Scalar multiply(Scalar scalar) {
    if (isLocal(scalar)) {
      ComplexEmbedding z = (ComplexEmbedding) scalar;
      Scalar z_re = z.real();
      Scalar z_im = z.imag();
      return of( //
          re.multiply(z_re).subtract(im.multiply(z_im)), //
          re.multiply(z_im).add(im.multiply(z_re)));
    }
    return scalar.multiply(this);
  }

  @Override // from AbstractScalar
  public Scalar divide(Scalar scalar) {
    if (isLocal(scalar)) {
      ComplexEmbedding z = (ComplexEmbedding) scalar;
      return ComplexHelper.division(re, im, z.real(), z.imag());
    }
    return scalar.under(this);
  }

  @Override // from AbstractScalar
  public Scalar under(Scalar scalar) {
    if (isLocal(scalar)) {
      ComplexEmbedding z = (ComplexEmbedding) scalar;
      return ComplexHelper.division(z.real(), z.imag(), re, im);
    }
    return scalar.divide(this);
  }

  @Override // from Scalar
  public Scalar reciprocal() {
    return of( //
        Scalars.isZero(re) ? re : ComplexHelper.c_dcd(re, im).reciprocal(), //
        ComplexHelper.c_dcd(im, re).reciprocal().negate()); // using the assumption that im is never zero
  }

  @Override // from Scalar
  public Scalar zero() {
    return re.zero().add(im.zero());
  }

  @Override // from Scalar
  public Scalar one() {
    return re.one();
  }

  @Override // from Scalar
  public Number number() {
    throw TensorRuntimeException.of(this);
  }

  // ---
  @Override // from AbstractScalar
  protected Scalar plus(Scalar scalar) {
    if (scalar instanceof ComplexEmbedding z)
      return of(re.add(z.real()), im.add(z.imag()));
    throw TensorRuntimeException.of(this, scalar);
  }

  // ---
  @Override // from AbsInterface
  public Scalar abs() { // "complex modulus"
    return Hypot.of(re, im);
  }

  @Override // from AbsInterface
  public Scalar absSquared() {
    return re.multiply(re).add(im.multiply(im));
  }

  @Override // from ArcTanInterface
  public Scalar arcTan(Scalar x) {
    return StaticHelper.arcTan(x, this);
  }

  @Override // from ArgInterface
  public Scalar arg() {
    return ArcTan.of(re, im); // Mathematica::ArcTan[x, y]
  }

  @Override // from RoundingInterface
  public Scalar ceiling() {
    return of(Ceiling.FUNCTION.apply(re), Ceiling.FUNCTION.apply(im));
  }

  @Override // from ChopInterface
  public Scalar chop(Chop chop) {
    return of(chop.apply(re), chop.apply(im));
  }

  @Override // from ComplexEmbedding
  public Scalar conjugate() {
    return of(re, im.negate());
  }

  @Override // from ExpInterface
  public Scalar exp() {
    // construct in polar coordinates
    return ComplexScalar.fromPolar(Exp.FUNCTION.apply(real()), imag());
  }

  @Override // from RoundingInterface
  public Scalar floor() {
    return of(Floor.FUNCTION.apply(re), Floor.FUNCTION.apply(im));
  }

  @Override // from LogInterface
  public Scalar log() {
    return of(Log.FUNCTION.apply(abs()), arg());
  }

  @Override // from ComplexEmbedding
  public Scalar imag() {
    return im;
  }

  @Override // from ExactNumberInterface
  public boolean isExactScalar() {
    return ExactScalarQ.of(re) //
        && ExactScalarQ.of(im);
  }

  @Override // MachineNumberQInterface
  public boolean isMachineNumber() {
    return MachineNumberQ.of(re) //
        && MachineNumberQ.of(im);
  }

  @Override // from NInterface
  public Scalar n() {
    return of(N.DOUBLE.apply(re), N.DOUBLE.apply(im));
  }

  @Override // from NInterface
  public Scalar n(MathContext mathContext) {
    N n = N.in(mathContext.getPrecision());
    return of(n.apply(re), n.apply(im));
  }

  @Override // from PowerInterface
  public Scalar power(Scalar exponent) {
    if (isExactScalar()) {
      Optional<BigInteger> optional = Scalars.optionalBigInteger(exponent);
      if (optional.isPresent())
        return BINARY_POWER.raise(this, optional.orElseThrow());
    }
    return Exp.FUNCTION.apply(exponent.multiply(Log.FUNCTION.apply(this)));
  }

  @Override // from ComplexEmbedding
  public Scalar real() {
    return re;
  }

  @Override // from RoundingInterface
  public Scalar round() {
    return of(Round.FUNCTION.apply(re), Round.FUNCTION.apply(im));
  }

  @Override // from SignInterface
  public Scalar sign() {
    Scalar scalar = divide(abs());
    return scalar.divide(Abs.FUNCTION.apply(scalar));
  }

  @Override // from SqrtInterface
  public Scalar sqrt() {
    return ComplexHelper.sqrt(re, im);
  }

  @Override // from TrigonometryInterface
  public Scalar cos() {
    return of( //
        Cos.of(re).multiply(Cosh.of(im)), //
        Sin.of(re).multiply(Sinh.of(im)).negate());
  }

  @Override // from TrigonometryInterface
  public Scalar cosh() {
    return of( //
        Cosh.of(re).multiply(Cos.of(im)), //
        Sinh.of(re).multiply(Sin.of(im)));
  }

  @Override // from TrigonometryInterface
  public Scalar sin() {
    return of( //
        Sin.of(re).multiply(Cosh.of(im)), //
        Cos.of(re).multiply(Sinh.of(im)));
  }

  @Override // from TrigonometryInterface
  public Scalar sinh() {
    return of( //
        Sinh.of(re).multiply(Cos.of(im)), //
        Cosh.of(re).multiply(Sin.of(im)));
  }

  // ---
  @Override // from AbstractScalar
  public int hashCode() {
    return re.hashCode() + 31 * im.hashCode();
  }

  @Override // from AbstractScalar
  public boolean equals(Object object) {
    return object instanceof ComplexEmbedding z //
        && re.equals(z.real()) //
        && im.equals(z.imag());
  }

  @Override // from AbstractScalar
  public String toString() {
    StringBuilder stringBuilder = new StringBuilder(48); // initial capacity
    String imag = ScalarParser.imagToString(im);
    if (Scalars.nonZero(re)) {
      stringBuilder.append(re);
      if (!imag.startsWith("-"))
        stringBuilder.append('+');
    }
    stringBuilder.append(imag);
    return stringBuilder.toString();
  }
}
