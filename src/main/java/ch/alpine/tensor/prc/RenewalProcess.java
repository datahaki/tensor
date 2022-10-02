// code by jph
package ch.alpine.tensor.prc;

import java.io.Serializable;

import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.io.MathematicaFormat;

class RenewalProcess implements RandomProcess, Serializable {
  @Override
  public Scalar eval(TimeSeries timeSeries, Scalar x) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public String toString() {
    return MathematicaFormat.concise("RenewalProcess");
  }
}
