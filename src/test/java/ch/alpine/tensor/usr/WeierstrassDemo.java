// code by jph
package ch.alpine.tensor.usr;

import ch.alpine.tensor.DoubleScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.img.ColorDataGradients;
import ch.alpine.tensor.sca.Clips;
import ch.alpine.tensor.sca.Cos;
import ch.alpine.tensor.sca.Power;

/* package */ enum WeierstrassDemo {
  ;
  private static final int DEPTH = 10;
  private static final BivariateEvaluation BIVARIATE_EVALUATION = new BivariateEvaluation( //
      Clips.interval(0.25, 1.0), //
      Clips.interval(0.25, 1.0)) {
    @Override
    protected Scalar function(Scalar re, Scalar im) {
      Scalar s = DoubleScalar.of(0.0);
      // b = 7.0 has to be a positive odd integer
      for (int n = 0; n < DEPTH; ++n)
        s = s.add(Power.of(im, n).multiply(Cos.of(Power.of(7.0, n).multiply(DoubleScalar.of(Math.PI)).multiply(re))));
      return s;
    }
  };

  public static void main(String[] args) throws Exception {
    StaticHelper.export(BIVARIATE_EVALUATION, DoubleScalar.class, ColorDataGradients.ALPINE);
  }
}
