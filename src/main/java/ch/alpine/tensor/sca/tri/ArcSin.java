// code by jph
package ch.alpine.tensor.sca.tri;

import ch.alpine.tensor.ComplexScalar;
import ch.alpine.tensor.DoubleScalar;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.api.ScalarUnaryOperator;
import ch.alpine.tensor.sca.exp.Log;
import ch.alpine.tensor.sca.pow.Sqrt;

/** For real input in the interval [-1, 1] the returned angle is in the range -pi/2 through pi/2.
 * 
 * <pre>
 * ArcSin[NaN] == NaN
 * </pre>
 * 
 * <p>Reference:
 * <a href="http://www.milefoot.com/math/complex/functionsofi.htm">functions of i</a>
 * 
 * <p>inspired by
 * <a href="https://reference.wolfram.com/language/ref/ArcCos.html">ArcCos</a>
 * 
 * @see Sin */
public enum ArcSin implements ScalarUnaryOperator {
  FUNCTION;

  private static final Scalar I_NEGATE = ComplexScalar.I.negate();

  @Override
  public Scalar apply(Scalar scalar) {
    if (scalar instanceof RealScalar) {
      double value = scalar.number().doubleValue();
      if (-1 <= value && value <= 1)
        return DoubleScalar.of(Math.asin(value));
      if (Double.isNaN(value))
        return DoubleScalar.INDETERMINATE;
    }
    Scalar o_x2 = Sqrt.FUNCTION.apply(RealScalar.ONE.subtract(scalar.multiply(scalar)));
    return I_NEGATE.multiply(Log.FUNCTION.apply(ComplexScalar.I.multiply(scalar).add(o_x2)));
  }
}
