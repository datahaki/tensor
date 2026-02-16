// code by jph
package ch.alpine.tensor.sca.erf;

import ch.alpine.tensor.ComplexScalar;
import ch.alpine.tensor.Rational;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.api.ScalarUnaryOperator;
import ch.alpine.tensor.num.Pi;
import ch.alpine.tensor.sca.Conjugate;
import ch.alpine.tensor.sca.pow.Sqrt;

/** <pre>
 * Fresnel[z] = FresnelC[z] + FresnelS[z] * I
 * 
 * Fresnel[z] = Integral 0 -> z Exp[I * pi / 2 * s ^ 2] ds
 * </pre>
 * 
 * <p>Reference:
 * "Fresnel Integrals, Cosine and Sine Integrals" in NR, 2007
 * 
 * <p>inspired by
 * <a href="https://reference.wolfram.com/language/ref/FresnelC.html">FresnelC</a> and
 * <a href="https://reference.wolfram.com/language/ref/FresnelS.html">FresnelS</a> */
public enum Fresnel implements ScalarUnaryOperator {
  FUNCTION;

  private static final Scalar FACTOR = ComplexScalar.of(Rational.HALF, Rational.HALF);
  private static final Scalar SCALE = Conjugate.FUNCTION.apply(FACTOR).multiply(Sqrt.FUNCTION.apply(Pi.VALUE));

  @Override
  public Scalar apply(Scalar t) {
    return FACTOR.multiply(Erf.FUNCTION.apply(SCALE.multiply(t)));
  }
}
