// code by jph
package ch.alpine.tensor.prc;

import java.io.Serializable;
import java.security.SecureRandom;
import java.util.Objects;
import java.util.Random;

import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.tmp.TimeSeries;

/** a random function is a realization of a random process.
 * 
 * <p>The value of the random function at location x is "discovered" once,
 * no later that the first query via {@link #evaluate(Scalar)}, or
 * {@link #evaluate(Random, Scalar)}.
 * After that, the function evaluates to the same value at x of course.
 * 
 * <p>inspired by
 * <a href="https://reference.wolfram.com/language/ref/RandomFunction.html">RandomFunction</a> */
public class RandomFunction implements Serializable {
  private static final Random RANDOM = new SecureRandom();

  /** @param randomProcess non null
   * @return */
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
  public Scalar evaluate(Scalar time) {
    return evaluate(RANDOM, time);
  }

  /** @param random
   * @param time
   * @return */
  public Scalar evaluate(Random random, Scalar time) {
    return randomProcess.evaluate(timeSeries, random, time);
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
