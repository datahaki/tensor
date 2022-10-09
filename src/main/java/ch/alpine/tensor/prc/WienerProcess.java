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
import ch.alpine.tensor.qty.Quantity;
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
  private static final RandomProcess STANDARD = of(0, 1);

  /** input parameters may be of type {@link Quantity}, for instance
   * <pre>
   * WienerProcess[mu = 1[m*s^-1], sigma=2[m*s^-1/2]]
   * </pre>
   * 
   * @param mu drift
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

  /** @return wiener process with drift 0, and volatility 1 */
  public static RandomProcess standard() {
    return STANDARD;
  }

  // ---
  private final Scalar mu;
  private final Scalar sigma;
  private final Scalar t_zero;
  private final Scalar v_zero;
  private BrownianBridgeProcess brownianBridgeProcess;

  private WienerProcess(Scalar mu, Scalar sigma) {
    this.mu = mu;
    this.sigma = sigma;
    Scalar ratio = mu.divide(sigma);
    t_zero = InvertUnlessZero.FUNCTION.apply(ratio.multiply(ratio)).zero();
    v_zero = InvertUnlessZero.FUNCTION.apply(ratio.divide(sigma)).zero();
    brownianBridgeProcess = BrownianBridgeProcess.of(sigma);
  }

  @Override // from RandomProcess
  public TimeSeries spawn() {
    TimeSeries timeSeries = TimeSeries.empty(ResamplingMethods.LINEAR_INTERPOLATION);
    timeSeries.insert(t_zero, v_zero);
    return timeSeries;
  }

  @Override // from RandomProcess
  public Scalar evaluate(TimeSeries timeSeries, Random random, Scalar x) {
    Sign.requirePositiveOrZero(x);
    Clip clip = timeSeries.domain();
    Distribution distribution = null;
    if (clip.isInside(x)) {
      NavigableSet<Scalar> navigableSet = timeSeries.keySet(clip, true);
      Clip interval = Clips.interval( //
          navigableSet.floor(x), //
          navigableSet.ceiling(x));
      if (Scalars.isZero(interval.width()))
        return (Scalar) timeSeries.evaluate(x);
      distribution = brownianBridgeProcess.at( //
          interval, //
          (Scalar) timeSeries.evaluate(interval.min()), //
          (Scalar) timeSeries.evaluate(interval.max()), x);
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
