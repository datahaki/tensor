// code by jph
package ch.alpine.tensor.sca;

import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.api.ScalarUnaryOperator;

/** <p>inspired by
 * <a href="https://reference.wolfram.com/language/ref/SawtoothWave.html">SawtoothWave</a> */
public enum SawtoothWave implements ScalarUnaryOperator {
  FUNCTION;

  @Override
  public Scalar apply(Scalar scalar) {
    return scalar.subtract(Floor.FUNCTION.apply(scalar));
  }
}
