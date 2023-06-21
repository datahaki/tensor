// code by jph
package ch.alpine.tensor.sca;

import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.api.ScalarUnaryOperator;
import ch.alpine.tensor.num.Pi;
import ch.alpine.tensor.sca.exp.Exp;
import ch.alpine.tensor.sca.tri.ArcTan;

/** inspired by
 * <a href="https://reference.wolfram.com/language/ref/Gudermannian.html">Gudermannian</a> */
public enum Gudermannian implements ScalarUnaryOperator {
  FUNCTION;

  @Override
  public Scalar apply(Scalar scalar) {
    Scalar value = ArcTan.FUNCTION.apply(Exp.FUNCTION.apply(scalar));
    return value.add(value).subtract(Pi.HALF);
  }
}
