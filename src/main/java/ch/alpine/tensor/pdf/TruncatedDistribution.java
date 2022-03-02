// code by jph
package ch.alpine.tensor.pdf;

import java.io.Serializable;
import java.util.Objects;
import java.util.Random;
import java.util.stream.Stream;

import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Scalars;
import ch.alpine.tensor.ext.PackageTestAccess;
import ch.alpine.tensor.itp.LinearInterpolation;
import ch.alpine.tensor.pdf.c.DiracDistribution;
import ch.alpine.tensor.pdf.c.UniformDistribution;
import ch.alpine.tensor.sca.Clip;
import ch.alpine.tensor.sca.Clips;

/** inspired by
 * <a href="https://reference.wolfram.com/language/ref/TruncatedDistribution.html">TruncatedDistribution</a> */
public class TruncatedDistribution implements Distribution, PDF, CDF, InverseCDF, RandomVariateInterface, Serializable {
  /** maximum number of attempts to produce a random variate before an exception is thrown */
  private static final int MAX_ITERATIONS = 100;

  /** @param distribution non-null
   * @param clip non-null
   * @return
   * @throws Exception if either parameter is null
   * @throws if CDF of given distribution is not monotonous over given interval */
  public static Distribution of(Distribution distribution, Clip clip) {
    RandomVariateInterface rvin = (RandomVariateInterface) Objects.requireNonNull(distribution);
    if (Scalars.isZero(clip.width()))
      return new DiracDistribution(clip.min());
    if (distribution instanceof CDF cdf) {
      Clip clip_cdf = Clips.interval(cdf.p_lessThan(clip.min()), cdf.p_lessEquals(clip.max()));
      if (Scalars.isZero(clip_cdf.width()))
        throw new IllegalArgumentException();
      RandomVariateInterface rvi = distribution instanceof InverseCDF inverseCDF //
          ? new RV_I(inverseCDF, clip_cdf)
          : new RV_S(rvin, clip);
      return new TruncatedDistribution(distribution, cdf, rvi, clip, clip_cdf);
    }
    return new RV_S(rvin, clip);
  }
  // ---

  private final PDF pdf;
  private final CDF cdf;
  private final RandomVariateInterface randomVariateInterface;
  private final InverseCDF inverseCDF;
  private final Clip clip;
  private final Clip clip_cdf;

  public TruncatedDistribution( //
      Distribution distribution, CDF cdf, //
      RandomVariateInterface randomVariateInterface, Clip clip, Clip clip_cdf) {
    pdf = PDF.of(distribution);
    this.cdf = cdf;
    inverseCDF = InverseCDF.of(distribution);
    this.randomVariateInterface = randomVariateInterface;
    this.clip = clip;
    this.clip_cdf = clip_cdf;
  }

  @Override // from PDF
  public Scalar at(Scalar x) {
    Scalar p = pdf.at(x);
    return clip.isInside(x) //
        ? p.divide(clip_cdf.width())
        : p.zero();
  }

  @Override // from CDF
  public Scalar p_lessThan(Scalar x) {
    return clip_cdf.rescale(cdf.p_lessThan(x));
  }

  @Override // from CDF
  public Scalar p_lessEquals(Scalar x) {
    return clip_cdf.rescale(cdf.p_lessEquals(x));
  }

  @Override // from RandomVariateInterface
  public Scalar randomVariate(Random random) {
    return randomVariateInterface.randomVariate(random);
  }

  @Override // from InverseCDF
  public Scalar quantile(Scalar p) {
    return inverseCDF.quantile(LinearInterpolation.of(clip_cdf).At(p));
  }

  @PackageTestAccess
  Clip clip_cdf() {
    return clip_cdf;
  }

  private static final class RV_I implements RandomVariateInterface, Serializable {
    private final InverseCDF inverseCDF;
    private final Distribution distribution;

    private RV_I(InverseCDF inverseCDF, Clip clip_cdf) {
      this.inverseCDF = inverseCDF;
      distribution = UniformDistribution.of(clip_cdf);
    }

    @Override // from RandomVariateInterface
    public Scalar randomVariate(Random random) {
      return inverseCDF.quantile(RandomVariate.of(distribution, random));
    }
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
