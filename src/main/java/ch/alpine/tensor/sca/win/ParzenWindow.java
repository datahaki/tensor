// code by jph
package ch.alpine.tensor.sca.win;

import ch.alpine.tensor.RationalScalar;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Scalars;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.api.ScalarUnaryOperator;
import ch.alpine.tensor.num.Polynomial;
import ch.alpine.tensor.sca.Abs;

/** ParzenWindow[1/2]=0
 * 
 * <p>inspired by
 * <a href="https://reference.wolfram.com/language/ref/ParzenWindow.html">ParzenWindow</a> */
public enum ParzenWindow implements ScalarUnaryOperator {
  FUNCTION;

  private static final Scalar _1_4 = RationalScalar.of(1, 4);
  private static final ScalarUnaryOperator S1 = Polynomial.of(Tensors.vector(1, 0, -24, 48));
  private static final ScalarUnaryOperator S2 = Polynomial.of(Tensors.vector(2, -12, 24, -16));

  @Override
  public Scalar apply(Scalar x) {
    if (StaticHelper.SEMI.isInside(x)) {
      x = Abs.FUNCTION.apply(x);
      return Scalars.lessEquals(x, _1_4) //
          ? S1.apply(x)
          : S2.apply(x);
    }
    return RealScalar.ZERO;
  }

  @Override // from Object
  public String toString() {
    return "ParzenWindow";
  }
}
