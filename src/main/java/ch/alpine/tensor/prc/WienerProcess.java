// code by jph
package ch.alpine.tensor.prc;

import java.io.Serializable;
import java.util.NavigableSet;
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
import ch.alpine.tensor.sca.Sign;
import ch.alpine.tensor.sca.pow.Sqrt;

/** <p>inspired by
 * <a href="https://reference.wolfram.com/language/ref/WienerProcess.html">WienerProcess</a> */
public class WienerProcess implements RandomProcess, Serializable {
  /** @param mu drift
   * @param sigma volatility */
  public static RandomProcess of(Scalar mu, Scalar sigma) {
    mu.add(sigma);
    Sign.requirePositiveOrZero(sigma);
    return new WienerProcess(mu, sigma);
  }

  public static RandomProcess of(Number mu, Number sigma) {
    return of(RealScalar.of(mu), RealScalar.of(sigma));
  }

  // ---
  private final Scalar mu;
  private final Scalar sigma;

  private WienerProcess(Scalar mu, Scalar sigma) {
    this.mu = mu;
    this.sigma = sigma;
  }

  @Override
  public TimeSeries spawn() {
    TimeSeries timeSeries = TimeSeries.empty(ResamplingMethods.INTERPOLATION_1);
    // TODO TENSOR MATH is this general !? quantity?
    timeSeries.insert(RealScalar.ZERO, RealScalar.ZERO);
    return timeSeries;
  }

  @Override
  public Scalar eval(TimeSeries timeSeries, Random random, Scalar x) {
    Sign.requirePositiveOrZero(x);
    Clip clip = timeSeries.support();
    Distribution distribution = null;
    if (clip.isInside(x)) {
      NavigableSet<Scalar> navigableSet = timeSeries.keySet(clip);
      Clip interval = Clips.interval( //
          navigableSet.floor(x), //
          navigableSet.ceiling(x));
      if (Scalars.isZero(interval.width()))
        return (Scalar) timeSeries.eval(x);
      BrownianBridgeProcess brownianBridgeProcess = BrownianBridgeProcess.of(sigma); // TODO TENSOR MATH sigma !?
      distribution = brownianBridgeProcess.at( //
          interval, //
          (Scalar) timeSeries.eval(interval.min()), //
          (Scalar) timeSeries.eval(interval.max()), x);
    } else {
      Scalar t = x.subtract(clip.max());
      distribution = NormalDistribution.of(mu.multiply(t), Sqrt.FUNCTION.apply(t).multiply(sigma));
    }
    Scalar val = RandomVariate.of(distribution, random);
    timeSeries.insert(x, val);
    return val;
  }

  @Override
  public String toString() {
    return MathematicaFormat.concise("WienerProcess", mu, sigma);
  }
}
