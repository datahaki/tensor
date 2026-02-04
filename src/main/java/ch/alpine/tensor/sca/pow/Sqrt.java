// code by jph
package ch.alpine.tensor.sca.pow;

import ch.alpine.tensor.ComplexScalar;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Scalars;
import ch.alpine.tensor.api.ScalarUnaryOperator;
import ch.alpine.tensor.ext.BoundedLinkedList;
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
    BoundedLinkedList<Scalar> boundedLinkedList = new BoundedLinkedList<>(2);
    while (!boundedLinkedList.contains(xn1)) {
      boundedLinkedList.add(xn1);
      xn0 = xn1;
      Scalar fx = xn0.multiply(xn0).subtract(scalar);
      Scalar fpx = xn0.add(xn0); // equals to 2 * xn0
      xn1 = xn0.subtract(fx.divide(fpx));
    }
    return xn1;
  }
}
