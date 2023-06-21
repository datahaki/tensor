// code by jph
package ch.alpine.tensor.sca.tri;

import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Throw;
import ch.alpine.tensor.api.ScalarUnaryOperator;

/** <pre>
 * Cos[NaN] == NaN
 * </pre>
 * 
 * <p>Reference:
 * <a href="http://www.milefoot.com/math/complex/functionsofi.htm">functions of i</a>
 * 
 * <p>inspired by
 * <a href="https://reference.wolfram.com/language/ref/Cos.html">Cos</a>
 * 
 * @see ArcCos */
public enum Cos implements ScalarUnaryOperator {
  FUNCTION;

  @Override
  public Scalar apply(Scalar scalar) {
    if (scalar instanceof TrigonometryInterface trigonometryInterface)
      return trigonometryInterface.cos();
    throw new Throw(scalar);
  }
}
