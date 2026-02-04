// code by jph
package ch.alpine.tensor.sca.var;

import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Scalars;
import ch.alpine.tensor.api.ScalarUnaryOperator;
import ch.alpine.tensor.io.MathematicaFormat;
import ch.alpine.tensor.pdf.BinningMethods;
import ch.alpine.tensor.sca.Sign;
import ch.alpine.tensor.sca.exp.Log;

/** Reference:
 * "Radial Basis Functions in General Use", eq (3.7.7)
 * in NR, 2007
 * 
 * <p>The returned values have the unit of r0 squared.
 * For example if r0 has unit "m" then the returned unit is "m^2".
 * 
 * @see BinningMethods
 * 
 * @param r0 positive */
public record ThinPlateSplineVariogram(Scalar r0) implements ScalarUnaryOperator {
  /** @param r0 positive
   * @return */
  public static ScalarUnaryOperator of(Number r0) {
    return new ThinPlateSplineVariogram(RealScalar.of(r0));
  }

  // ---
  public ThinPlateSplineVariogram {
    Sign.requirePositive(r0);
  }

  @Override
  public Scalar apply(Scalar r) {
    return Scalars.isZero(r) //
        ? r.multiply(r) // units consistent with case 0 < r
        : r.multiply(r).multiply(Log.FUNCTION.apply(r.divide(r0)));
  }

  @Override // from Object
  public String toString() {
    return MathematicaFormat.concise("ThinPlateSplineVariogram", r0);
  }
}
