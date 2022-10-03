// code by jph
package ch.alpine.tensor.prc;

import java.security.SecureRandom;
import java.util.Objects;
import java.util.Random;

import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.api.ScalarUnaryOperator;

/** <p>inspired by
 * <a href="https://reference.wolfram.com/language/ref/RandomFunction.html">RandomFunction</a> */
public class RandomFunction implements ScalarUnaryOperator {
  private static final Random RANDOM = new SecureRandom();

  public static RandomFunction of(RandomProcess randomProcess) {
    return new RandomFunction(Objects.requireNonNull(randomProcess));
  }

  // ---
  private final TimeSeries timeSeries = TimeSeries.empty();
  private final RandomProcess randomProcess;

  private RandomFunction(RandomProcess randomProcess) {
    this.randomProcess = randomProcess;
  }

  @Override
  public Scalar apply(Scalar t) {
    return with(RANDOM, t);
  }

  public Scalar with(Random random, Scalar t) {
    return randomProcess.eval(timeSeries, random, t);
  }

  public Tensor path() {
    return timeSeries.path();
  }
}
