// code by jph
package ch.alpine.tensor.prc;

import java.io.Serializable;
import java.util.NavigableSet;
import java.util.Objects;
import java.util.Random;

import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Scalars;
import ch.alpine.tensor.io.MathematicaFormat;
import ch.alpine.tensor.pdf.Distribution;
import ch.alpine.tensor.pdf.RandomVariate;
import ch.alpine.tensor.pdf.c.NormalDistribution;
import ch.alpine.tensor.sca.Clip;
import ch.alpine.tensor.sca.Clips;
import ch.alpine.tensor.sca.InvertUnlessZero;
import ch.alpine.tensor.sca.Sign;
import ch.alpine.tensor.sca.pow.Sqrt;
import ch.alpine.tensor.tmp.ResamplingMethods;
import ch.alpine.tensor.tmp.TimeSeries;

/** <p>inspired by
 * <a href="https://reference.wolfram.com/language/ref/WienerProcess.html">WienerProcess</a> */
public class WienerProcess implements RandomProcess, Serializable {
  /** @param mu drift
   * @param sigma volatility positive */
  public static RandomProcess of(Scalar mu, Scalar sigma) {
    return new WienerProcess( //
        Objects.requireNonNull(mu), //
        Sign.requirePositiveOrZero(sigma));
  }

  /** @param mu drift
   * @param sigma volatility positive
   * @return */
  public static RandomProcess of(Number mu, Number sigma) {
    return of(RealScalar.of(mu), RealScalar.of(sigma));
  }

  // ---
  private final Scalar mu;
  private final Scalar sigma;
  private final Scalar t_zero;
  private final Scalar v_zero;

  private WienerProcess(Scalar mu, Scalar sigma) {
    this.mu = mu;
    this.sigma = sigma;
    Scalar ratio = mu.divide(sigma);
    t_zero = InvertUnlessZero.FUNCTION.apply(ratio.multiply(ratio)).zero();
    v_zero = InvertUnlessZero.FUNCTION.apply(ratio.divide(sigma)).zero();
  }

  @Override // from RandomProcess
  public TimeSeries spawn() {
    TimeSeries timeSeries = TimeSeries.empty(ResamplingMethods.LINEAR_INTERPOLATION);
    timeSeries.insert(t_zero, v_zero);
    return timeSeries;
  }

  @Override // from RandomProcess
  public Scalar eval(TimeSeries timeSeries, Random random, Scalar x) {
    Sign.requirePositiveOrZero(x);
    Clip clip = timeSeries.support();
    Distribution distribution = null;
    if (clip.isInside(x)) {
      NavigableSet<Scalar> navigableSet = timeSeries.keySet(clip, true);
      Clip interval = Clips.interval( //
          navigableSet.floor(x), //
          navigableSet.ceiling(x));
      if (Scalars.isZero(interval.width()))
        return (Scalar) timeSeries.eval(x);
      distribution = BrownianBridgeProcess.of(sigma).at( //
          interval, //
          (Scalar) timeSeries.eval(interval.min()), //
          (Scalar) timeSeries.eval(interval.max()), x);
    } else {
      Scalar t = x.subtract(clip.max());
      distribution = NormalDistribution.of(mu.multiply(t), Sqrt.FUNCTION.apply(t).multiply(sigma));
    }
    Scalar value = RandomVariate.of(distribution, random);
    timeSeries.insert(x, value);
    return value;
  }

  @Override
  public String toString() {
    return MathematicaFormat.concise("WienerProcess", mu, sigma);
  }
}
