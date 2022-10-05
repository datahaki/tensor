// code by jph
package ch.alpine.tensor.prc;

import java.io.Serializable;
import java.util.Objects;
import java.util.Random;

import ch.alpine.tensor.IntegerQ;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.io.MathematicaFormat;
import ch.alpine.tensor.pdf.Distribution;
import ch.alpine.tensor.pdf.RandomVariate;
import ch.alpine.tensor.sca.Sign;
import ch.alpine.tensor.tmp.ResamplingMethods;
import ch.alpine.tensor.tmp.TimeSeries;

/** Remark: the name of the class is not final
 * 
 * 
 * @see WhiteNoiseProcess
 * @see BernoulliProcess */
public class DiscreteTimeIidProcess implements RandomProcess, Serializable {
  /** @param distribution non-null
   * @return */
  public static RandomProcess of(Distribution distribution) {
    return new DiscreteTimeIidProcess(Objects.requireNonNull(distribution));
  }

  // ---
  private final Distribution distribution;

  /** @param distribution */
  private DiscreteTimeIidProcess(Distribution distribution) {
    this.distribution = distribution;
  }

  @Override // from RandomProcess
  public final TimeSeries spawn() {
    return TimeSeries.empty(ResamplingMethods.NONE);
  }

  @Override // from RandomProcess
  public final Scalar evaluate(TimeSeries timeSeries, Random random, Scalar x) {
    IntegerQ.require(x);
    Sign.requirePositiveOrZero(x);
    if (timeSeries.isEmpty())
      timeSeries.insert(RealScalar.ZERO, RandomVariate.of(distribution, random));
    while (timeSeries.domain().isOutside(x)) {
      Scalar ofs = timeSeries.domain().max().add(RealScalar.ONE);
      timeSeries.insert(ofs, RandomVariate.of(distribution, random));
    }
    return (Scalar) timeSeries.evaluate(x);
  }

  @Override // from Object
  public String toString() {
    return MathematicaFormat.concise("DiscreteTimeIidProcess", distribution);
  }
}
