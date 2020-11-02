// code by jph
package ch.ethz.idsc.tensor.itp;

import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.sca.Round;

/** entries of index are rounded to nearest integers */
public enum NearestInterpolation {
  ;
  /** @param tensor
   * @return
   * @throws Exception if tensor == null */
  public static Interpolation of(Tensor tensor) {
    return new MappedInterpolation(tensor, Round.FUNCTION);
  }
}
