// code by jph
// https://mathematica.stackexchange.com/questions/9167/adapt-colorfunction-in-array-plot
package ch.alpine.tensor.usr;

import ch.alpine.tensor.ComplexScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.img.ColorDataGradients;
import ch.alpine.tensor.num.Pi;
import ch.alpine.tensor.red.Nest;
import ch.alpine.tensor.sca.ArcTan;
import ch.alpine.tensor.sca.Clips;
import ch.alpine.tensor.sca.Real;
import ch.alpine.tensor.sca.Sin;

/** inspired by mathematica's documentation of Gamma */
/* package */ enum SinDemo {
  ;
  static final BivariateEvaluation BIVARIATE_EVALUATION = new BivariateEvaluation( //
      Clips.absolute(Pi.VALUE), Clips.absolute(Pi.VALUE)) {
    @Override
    protected Scalar function(Scalar re, Scalar im) {
      Scalar seed = ComplexScalar.of(re, im);
      return Real.of(ArcTan.of(Nest.of(Sin.FUNCTION, seed, 2)));
    }
  };

  public static void main(String[] args) throws Exception {
    StaticHelper.export(BIVARIATE_EVALUATION, Sin.class, ColorDataGradients.SUNSET);
  }
}
