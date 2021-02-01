// code by jph
package ch.ethz.idsc.tensor.sca;

import ch.ethz.idsc.tensor.RationalScalar;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.api.ScalarUnaryOperator;
import ch.ethz.idsc.tensor.num.Pi;

/** <pre>
 * LogGamma[x] == Log[Gamma[x]]
 * </pre>
 * 
 * <p>Careful: Generally not consistent with Mathematica for z with Re[z] < 0.
 * 
 * <p>References:
 * "Gamma, Beta, and Related Functions" in NR, 2007
 * 
 * "A Precision Approximation of the Gamma Function"
 * by C. Lanczos, 1964
 * 
 * <p>inspired by
 * <a href="https://reference.wolfram.com/language/ref/LogGamma.html">LogGamma</a>
 * 
 * @see Gamma
 * @see Beta */
public enum LogGamma implements ScalarUnaryOperator {
  FUNCTION;

  private static final Tensor COEFFS = Tensors.vector( //
      57.1562356658629235, -59.5979603554754912, //
      14.1360979747417471, -0.491913816097620199, 0.339946499848118887E-4, //
      0.465236289270485756E-4, -0.983744753048795646E-4, 0.158088703224912494E-3, //
      -0.210264441724104883E-3, 0.217439618115212643E-3, -0.164318106536763890E-3, //
      0.844182239838527433E-4, -0.261908384015814087E-4, 0.368991826595316234E-5);
  private static final Scalar _671_128 = RationalScalar.of(671, 128);
  private static final Scalar SEED = RealScalar.of(0.999999999999997092);
  private static final Scalar FACTOR = RealScalar.of(2.5066282746310005);

  @Override
  public Scalar apply(Scalar z) {
    if (Sign.isPositive(Real.FUNCTION.apply(z)))
      return positive(z);
    Scalar zp = RealScalar.ONE.subtract(z);
    return Log.FUNCTION.apply(Sinc.FUNCTION.apply(Pi.VALUE.multiply(zp))).add(positive(RealScalar.ONE.add(zp))).negate();
  }

  /** @param z with Re[z] positive
   * @return */
  /* package */ static Scalar positive(Scalar z) {
    Scalar y = z;
    Scalar tmp = z.add(_671_128);
    tmp = z.add(RationalScalar.HALF).multiply(Log.FUNCTION.apply(tmp)).subtract(tmp);
    Scalar sum = SEED;
    for (int index = 0; index < 14; ++index) {
      y = y.add(RealScalar.ONE);
      sum = sum.add(COEFFS.Get(index).divide(y));
    }
    return tmp.add(Log.FUNCTION.apply(FACTOR.multiply(sum).divide(z)));
  }

  /** @param tensor
   * @return tensor with all scalars replaced with their gamma evaluation */
  @SuppressWarnings("unchecked")
  public static <T extends Tensor> T of(T tensor) {
    return (T) tensor.map(FUNCTION);
  }
}
