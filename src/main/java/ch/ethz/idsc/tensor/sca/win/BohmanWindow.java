// code by jph
package ch.ethz.idsc.tensor.sca.win;

import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.api.ScalarUnaryOperator;
import ch.ethz.idsc.tensor.num.Pi;
import ch.ethz.idsc.tensor.sca.Abs;
import ch.ethz.idsc.tensor.sca.Cos;
import ch.ethz.idsc.tensor.sca.Sin;

/** BohmanWindow[1/2]=0
 * 
 * <p>inspired by
 * <a href="https://reference.wolfram.com/language/ref/BohmanWindow.html">BohmanWindow</a> */
public enum BohmanWindow implements ScalarUnaryOperator {
  FUNCTION;

  @Override
  public Scalar apply(Scalar x) {
    if (StaticHelper.SEMI.isInside(x)) {
      x = Abs.FUNCTION.apply(x);
      Scalar x2pi = Pi.TWO.multiply(x);
      return RealScalar.ONE.subtract(x.add(x)).multiply(Cos.FUNCTION.apply(x2pi)) //
          .add(Sin.FUNCTION.apply(x2pi).divide(Pi.VALUE));
    }
    return RealScalar.ZERO;
  }

  @Override // from Object
  public String toString() {
    return getClass().getSimpleName();
  }
}
