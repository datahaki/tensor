// code by jph
package ch.alpine.tensor.lie;

import java.util.Collections;

import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.alg.Array;
import ch.alpine.tensor.mat.HilbertMatrix;

/** definition of Cauchy tensor taken from [qi, luo 2017] p.248
 * 
 * the {@link HilbertMatrix} is a Cauchy tensor */
public enum CauchyTensor {
  ;
  /** @param vector generator
   * @param rank
   * @return
   * @throws Exception if input is not a vector */
  public static Tensor of(Tensor vector, int rank) {
    return Array.of(list -> list.stream().map(vector::Get).reduce(Scalar::add).orElseThrow().reciprocal(), //
        Collections.nCopies(rank, vector.length()));
  }
}
