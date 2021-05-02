// code by jph
package ch.alpine.tensor.usr;

import ch.alpine.tensor.ComplexScalar;
import ch.alpine.tensor.DoubleScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.ext.HomeDirectory;
import ch.alpine.tensor.img.ArrayPlot;
import ch.alpine.tensor.img.ColorDataGradients;
import ch.alpine.tensor.io.Export;
import ch.alpine.tensor.red.Nest;
import ch.alpine.tensor.sca.Arg;
import ch.alpine.tensor.sca.Clips;
import ch.alpine.tensor.sca.Gamma;

/** inspired by Mathematica's documentation of Gamma */
/* package */ enum GammaDemo {
  ;
  private static final int DEPTH = 2;
  // ---
  public static final BivariateEvaluation BIVARIATE_EVALUATION = new BivariateEvaluation( //
      Clips.interval(-1.25, -0.6), //
      Clips.interval(-0.25, +0.25)) {
    @Override
    protected Scalar function(Scalar re, Scalar im) {
      Scalar seed = ComplexScalar.of(re, im);
      try {
        return Arg.of(Nest.of(Gamma.FUNCTION, seed, DEPTH));
      } catch (Exception exception) {
        System.out.println("fail=" + seed);
      }
      return DoubleScalar.INDETERMINATE;
    }
  };

  public static void main(String[] args) throws Exception {
    Tensor matrix = BIVARIATE_EVALUATION.image(192);
    Export.of(HomeDirectory.Pictures(GammaDemo.class.getSimpleName() + ".png"), //
        ArrayPlot.of(matrix, ColorDataGradients.HUE));
  }
}
