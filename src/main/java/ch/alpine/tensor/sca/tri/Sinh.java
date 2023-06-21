// code by jph
package ch.alpine.tensor.sca.tri;

import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Throw;
import ch.alpine.tensor.api.ScalarUnaryOperator;

/** <pre>
 * Sinh[NaN] == NaN
 * </pre>
 * 
 * <p>Reference:
 * <a href="http://www.milefoot.com/math/complex/functionsofi.htm">functions of i</a>
 * 
 * <p>inspired by
 * <a href="https://reference.wolfram.com/language/ref/Sinh.html">Sinh</a>
 * 
 * @see ArcSinh */
public enum Sinh implements ScalarUnaryOperator {
  FUNCTION;

  @Override
  public Scalar apply(Scalar scalar) {
    if (scalar instanceof TrigonometryInterface trigonometryInterface)
      return trigonometryInterface.sinh();
    throw new Throw(scalar);
  }
}
