// code by jph
// https://mathematica.stackexchange.com/questions/9167/adapt-colorfunction-in-array-plot
package ch.alpine.tensor.usr;

import ch.alpine.tensor.ComplexScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.img.ColorDataGradients;
import ch.alpine.tensor.num.Pi;
import ch.alpine.tensor.red.Nest;
import ch.alpine.tensor.sca.Clip;
import ch.alpine.tensor.sca.Clips;
import ch.alpine.tensor.sca.Re;
import ch.alpine.tensor.sca.tri.ArcTan;
import ch.alpine.tensor.sca.tri.Sin;

/** inspired by mathematica's documentation of Gamma */
/* package */ enum SinDemo implements BivariateEvaluation {
  INSTANCE;

  @Override
  public Scalar apply(Scalar re, Scalar im) {
    Scalar seed = ComplexScalar.of(re, im);
    return Re.of(ArcTan.of(Nest.of(Sin.FUNCTION, seed, 2)));
  }

  @Override
  public Clip clipX() {
    return Clips.absolute(Pi.VALUE);
  }

  @Override
  public Clip clipY() {
    return Clips.absolute(Pi.VALUE);
  }

  public static void main(String[] args) throws Exception {
    StaticHelper.export(INSTANCE, Sin.class, ColorDataGradients.SUNSET);
  }
}
