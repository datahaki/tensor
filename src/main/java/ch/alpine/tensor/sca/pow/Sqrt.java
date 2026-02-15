// code by jph
package ch.alpine.tensor.sca.pow;

import ch.alpine.tensor.ComplexScalar;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Scalars;
import ch.alpine.tensor.api.ScalarUnaryOperator;
import ch.alpine.tensor.ext.PackageTestAccess;
import ch.alpine.tensor.sca.Sign;

/** <pre>
 * Sqrt[NaN] == NaN
 * </pre>
 * 
 * inspired by
 * <a href="https://reference.wolfram.com/language/ref/Sqrt.html">Sqrt</a> */
public enum Sqrt implements ScalarUnaryOperator {
  FUNCTION;

  @Override
  public Scalar apply(Scalar scalar) {
    if (scalar instanceof SqrtInterface sqrtInterface)
      return sqrtInterface.sqrt();
    if (scalar instanceof RealScalar)
      return Sign.isPositiveOrZero(scalar) //
          ? series(scalar)
          : ComplexScalar.of(scalar.zero(), series(scalar.negate()));
    return series(scalar);
  }

  /** computation of square-root using Newton iteration
   * 
   * @param scalar
   * @return root of given scalar */
  // implementation inspired by Luciano Culacciatti
  // http://www.codeproject.com/Tips/257031/Implementing-SqrtRoot-in-BigDecimal
  // TODO TENSOR could be improved, ask gemini: Binomial Series expansion
  @PackageTestAccess
  static Scalar series(Scalar scalar) {
    if (Scalars.isZero(scalar))
      return scalar;
    Scalar xn0 = scalar.zero();
    Scalar xn1 = scalar.one();
    while (true) {
      Scalar xn2 = xn1.subtract(xn1.multiply(xn1).subtract(scalar).divide(xn1.add(xn1)));
      if (xn0.equals(xn2) || xn1.equals(xn2))
        return xn2;
      xn0 = xn1;
      xn1 = xn2;
    }
  }
}
