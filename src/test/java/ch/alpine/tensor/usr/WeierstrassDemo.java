// code by jph
package ch.alpine.tensor.usr;

import ch.alpine.tensor.DoubleScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.img.ColorDataGradients;
import ch.alpine.tensor.sca.Clip;
import ch.alpine.tensor.sca.Clips;
import ch.alpine.tensor.sca.pow.Power;
import ch.alpine.tensor.sca.tri.Cos;

/* package */ enum WeierstrassDemo implements BivariateEvaluation {
  INSTANCE;

  private static final int DEPTH = 10;

  @Override
  public Scalar apply(Scalar re, Scalar im) {
    Scalar s = DoubleScalar.of(0.0);
    // b = 7.0 has to be a positive odd integer
    for (int n = 0; n < DEPTH; ++n)
      s = s.add(Power.of(im, n).multiply(Cos.FUNCTION.apply(Power.of(7.0, n).multiply(DoubleScalar.of(Math.PI)).multiply(re))));
    return s;
  }

  @Override
  public Clip clipX() {
    return Clips.interval(0.25, 1.0);
  }

  @Override
  public Clip clipY() {
    return Clips.interval(0.25, 1.0);
  }

  public static void main(String[] args) throws Exception {
    StaticHelper.export(INSTANCE, DoubleScalar.class, ColorDataGradients.ALPINE);
  }
}
