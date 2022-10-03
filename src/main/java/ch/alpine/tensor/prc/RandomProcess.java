// code by jph
package ch.alpine.tensor.prc;

import java.util.Random;

import ch.alpine.tensor.Scalar;

public interface RandomProcess {
  TimeSeries spawn();

  Scalar eval(TimeSeries timeSeries, Random random, Scalar x);
}
