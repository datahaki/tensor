// code by jph
package ch.alpine.tensor.prc;

import java.io.Serializable;
import java.util.NavigableSet;
import java.util.Objects;
import java.util.random.RandomGenerator;

import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Scalars;
import ch.alpine.tensor.Unprotect;
import ch.alpine.tensor.io.MathematicaFormat;
import ch.alpine.tensor.pdf.Distribution;
import ch.alpine.tensor.pdf.RandomVariate;
import ch.alpine.tensor.pdf.c.NormalDistribution;
import ch.alpine.tensor.qty.Quantity;
import ch.alpine.tensor.sca.Clip;
import ch.alpine.tensor.sca.Clips;
import ch.alpine.tensor.sca.N;
import ch.alpine.tensor.sca.Sign;
import ch.alpine.tensor.sca.pow.Sqrt;
import ch.alpine.tensor.tmp.ResamplingMethod;
import ch.alpine.tensor.tmp.TimeSeries;

/** <p>inspired by
 * <a href="https://reference.wolfram.com/language/ref/WienerProcess.html">WienerProcess</a> */
public class WienerProcess implements RandomProcess, Serializable {
  private static final RandomProcess STANDARD = of(0, 1);

  /** @param mu drift
   * @param sigma volatility non-negative
   * @param t_zero start time
   * @param v_zero start value
   * @return */
  public static RandomProcess of(Scalar mu, Scalar sigma, Scalar t_zero, Scalar v_zero) {
    return new WienerProcess( //
        Objects.requireNonNull(mu), //
        Sign.requirePositiveOrZero(sigma), //
        t_zero, v_zero);
  }

  /** input parameters may be of type {@link Quantity}, for instance
   * <pre>
   * WienerProcess[mu = 1[m*s^-1], sigma=2[m*s^-1/2]]
   * </pre>
   * 
   * @param mu drift
   * @param sigma volatility non-negative */
  public static RandomProcess of(Scalar mu, Scalar sigma) {
    Scalar ratio = mu.divide(N.DOUBLE.apply(sigma)); // switch to numeric for division
    return of(mu, sigma, //
        Unprotect.zero_negateUnit(ratio.multiply(ratio)), //
        Unprotect.zero_negateUnit(ratio.divide(sigma)));
  }

  /** @param mu drift
   * @param sigma volatility non-negative
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
  private final BrownianBridgeProcess brownianBridgeProcess;

  private WienerProcess(Scalar mu, Scalar sigma, Scalar t_zero, Scalar v_zero) {
    this.mu = mu;
    this.sigma = sigma;
    this.t_zero = t_zero;
    this.v_zero = v_zero;
    brownianBridgeProcess = BrownianBridgeProcess.of(sigma);
  }

  @Override // from RandomProcess
  public TimeSeries spawn() {
    TimeSeries timeSeries = TimeSeries.empty(ResamplingMethod.LINEAR_INTERPOLATION);
    timeSeries.insert(t_zero, v_zero);
    return timeSeries;
  }

  @Override // from RandomProcess
  public Scalar evaluate(TimeSeries timeSeries, RandomGenerator randomGenerator, Scalar x) {
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
      Scalar max = clip.max();
      Scalar t = Sign.requirePositive(x.subtract(max));
      distribution = NormalDistribution.of( //
          mu.multiply(t).add(timeSeries.evaluate(max)), //
          Sqrt.FUNCTION.apply(t).multiply(sigma));
    }
    Scalar value = RandomVariate.of(distribution, randomGenerator);
    timeSeries.insert(x, value);
    return value;
  }

  @Override // from Object
  public String toString() {
    return MathematicaFormat.concise("WienerProcess", mu, sigma);
  }
}
