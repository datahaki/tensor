// code by jph
package ch.alpine.tensor.itp;

import ch.alpine.tensor.RationalScalar;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Scalars;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.api.ScalarUnaryOperator;
import ch.alpine.tensor.io.MathematicaFormat;
import ch.alpine.tensor.sca.Abs;
import ch.alpine.tensor.sca.ply.Polynomial;

/** function has support over the interval [-2, 2]
 * 
 * Reference:
 * https://en.wikipedia.org/wiki/Mitchell%E2%80%93Netravali_filters */
public class MitchellNetravaliKernel implements ScalarUnaryOperator {
  private static final ScalarUnaryOperator STANDARD = of(RationalScalar.of(1, 3), RationalScalar.of(1, 3));

  /** @param b typically inside unit interval
   * @param c typically inside unit interval
   * @return */
  public static ScalarUnaryOperator of(Scalar b, Scalar c) {
    return new MitchellNetravaliKernel(b, c);
  }

  /** @param b typically inside unit interval
   * @param c typically inside unit interval
   * @return */
  public static ScalarUnaryOperator of(Number b, Number c) {
    return of(RealScalar.of(b), RealScalar.of(c));
  }

  /** @return with b == c == 1/3 */
  public static ScalarUnaryOperator standard() {
    return STANDARD;
  }

  // ---
  private final Scalar b;
  private final Scalar c;
  private final ScalarUnaryOperator y;
  private final ScalarUnaryOperator z;

  private MitchellNetravaliKernel(Scalar b, Scalar c) {
    this.b = b;
    this.c = c;
    Scalar y_0 = RealScalar.of(6) //
        .add(RealScalar.of(-2).multiply(b));
    Scalar y_2 = RealScalar.of(-18) //
        .add(RealScalar.of(12).multiply(b)) //
        .add(RealScalar.of(6).multiply(c));
    Scalar y_3 = RealScalar.of(12) //
        .add(RealScalar.of(-9).multiply(b)) //
        .add(RealScalar.of(-6).multiply(c));
    y = Polynomial.of(Tensors.of(y_0, y_0.zero(), y_2, y_3).multiply(RationalScalar.of(1, 6)));
    // ---
    Scalar z_0 = RealScalar.of(8).multiply(b) //
        .add(RealScalar.of(24).multiply(c));
    Scalar z_1 = RealScalar.of(-12).multiply(b) //
        .add(RealScalar.of(-48).multiply(c));
    Scalar z_2 = RealScalar.of(6).multiply(b) //
        .add(RealScalar.of(30).multiply(c));
    Scalar z_3 = b.negate() //
        .add(RealScalar.of(-6).multiply(c));
    z = Polynomial.of(Tensors.of(z_0, z_1, z_2, z_3).multiply(RationalScalar.of(1, 6)));
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

  @Override
  public String toString() {
    return MathematicaFormat.concise("MitchellNetravaliKernel", b, c);
  }
}
