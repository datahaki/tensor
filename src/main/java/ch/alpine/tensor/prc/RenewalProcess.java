// code by jph
package ch.alpine.tensor.prc;

import java.io.Serializable;
import java.util.Objects;
import java.util.Random;

import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Scalars;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.io.MathematicaFormat;
import ch.alpine.tensor.pdf.Distribution;
import ch.alpine.tensor.pdf.RandomVariate;
import ch.alpine.tensor.sca.Sign;
import ch.alpine.tensor.tmp.ResamplingMethods;
import ch.alpine.tensor.tmp.TimeSeries;

/** Quote from Mathematica:
 * <blockquote>
 * The state x(t) is the number of events in the interval 0 to t and x(0)==0.
 * </blockquote>
 * 
 * <p>inspired by
 * <a href="https://reference.wolfram.com/language/ref/RenewalProcess.html">RenewalProcess</a>
 * 
 * @see BinomialProcess */
public class RenewalProcess implements RandomProcess, Serializable {
  /** @param distribution
   * @return */
  public static RandomProcess of(Distribution distribution) {
    return new RenewalProcess(Objects.requireNonNull(distribution));
  }

  // ---
  private final Distribution distribution;

  private RenewalProcess(Distribution distribution) {
    this.distribution = distribution;
  }

  @Override // from RandomProcess
  public TimeSeries spawn() {
    TimeSeries timeSeries = TimeSeries.empty(ResamplingMethods.HOLD_VALUE_FROM_LEFT);
    timeSeries.insert(RandomVariate.of(distribution).zero(), RealScalar.ZERO);
    return timeSeries;
  }

  @Override // from RandomProcess
  public Scalar evaluate(TimeSeries timeSeries, Random random, Scalar x) {
    Sign.requirePositiveOrZero(x);
    while (Scalars.lessThan(timeSeries.domain().max(), x)) {
      Scalar dt = Sign.requirePositive(RandomVariate.of(distribution, random));
      Scalar max = timeSeries.domain().max();
      Tensor val = timeSeries.evaluate(max);
      timeSeries.insert(max.add(dt), val.add(RealScalar.ONE));
    }
    return (Scalar) timeSeries.evaluate(x);
  }

  @Override // from Object
  public String toString() {
    return MathematicaFormat.concise("RenewalProcess", distribution);
  }
}
