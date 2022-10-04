// code by jph
package ch.alpine.tensor.prc;

import java.io.Serializable;
import java.security.SecureRandom;
import java.util.Objects;
import java.util.Random;

import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.tmp.TimeSeries;

/** <p>inspired by
 * <a href="https://reference.wolfram.com/language/ref/RandomFunction.html">RandomFunction</a> */
public class RandomFunction implements Serializable {
  private static final Random RANDOM = new SecureRandom();

  public static RandomFunction of(RandomProcess randomProcess) {
    return new RandomFunction(Objects.requireNonNull(randomProcess));
  }

  // ---
  private final RandomProcess randomProcess;
  private final TimeSeries timeSeries;

  private RandomFunction(RandomProcess randomProcess) {
    this.randomProcess = randomProcess;
    this.timeSeries = randomProcess.spawn();
  }

  /** @param time
   * @return */
  public Scalar eval(Scalar time) {
    return eval(RANDOM, time);
  }

  /** @param random
   * @param time
   * @return */
  public Scalar eval(Random random, Scalar time) {
    return randomProcess.eval(timeSeries, random, time);
  }

  /** @return matrix with dimensions n x 2 */
  public Tensor path() {
    return timeSeries.path();
  }

  /** @return unmodifiable view on the time series underlying this random function */
  public TimeSeries timeSeries() {
    return timeSeries.unmodifiable();
  }
}
