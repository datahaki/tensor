// code by jph
package ch.ethz.idsc.tensor;

import ch.ethz.idsc.tensor.sca.ComplexEmbedding;
import ch.ethz.idsc.tensor.sca.Exp;
import ch.ethz.idsc.tensor.sca.Log;
import ch.ethz.idsc.tensor.sca.SignInterface;

/** suggested base class for implementations of {@link RealScalar} */
public abstract class AbstractRealScalar extends AbstractScalar implements RealScalar {
  /** @return this or this.negate() depending on whichever is non-negative */
  @Override // from Scalar
  public final Scalar abs() {
    return isNonNegative() ? this : negate();
  }

  /***************************************************/
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

  /***************************************************/
  // methods are non-final because overriding classes may support better precision
  @Override // from ArcTanInterface
  public Scalar arcTan(Scalar x) {
    if (x instanceof ComplexScalar)
      return StaticHelper.arcTan(x, this);
    if (Scalars.isZero(x))
      x = RealScalar.ZERO;
    if (x instanceof RealScalar)
      return DoubleScalar.of(Math.atan2( //
          number().doubleValue(), // y
          x.number().doubleValue())); // x
    throw TensorRuntimeException.of(x, this);
  }

  @Override // from ArgInterface
  public Scalar arg() {
    return isNonNegative() ? RealScalar.ZERO : DoubleScalar.of(Math.PI);
  }

  @Override // from PowerInterface
  public Scalar power(Scalar exponent) {
    if (Scalars.isZero(this)) {
      if (Scalars.isZero(exponent))
        return RealScalar.ONE; // <- not generic
      if (exponent instanceof ComplexEmbedding) {
        ComplexEmbedding complexEmbedding = (ComplexEmbedding) exponent;
        SignInterface signInterface = (SignInterface) complexEmbedding.real();
        if (signInterface.signInt() == 1)
          return zero();
      }
      throw TensorRuntimeException.of(this, exponent);
    }
    if (exponent instanceof RealScalar)
      return DoubleScalar.of(Math.pow(number().doubleValue(), exponent.number().doubleValue()));
    return Exp.FUNCTION.apply(exponent.multiply(Log.FUNCTION.apply(this)));
  }

  /** implementation is used by {@link DoubleScalar},
   * and is a fallback option for {@link RationalScalar}
   * 
   * @return {@link ComplexScalar} if negative */
  @Override // from SqrtInterface
  public Scalar sqrt() {
    if (isNonNegative())
      return DoubleScalar.of(Math.sqrt(number().doubleValue()));
    return ComplexScalar.of(zero(), DoubleScalar.of(Math.sqrt(-number().doubleValue())));
  }

  /***************************************************/
  /** @return true if this scalar is zero, or strictly greater zero, false otherwise */
  protected final boolean isNonNegative() {
    return 0 <= signInt();
  }
}