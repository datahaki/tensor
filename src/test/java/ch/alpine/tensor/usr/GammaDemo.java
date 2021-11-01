// code by jph
package ch.alpine.tensor.usr;

import ch.alpine.tensor.ComplexScalar;
import ch.alpine.tensor.DoubleScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.ext.HomeDirectory;
import ch.alpine.tensor.img.ColorDataGradients;
import ch.alpine.tensor.img.Raster;
import ch.alpine.tensor.io.Export;
import ch.alpine.tensor.red.Nest;
import ch.alpine.tensor.sca.Arg;
import ch.alpine.tensor.sca.Clip;
import ch.alpine.tensor.sca.Clips;
import ch.alpine.tensor.sca.Gamma;

/** inspired by Mathematica's documentation of Gamma */
/* package */ enum GammaDemo implements BivariateEvaluation {
  INSTANCE;

  private static final int DEPTH = 2;

  @Override
  public Scalar apply(Scalar re, Scalar im) {
    Scalar seed = ComplexScalar.of(re, im);
    try {
      return Arg.of(Nest.of(Gamma.FUNCTION, seed, DEPTH));
    } catch (Exception exception) {
      System.out.println("fail=" + seed);
    }
    return DoubleScalar.INDETERMINATE;
  }

  @Override
  public Clip clipX() {
    return Clips.interval(-1.25, -0.6);
  }

  @Override
  public Clip clipY() {
    return Clips.interval(-0.25, +0.25);
  }

  public static void main(String[] args) throws Exception {
    Tensor matrix = StaticHelper.image(INSTANCE, 192);
    Export.of(HomeDirectory.Pictures(GammaDemo.class.getSimpleName() + ".png"), //
        Raster.of(matrix, ColorDataGradients.HUE));
  }
}
