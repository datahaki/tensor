// code by jph
package ch.alpine.tensor.lie;

import java.util.stream.IntStream;

import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.alg.Range;
import ch.alpine.tensor.io.Primitives;
import ch.alpine.tensor.spa.SparseArray;

/** Quote: "The elements of LeviCivitaTensor[d] are 0, -1, +1, and can be obtained
 * by applying {@link Signature} to their indices."
 * 
 * <pre>
 * Mathematica::LeviCivitaTensor[0] throws an Exception
 * Tensor-lib.::LeviCivitaTensor[0] == 1
 * </pre>
 * 
 * LeviCivitaTensor[0] == 1 is warranted by Signature[{}] == 1.
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
  /** @param d non-negative
   * @return sparse tensor of rank d and dimensions d x ... x d
   * @throws Exception if d is negative */
  public static Tensor of(int d) {
    if (d == 0)
      return Signature.of(Tensors.empty());
    Tensor tensor = SparseArray.of(RealScalar.ZERO, IntStream.generate(() -> d).limit(d).toArray());
    Permutations.stream(Range.of(0, d)) //
        .forEach(perm -> tensor.set(Signature.of(perm), Primitives.toIntArray(perm)));
    return tensor;
  }
}
