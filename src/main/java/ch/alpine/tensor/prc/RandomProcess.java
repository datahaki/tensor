// code by jph
package ch.alpine.tensor.prc;

import ch.alpine.tensor.Scalar;

public interface RandomProcess {
  Scalar eval(TimeSeries timeSeries, Scalar x);
}
