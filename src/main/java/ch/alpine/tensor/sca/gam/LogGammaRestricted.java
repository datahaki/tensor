// code by jph
package ch.alpine.tensor.sca.gam;

import ch.alpine.tensor.Rational;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.api.ScalarUnaryOperator;
import ch.alpine.tensor.sca.exp.Log;

/** Evaluation of LogGamma for arguments with positive real part
 * 
 * References:
 * "Gamma, Beta, and Related Functions" in NR, 2007
 * 
 * "A Precision Approximation of the Gamma Function"
 * by C. Lanczos, 1964 */
/* package */ enum LogGammaRestricted implements ScalarUnaryOperator {
  FUNCTION;

  private static final Tensor COEFFS = Tensors.vector( //
      57.1562356658629235, -59.5979603554754912, //
      14.1360979747417471, -0.491913816097620199, 0.339946499848118887E-4, //
      0.465236289270485756E-4, -0.983744753048795646E-4, 0.158088703224912494E-3, //
      -0.210264441724104883E-3, 0.217439618115212643E-3, -0.164318106536763890E-3, //
      0.844182239838527433E-4, -0.261908384015814087E-4, 0.368991826595316234E-5);
  private static final Scalar _671_128 = Rational.of(671, 128);
  private static final Scalar SEED = RealScalar.of(0.999999999999997092);
  private static final Scalar FACTOR = RealScalar.of(2.5066282746310005);

  /** @param z with Re[z] positive
   * @return */
  @Override
  public Scalar apply(Scalar z) {
    Scalar y = z;
    Scalar tmp = z.add(_671_128);
    tmp = z.add(Rational.HALF).multiply(Log.FUNCTION.apply(tmp)).subtract(tmp);
    Scalar sum = SEED;
    for (int index = 0; index < 14; ++index) {
      y = y.add(RealScalar.ONE);
      sum = sum.add(COEFFS.Get(index).divide(y));
    }
    return tmp.add(Log.FUNCTION.apply(FACTOR.multiply(sum).divide(z)));
  }
}
