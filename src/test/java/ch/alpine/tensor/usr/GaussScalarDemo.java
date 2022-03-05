// code by jph
package ch.alpine.tensor.usr;

import java.io.IOException;

import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.img.ColorDataGradients;
import ch.alpine.tensor.num.GaussScalar;
import ch.alpine.tensor.sca.Clip;
import ch.alpine.tensor.sca.Clips;

/* package */ enum GaussScalarDemo implements BivariateEvaluation {
  INSTANCE;

  private static final int PRIME = 719;

  @Override
  public Scalar apply(Scalar re, Scalar im) {
    GaussScalar x = GaussScalar.of(re.number().intValue(), PRIME);
    GaussScalar y = GaussScalar.of(im.number().intValue(), PRIME);
    return RealScalar.of(x.divide(y).number());
  }

  @Override
  public Clip clipX() {
    return Clips.interval(1, PRIME - 1);
  }

  @Override
  public Clip clipY() {
    return Clips.interval(1, PRIME - 1);
  }

  public static void main(String[] args) throws IOException {
    StaticHelper.export(INSTANCE, GaussScalar.class, ColorDataGradients.STARRY_NIGHT);
  }
}
