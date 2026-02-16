// code by jph
package ch.alpine.tensor;

import java.math.BigDecimal;
import java.math.BigInteger;

import ch.alpine.tensor.api.ComplexEmbedding;
import ch.alpine.tensor.num.Pi;
import ch.alpine.tensor.sca.Sign;
import ch.alpine.tensor.sca.exp.Exp;
import ch.alpine.tensor.sca.exp.Log;

/** suggested base class for implementations of {@link RealScalar} */
public abstract class AbstractRealScalar extends AbstractScalar implements RealScalar {
  static final double LOG_LO = 0.75;
  static final double LOG_HI = 1.3;
  // ---

  /** @return true if this scalar is zero, or strictly greater zero, false otherwise */
  protected final boolean isNonNegative() {
    return 0 <= signum();
  }

  // methods in this section are final
  @Override // from Scalar
  public final Scalar zero() {
    return ZERO;
  }

  @Override // from Scalar
  public final Scalar one() {
    return ONE;
  }

  @Override // from AbsInterface
  public final Scalar absSquared() {
    return multiply(this);
  }

  @Override // from ComplexEmbedding
  public final Scalar conjugate() {
    return this;
  }

  @Override // from ComplexEmbedding
  public final Scalar imag() {
    return ZERO; // consistent with Mathematica::Im[3.] == 0
  }

  @Override // from ComplexEmbedding
  public final Scalar real() {
    return this;
  }

  /** @return gives -1, 0, or 1 depending on whether this is negative, zero, or positive.
   * @see BigInteger#signum()
   * @see BigDecimal#signum() */
  protected abstract int signum();

  // ---
  /** @return this or this.negate() depending on whichever is non-negative */
  @Override // from AbsInterface
  public Scalar abs() {
    return isNonNegative() ? this : negate();
  }

  // methods are non-final because overriding classes may support better precision
  // or handling of NaN
  @Override // from ArcTanInterface
  public Scalar arcTan(Scalar x) {
    if (x instanceof ComplexScalar)
      return StaticHelper.arcTan(x, this);
    if (x instanceof RealScalar)
      return DoubleScalar.of(Math.atan2( //
          number().doubleValue(), // y
          x.number().doubleValue())); // x
    // return ArcTan.FUNCTION.apply(divide(x)); // ArcTan[x, y] == ArcTan[ y / x ]
    throw new Throw(this, x);
  }

  @Override // from ArgInterface
  public Scalar arg() {
    return isNonNegative() ? ZERO : Pi.VALUE;
  }

  @Override // from LogInterface
  public Scalar log() {
    if (isNonNegative()) {
      double value = number().doubleValue();
      if (LOG_LO < value && value < LOG_HI)
        return DoubleScalar.of(Math.log1p(subtract(one()).number().doubleValue()));
      return DoubleScalar.of(Math.log(value));
    }
    return ComplexScalarImpl.of(Log.FUNCTION.apply(negate()), Pi.VALUE);
  }

  @Override // from PowerInterface
  public Scalar power(Scalar exponent) {
    if (Scalars.isZero(this)) {
      if (Scalars.isZero(exponent))
        return ONE; // Mathematica evaluates 0^0 as Indeterminate
      if (exponent instanceof ComplexEmbedding complexEmbedding)
        if (Sign.isPositive(complexEmbedding.real()))
          return zero();
      throw new Throw(this, exponent);
    }
    if (exponent instanceof RealScalar) {
      double result = Math.pow(number().doubleValue(), exponent.number().doubleValue());
      if (result == result) // !Double::isNaN
        return DoubleScalar.of(result);
    }
    return Exp.FUNCTION.apply(exponent.multiply(Log.FUNCTION.apply(this)));
  }

  @Override // from SignInterface
  public Scalar sign() {
    return StaticHelper.SIGN[1 + signum()];
  }

  /** implementation is used by {@link DoubleScalar},
   * and is a fallback option for {@link Rational}
   * 
   * @return {@link ComplexScalar} if negative */
  protected Scalar _sqrt() {
    if (isNonNegative())
      return DoubleScalar.of(Math.sqrt(number().doubleValue()));
    return ComplexScalarImpl.of(zero(), DoubleScalar.of(Math.sqrt(-number().doubleValue())));
  }
}
