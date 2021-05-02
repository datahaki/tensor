// code by jph
package ch.alpine.tensor.lie;

import java.util.Collections;

import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.TensorRuntimeException;
import ch.alpine.tensor.alg.Array;

/** definition of Hankel tensor taken from [Qi, Luo 2017] p.230 */
public enum HankelTensor {
  ;
  /** @param vector
   * @param rank
   * @return
   * @throws Exception if input is not a vector */
  public static Tensor of(Tensor vector, int rank) {
    int last = vector.length() - 1;
    if (last % rank != 0)
      throw TensorRuntimeException.of(vector);
    return Array.of(list -> vector.Get(list.stream().reduce(Integer::sum).get()), //
        Collections.nCopies(rank, last / rank + 1));
  }
}
