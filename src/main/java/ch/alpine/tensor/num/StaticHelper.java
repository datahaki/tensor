// code by jph
package ch.alpine.tensor.num;

import java.math.BigInteger;

import ch.alpine.tensor.RationalScalar;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.alg.Range;
import ch.alpine.tensor.ext.Cache;
import ch.alpine.tensor.qty.Quantity;
import ch.alpine.tensor.qty.QuantityUnit;
import ch.alpine.tensor.qty.Unit;
import ch.alpine.tensor.red.Times;
import ch.alpine.tensor.sca.Abs;

/* package */ enum StaticHelper {
  ;
  /** stores sqrt roots of gauss scalars */
  public static final Cache<GaussScalar, GaussScalar> SQRT = Cache.of(StaticHelper::sqrt, 256 * 3);

  public static GaussScalar sqrt(GaussScalar gaussScalar) {
    for (BigInteger index = BigInteger.ZERO; //
        index.compareTo(gaussScalar.prime()) < 0; //
        index = index.add(BigInteger.ONE))
      if (index.multiply(index).mod(gaussScalar.prime()).equals(gaussScalar.number()))
        return GaussScalar.of(index, gaussScalar.prime());
    return null;
  }

  // ---
  // TODO function does not result in Mathematica standard for all input
  public static Scalar normalForm(Scalar scalar) {
    return scalar instanceof RealScalar //
        ? Abs.FUNCTION.apply(scalar)
        : scalar;
  }

  /** Hint:
   * ordering of coefficients is <em>reversed</em> compared to
   * MATLAB::polyval, MATLAB::polyfit, etc. !
   * 
   * Example:
   * <pre>
   * derivative_coeffs[{a, b, c, d}] == {b, 2*c, 3*d}
   * </pre>
   * 
   * Remark:
   * Consider the polynomial that maps temperatures (in Kelvin) to pressure (in bar)
   * with coefficients {-13[bar], 0.27[K^-1*bar]}
   * The derivative maps temperatures to quantities with unit "K^-1*bar"
   * {0.27[K^-1*bar], 0.0[K^-2*bar]}
   * in order to preserve the units of the domain.
   * 
   * @param coeffs
   * @return coefficients of polynomial that is the derivative of the polynomial defined by given coeffs
   * @throws Exception if given coeffs is not a vector */
  public static Tensor derivative_coeffs(Tensor coeffs) {
    int length = coeffs.length();
    if (length == 2) {
      /* a linear polynomial is mapped to a linear polynomial (with zero factor).
       * this ensures that the unit of the argument in the polynomial evaluation
       * is checked. */
      Scalar a = coeffs.Get(0);
      Scalar b = coeffs.Get(1);
      Unit unit = QuantityUnit.of(b).add(QuantityUnit.of(a).negate()); // of domain
      return Tensors.of(b, b.zero().multiply(Quantity.of(b.one(), unit)));
    }
    return length == 1 //
        ? Tensors.of(coeffs.Get(0).zero())
        : Times.of(coeffs.extract(1, length), Range.of(1, length));
  }

  /** @param coeffs
   * @return */
  public static Tensor integral_coeffs(Tensor coeffs) {
    int length = coeffs.length();
    Tensor tensor = Tensors.reserve(length + 1);
    if (1 < length) {
      Scalar a = coeffs.Get(0);
      Scalar b = coeffs.Get(1);
      Unit unit = QuantityUnit.of(a).add(QuantityUnit.of(b).negate()); // of domain
      tensor.append(a.zero().multiply(Quantity.of(a.one(), unit)));
    }
    for (int index = 0; index < coeffs.length(); ++index)
      tensor.append(coeffs.Get(index).multiply(RationalScalar.of(1, index + 1)));
    return tensor;
  }
}
