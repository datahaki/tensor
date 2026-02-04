// code by jph
package ch.alpine.tensor.sca.tri;

import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.api.ScalarUnaryOperator;
import ch.alpine.tensor.chq.ExactScalarQ;
import ch.alpine.tensor.ext.PackageTestAccess;
import ch.alpine.tensor.sca.N;

/** <pre>
 * Cosh[NaN] == NaN
 * </pre>
 * 
 * <p>Reference:
 * <a href="http://www.milefoot.com/math/complex/functionsofi.htm">functions of i</a>
 * 
 * <p>inspired by
 * <a href="https://reference.wolfram.com/language/ref/Cosh.html">Cosh</a>
 * 
 * @see ArcCosh */
public enum Cosh implements ScalarUnaryOperator {
  FUNCTION;

  @Override
  public Scalar apply(Scalar scalar) {
    if (scalar instanceof TrigonometryInterface trigonometryInterface)
      return trigonometryInterface.cosh();
    return series(ExactScalarQ.of(scalar) ? N.DOUBLE.apply(scalar) : scalar);
  }

  /** @param x
   * @return hyperbolic cosine of x */
  @PackageTestAccess
  static Scalar series(Scalar x) {
    Scalar xn1 = x.one();
    Scalar add = x.one();
    final Scalar x2 = x.multiply(x);
    int index = 0;
    Scalar xn0 = null;
    while (!xn1.equals(xn0)) {
      xn0 = xn1;
      add = add.multiply(x2).divide(RealScalar.of(++index * ++index));
      xn1 = xn1.add(add);
    }
    return xn1;
  }
}
