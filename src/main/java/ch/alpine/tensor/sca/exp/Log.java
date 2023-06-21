// code by jph
package ch.alpine.tensor.sca.exp;

import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Scalars;
import ch.alpine.tensor.Throw;
import ch.alpine.tensor.api.ScalarUnaryOperator;

/** gives the logarithm of a {@link Scalar} that implements {@link LogInterface}
 * 
 * <p>inspired by
 * <a href="https://reference.wolfram.com/language/ref/Log.html">Log</a>
 * 
 * @see LogInterface
 * @see Log10
 * @see Exp */
public enum Log implements ScalarUnaryOperator {
  FUNCTION;

  @Override
  public Scalar apply(Scalar scalar) {
    if (scalar instanceof LogInterface logInterface)
      return logInterface.log();
    throw new Throw(scalar);
  }

  /** Hint:
   * for natural logarithm use {@link Log},
   * for base 10 use {@link Log10}.
   * 
   * @param base not equal to 1
   * @return logarithm function with given base
   * @throws Exception if base == 1 */
  public static ScalarUnaryOperator base(Scalar base) {
    Scalar log_b = FUNCTION.apply(base);
    if (Scalars.isZero(log_b))
      throw new Throw(base);
    return scalar -> FUNCTION.apply(scalar).divide(log_b);
  }

  /** @param base not equal to 1
   * @return logarithm function with given base
   * @throws Exception if base == 1 */
  public static ScalarUnaryOperator base(Number base) {
    return base(RealScalar.of(base));
  }
}
