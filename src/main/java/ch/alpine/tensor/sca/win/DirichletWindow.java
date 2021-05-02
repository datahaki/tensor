// code by jph
package ch.alpine.tensor.sca.win;

import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.api.ScalarUnaryOperator;
import ch.alpine.tensor.num.Boole;

/** DirichletWindow[1/2]=1
 * 
 * <p>inspired by
 * <a href="https://reference.wolfram.com/language/ref/DirichletWindow.html">DirichletWindow</a> */
public enum DirichletWindow implements ScalarUnaryOperator {
  FUNCTION;

  @Override
  public Scalar apply(Scalar x) {
    return Boole.of(StaticHelper.SEMI.isInside(x));
  }

  @Override // from Object
  public String toString() {
    return getClass().getSimpleName();
  }
}
