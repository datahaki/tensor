// code by jph
package ch.alpine.tensor.sca.gam;

import ch.alpine.tensor.ComplexScalar;
import ch.alpine.tensor.DoubleScalar;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Scalars;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Throw;
import ch.alpine.tensor.api.ScalarUnaryOperator;
import ch.alpine.tensor.num.Pi;
import ch.alpine.tensor.sca.Real;
import ch.alpine.tensor.sca.Round;
import ch.alpine.tensor.sca.Sign;
import ch.alpine.tensor.sca.exp.Exp;
import ch.alpine.tensor.sca.tri.Sinc;

/** Euler gamma function
 * <pre>
 * Gamma[x + 1] == x Gamma[x]
 * </pre>
 * 
 * <p>implementation also works for {@link ComplexScalar}s
 *
 * <p>the function is not defined at non-positive integers
 * Gamma[0], Gamma[-1], Gamma[-2] == ComplexInfinity
 * For input of this type, an Exception is thrown.
 * 
 * <p>the implementation throws an exception for input with
 * real part of large magnitude.
 * 
 * <p>typically the implementation gives precision within ~1E^-8
 * of the magnitude of the input.
 * 
 * <p>inspired by
 * <a href="https://reference.wolfram.com/language/ref/Gamma.html">Gamma</a> */
public enum Gamma implements ScalarUnaryOperator {
  FUNCTION;

  /** EulerGamma is the negative of the derivative D[Gamma[x]] at x == 1 */
  public static final Scalar EULER = DoubleScalar.of(0.577215664901532860606512090082);

  @Override
  public Scalar apply(Scalar z) {
    Scalar re = Real.FUNCTION.apply(z);
    Scalar round = Round.FUNCTION.apply(re);
    if (z.equals(round)) { // ..., -2, -1, 0, 1, 2, ...
      z = round;
      if (Scalars.lessEquals(z, RealScalar.ZERO)) // ..., -2, -1, 0
        throw new Throw(z); // ComplexInfinity
      try {
        return Factorial.of(Math.subtractExact(Scalars.intValueExact(round), 1));
      } catch (Exception exception) {
        // ---
      }
      return DoubleScalar.POSITIVE_INFINITY;
    }
    if (Sign.isPositive(re))
      return Exp.FUNCTION.apply(LogGammaRestricted.FUNCTION.apply(z));
    Scalar zp = RealScalar.ONE.subtract(z);
    return Exp.FUNCTION.apply(LogGammaRestricted.FUNCTION.apply(RealScalar.ONE.add(zp))) //
        .multiply(Sinc.FUNCTION.apply(Pi.VALUE.multiply(zp))).reciprocal();
  }

  /** @param tensor
   * @return tensor with all scalars replaced with their gamma evaluation */
  @SuppressWarnings("unchecked")
  public static <T extends Tensor> T of(T tensor) {
    return (T) tensor.map(FUNCTION);
  }
}
