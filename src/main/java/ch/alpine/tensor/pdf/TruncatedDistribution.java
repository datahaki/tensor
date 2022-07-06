// code by jph
package ch.alpine.tensor.pdf;

import java.io.Serializable;
import java.util.Objects;
import java.util.Random;
import java.util.stream.Stream;

import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Scalars;
import ch.alpine.tensor.ext.PackageTestAccess;
import ch.alpine.tensor.io.MathematicaFormat;
import ch.alpine.tensor.itp.LinearInterpolation;
import ch.alpine.tensor.pdf.c.DiracDeltaDistribution;
import ch.alpine.tensor.pdf.c.UniformDistribution;
import ch.alpine.tensor.sca.Clip;
import ch.alpine.tensor.sca.Clips;

/** inspired by
 * <a href="https://reference.wolfram.com/language/ref/TruncatedDistribution.html">TruncatedDistribution</a> */
public class TruncatedDistribution implements Distribution, //
    PDF, CDF, InverseCDF, RandomVariateInterface, Serializable {
  /** maximum number of attempts to produce a random variate before an exception is thrown */
  private static final int MAX_ITERATIONS = 100;

  /** @param distribution non-null
   * @param clip outside of which the distribution is truncated to zero
   * @return
   * @throws Exception if either parameter is null
   * @throws if CDF of given distribution is not monotonous over given interval */
  public static Distribution of(Distribution distribution, Clip clip) {
    RandomVariateInterface rvin = (RandomVariateInterface) Objects.requireNonNull(distribution);
    if (Scalars.isZero(clip.width()))
      return DiracDeltaDistribution.of(clip.min());
    if (distribution instanceof UnivariateDistribution univariateDistribution) {
      Clip clip_cdf = Clips.interval( //
          univariateDistribution.p_lessThan(clip.min()), //
          univariateDistribution.p_lessEquals(clip.max()));
      if (Scalars.isZero(clip_cdf.width()))
        throw new IllegalArgumentException();
      return new TruncatedDistribution(univariateDistribution, clip, clip_cdf);
    }
    return new RV_S(rvin, clip);
  }

  // ---
  private final UnivariateDistribution univariateDistribution;
  private final Clip clip;
  private final Clip clip_cdf;

  public TruncatedDistribution( //
      UnivariateDistribution univariateDistribution, Clip clip, Clip clip_cdf) {
    this.univariateDistribution = univariateDistribution;
    this.clip = clip;
    this.clip_cdf = clip_cdf;
  }

  @Override // from PDF
  public Scalar at(Scalar x) {
    Scalar p = univariateDistribution.at(x);
    return clip.isInside(x) //
        ? p.divide(clip_cdf.width())
        : p.zero();
  }

  @Override // from CDF
  public Scalar p_lessThan(Scalar x) {
    return clip_cdf.rescale(univariateDistribution.p_lessThan(x));
  }

  @Override // from CDF
  public Scalar p_lessEquals(Scalar x) {
    return clip_cdf.rescale(univariateDistribution.p_lessEquals(x));
  }

  @Override // from InverseCDF
  public Scalar quantile(Scalar p) {
    return univariateDistribution.quantile(LinearInterpolation.of(clip_cdf).At(p));
  }

  @Override // from RandomVariateInterface
  public Scalar randomVariate(Random random) {
    return univariateDistribution.quantile(RandomVariate.of(UniformDistribution.of(clip_cdf), random));
  }

  @Override // from Object
  public String toString() {
    return MathematicaFormat.of("TruncatedDistribution", univariateDistribution, clip);
  }

  @PackageTestAccess
  Clip clip_cdf() {
    return clip_cdf;
  }

  private static final class RV_S implements Distribution, RandomVariateInterface, Serializable {
    private final RandomVariateInterface randomVariateInterface;
    private final Clip clip;

    private RV_S(RandomVariateInterface randomVariateInterface, Clip clip) {
      this.randomVariateInterface = randomVariateInterface;
      this.clip = clip;
    }

    @Override // from RandomVariateInterface
    public Scalar randomVariate(Random random) {
      return Stream.generate(() -> randomVariateInterface.randomVariate(random)) //
          .limit(MAX_ITERATIONS) //
          .filter(clip::isInside) //
          .findFirst() //
          .orElseThrow();
    }
  }
}
