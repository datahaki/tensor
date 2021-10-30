// code by jph
package ch.alpine.tensor.usr;

import java.io.IOException;

import ch.alpine.tensor.ComplexScalar;
import ch.alpine.tensor.DoubleScalar;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Scalars;
import ch.alpine.tensor.img.ColorDataGradients;
import ch.alpine.tensor.sca.Abs;
import ch.alpine.tensor.sca.Arg;
import ch.alpine.tensor.sca.Clip;
import ch.alpine.tensor.sca.Clips;
import ch.alpine.tensor.sca.Imag;
import ch.alpine.tensor.sca.Sin;

/** inspired by document by Paul Bourke */
/* package */ class JuliaSinDemo implements BivariateEvaluation {
  private static final Scalar MAX = RealScalar.of(50);
  private static final int MAX_ITERATIONS = 10;
  // ---
  private final Scalar c;

  public JuliaSinDemo(Scalar c) {
    this.c = c;
  }

  @Override
  public Scalar apply(Scalar re, Scalar im) {
    Scalar z = ComplexScalar.of(re, im);
    for (int count = 0; count < MAX_ITERATIONS; ++count) {
      z = Sin.FUNCTION.apply(z).multiply(c);
      if (Scalars.lessThan(MAX, Abs.of(Imag.of(z))))
        return DoubleScalar.INDETERMINATE;
    }
    return Arg.FUNCTION.apply(z);
  }

  @Override
  public Clip clipX() {
    return Clips.interval(-2.3, +2.3);
  }

  @Override
  public Clip clipY() {
    return Clips.interval(-2.3, +2.3);
  }

  public static void main(String[] args) throws IOException {
    StaticHelper.export( //
        new JuliaSinDemo(ComplexScalar.of(1.1, 0.5)), //
        Arg.class, ColorDataGradients.HUE);
  }
}
