// code by jph
package ch.alpine.tensor.pdf;

import java.util.random.RandomGenerator;

import ch.alpine.tensor.ComplexScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.pdf.c.UniformDistribution;
import ch.alpine.tensor.sca.Clip;
import ch.alpine.tensor.sca.Clips;

/** test scope only */
public class ComplexUniformDistribution implements Distribution {
  private static final Distribution UNIT = of(Clips.unit(), Clips.unit());

  public static Distribution of(Clip clip_re, Clip clip_im) {
    return new ComplexUniformDistribution(clip_re, clip_im);
  }

  public static Distribution unit() {
    return UNIT;
  }

  // ---
  private final Distribution distribution_re;
  private final Distribution distribution_im;

  private ComplexUniformDistribution(Clip clip_re, Clip clip_im) {
    distribution_re = UniformDistribution.of(clip_re);
    distribution_im = UniformDistribution.of(clip_im);
  }

  @Override // from Distribution
  public Scalar randomVariate(RandomGenerator randomGenerator) {
    return ComplexScalar.of( //
        RandomVariate.of(distribution_re, randomGenerator), //
        RandomVariate.of(distribution_im, randomGenerator));
  }
}
