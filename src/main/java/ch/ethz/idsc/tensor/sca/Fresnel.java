// code by jph
package ch.ethz.idsc.tensor.sca;

import ch.ethz.idsc.tensor.ComplexScalar;
import ch.ethz.idsc.tensor.RationalScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.opt.Pi;
import ch.ethz.idsc.tensor.pdf.Erf;
import ch.ethz.idsc.tensor.red.Times;

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

  private static final Scalar FACTOR = ComplexScalar.of(1, 1).multiply(RationalScalar.HALF);
  private static final Scalar SCALE = Times.of(Sqrt.FUNCTION.apply(Pi.VALUE), RationalScalar.HALF, ComplexScalar.of(1, -1));

  @Override
  public Scalar apply(Scalar t) {
    return FACTOR.multiply(Erf.FUNCTION.apply(SCALE.multiply(t)));
  }
}
