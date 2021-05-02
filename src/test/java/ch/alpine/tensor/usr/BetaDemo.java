// code by jph
package ch.alpine.tensor.usr;

import ch.alpine.tensor.ComplexScalar;
import ch.alpine.tensor.DoubleScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.img.ColorDataGradients;
import ch.alpine.tensor.red.Nest;
import ch.alpine.tensor.sca.Arg;
import ch.alpine.tensor.sca.Beta;
import ch.alpine.tensor.sca.Clips;

/** inspired by Mathematica's documentation of Beta */
/* package */ enum BetaDemo {
  ;
  private static final int DEPTH = 2;
  private static final BivariateEvaluation BIVARIATE_EVALUATION = new BivariateEvaluation( //
      Clips.absolute(2.0), //
      Clips.absolute(2.0)) {
    @Override
    protected Scalar function(Scalar re, Scalar im) {
      Scalar seed = ComplexScalar.of(re, im);
      try {
        return Arg.of(Nest.of(z -> Beta.of(z, z), seed, DEPTH));
      } catch (Exception exception) {
        // ---
      }
      return DoubleScalar.INDETERMINATE;
    }
  };

  public static void main(String[] args) throws Exception {
    StaticHelper.export(BIVARIATE_EVALUATION, Beta.class, ColorDataGradients.HUE);
  }
}
