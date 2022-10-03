// code by jph
package ch.alpine.tensor.prc;

import java.util.Random;

import ch.alpine.tensor.Scalar;

public interface RandomProcess {
  /** @return a new instance of {@link TimeSeries} with a {@link ResamplingMethod}
   * suitable for this random process */
  TimeSeries spawn();

  /** @param timeSeries
   * @param random
   * @param x
   * @return value of random process at x */
  Scalar eval(TimeSeries timeSeries, Random random, Scalar x);
}
