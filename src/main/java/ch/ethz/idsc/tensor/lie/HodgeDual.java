// code by jph
package ch.ethz.idsc.tensor.lie;

import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.alg.TensorRank;
import ch.ethz.idsc.tensor.red.Nest;
import ch.ethz.idsc.tensor.red.Total;
import ch.ethz.idsc.tensor.sca.Factorial;

/** <pre>
 * Mathematica::HodgeDual[scalar, 0] is not defined
 * Tensor-lib.::HodgeDual[scalar, 0] == scalar
 * </pre>
 * 
 * <p>inspired by
 * <a href="https://reference.wolfram.com/language/ref/HodgeDual.html">HodgeDual</a>
 * 
 * @see TensorRank
 * @see LeviCivitaTensor */
public enum HodgeDual {
  ;
  /** implementation is consistent with Mathematica for input restricted to the form below
   * 
   * @param tensor of array structure with dimensions d x ... x d
   * @param d
   * @return
   * @throws Exception if tensor is empty, or is not a regular array */
  public static Tensor of(Tensor tensor, int d) {
    int rank = TensorRank.ofArray(tensor).get();
    return Nest.of(Total::of, tensor.pmul(LeviCivitaTensor.of(d)), rank).divide(Factorial.of(rank));
  }

  /** @param tensor of rank at least 1
   * @return
   * @throws Exception if tensor is a scalar */
  public static Tensor of(Tensor tensor) {
    return of(tensor, tensor.length());
  }
}
