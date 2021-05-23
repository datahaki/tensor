// code by jph
package ch.alpine.tensor.itp;

import ch.alpine.tensor.RationalScalar;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Scalars;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.api.ScalarUnaryOperator;
import ch.alpine.tensor.num.Series;
import ch.alpine.tensor.sca.Abs;

/** function has support over the interval [-2, 2] */
public class MitchellNetravaliKernel implements ScalarUnaryOperator {
  /** @param b
   * @param c
   * @return */
  public static ScalarUnaryOperator of(Scalar b, Scalar c) {
    return new MitchellNetravaliKernel(b, c);
  }

  public static ScalarUnaryOperator of(Number b, Number c) {
    return of(RealScalar.of(b), RealScalar.of(c));
  }

  /***************************************************/
  private final ScalarUnaryOperator y;
  private final ScalarUnaryOperator z;

  private MitchellNetravaliKernel(Scalar b, Scalar c) {
    Scalar y_0 = RealScalar.of(6) //
        .add(RealScalar.of(-2).multiply(b));
    Scalar y_2 = RealScalar.of(-18) //
        .add(RealScalar.of(12).multiply(b)) //
        .add(RealScalar.of(6).multiply(c));
    Scalar y_3 = RealScalar.of(12) //
        .add(RealScalar.of(-9).multiply(b)) //
        .add(RealScalar.of(-6).multiply(c));
    y = Series.of(Tensors.of(y_0, y_0.zero(), y_2, y_3).multiply(RationalScalar.of(1, 6)));
    // ---
    Scalar z_0 = RealScalar.of(8).multiply(b) //
        .add(RealScalar.of(24).multiply(c));
    Scalar z_1 = RealScalar.of(-12).multiply(b) //
        .add(RealScalar.of(-48).multiply(c));
    Scalar z_2 = RealScalar.of(6).multiply(b) //
        .add(RealScalar.of(30).multiply(c));
    Scalar z_3 = b.negate() //
        .add(RealScalar.of(-6).multiply(c));
    z = Series.of(Tensors.of(z_0, z_1, z_2, z_3).multiply(RationalScalar.of(1, 6)));
  }

  @Override
  public Scalar apply(Scalar x) {
    x = Abs.FUNCTION.apply(x);
    if (Scalars.lessEquals(x, RealScalar.ONE))
      return y.apply(x);
    if (Scalars.lessEquals(x, RealScalar.TWO))
      return z.apply(x);
    return RealScalar.ZERO;
  }
}
