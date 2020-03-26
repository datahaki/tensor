// code by jph
package ch.ethz.idsc.tensor.lie;

import java.util.Collections;

import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Array;

/** Quote: "The elements of LeviCivitaTensor[d] are 0, -1, +1, and can be obtained
 * by applying {@link Signature} to their indices."
 * 
 * <pre>
 * Mathematica::LeviCivitaTensor[0] throws an Exception
 * Tensor-lib.::LeviCivitaTensor[0] == 1
 * </pre>
 * 
 * For a matrix of dimensions n x n, the relation holds
 * <pre>
 * Fold.of((t, v) -> v.dot(t), LeviCivitaTensor.of(n), matrix) == Det.of(matrix)
 * </pre>
 * 
 * <p>inspired by
 * <a href="https://reference.wolfram.com/language/ref/LeviCivitaTensor.html">LeviCivitaTensor</a> */
public enum LeviCivitaTensor {
  ;
  /** @param rank non-negative
   * @return tensor of given rank
   * @throws Exception if rank is negative */
  public static Tensor of(int rank) {
    return Array.of(list -> Signature.of(Tensors.vector(list)), Collections.nCopies(rank, rank));
  }
}
