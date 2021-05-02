// code by jph
package ch.alpine.tensor.itp;

import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.sca.Round;

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
