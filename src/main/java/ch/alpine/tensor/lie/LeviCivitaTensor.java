// code by jph
package ch.alpine.tensor.lie;

import java.util.Collections;

import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.alg.Array;

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
 * <a href="https://reference.wolfram.com/language/ref/LeviCivitaTensor.html">LeviCivitaTensor</a>
 * 
 * @see HodgeDual */
public enum LeviCivitaTensor {
  ;
  // number of elements are indicated
  private static final Tensor[] CACHE = { //
      build(0), // 1
      build(1), // 1
      build(2), // 4
      build(3), // 27
      build(4) }; // 256

  /** @param d non-negative
   * @return tensor of rank d and dimensions d x ... x d
   * @throws Exception if d is negative */
  public static Tensor of(int d) {
    return d < CACHE.length //
        ? CACHE[d].copy()
        : build(d);
  }

  // helper function
  private static Tensor build(int d) {
    return Array.of(list -> Signature.of(Tensors.vector(list)), Collections.nCopies(d, d));
  }
}
