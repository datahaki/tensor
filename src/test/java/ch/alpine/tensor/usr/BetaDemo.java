// code by jph
package ch.alpine.tensor.usr;

import ch.alpine.tensor.ComplexScalar;
import ch.alpine.tensor.DoubleScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.img.ColorDataGradients;
import ch.alpine.tensor.red.Nest;
import ch.alpine.tensor.sca.Arg;
import ch.alpine.tensor.sca.Clip;
import ch.alpine.tensor.sca.Clips;
import ch.alpine.tensor.sca.gam.Beta;

/** inspired by Mathematica's documentation of Beta */
/* package */ enum BetaDemo implements BivariateEvaluation {
  INSTANCE;

  private static final int DEPTH = 2;

  @Override
  public Scalar apply(Scalar re, Scalar im) {
    Scalar seed = ComplexScalar.of(re, im);
    try {
      return Arg.of(Nest.of(z -> Beta.of(z, z), seed, DEPTH));
    } catch (Exception exception) {
      // ---
    }
    return DoubleScalar.INDETERMINATE;
  }

  @Override
  public Clip clipX() {
    return Clips.absolute(2.0);
  }

  @Override
  public Clip clipY() {
    return Clips.absolute(2.0);
  }

  public static void main(String[] args) throws Exception {
    StaticHelper.export(INSTANCE, Beta.class, ColorDataGradients.HUE);
  }
}
