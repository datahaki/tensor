// code by jph
package ch.alpine.tensor.lie;

import java.util.concurrent.atomic.AtomicInteger;

import ch.alpine.tensor.RationalScalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.alg.Range;
import ch.alpine.tensor.alg.TensorRank;
import ch.alpine.tensor.alg.Transpose;
import ch.alpine.tensor.ext.PackageTestAccess;
import ch.alpine.tensor.io.Primitives;
import ch.alpine.tensor.sca.Factorial;

/** inspired by
 * <a href="https://reference.wolfram.com/language/ref/Symmetrize.html">Symmetrize</a>
 * 
 * @see Permutations
 * @see TensorRank
 * @see Transpose */
public enum Symmetrize {
  ;
  /** @param tensor of any rank with dimensions [n, n, ..., n]
   * @return symmetric tensor, i.e. invariant under transpose
   * @throws Exception if given tensor does not have regular dimensions */
  public static Tensor of(Tensor tensor) {
    int rank = TensorRank.ofArray(tensor).orElseThrow();
    switch (rank) {
    case 0: // scalar
      return tensor;
    case 1: // vector
      return tensor.copy();
    case 2: // matrix
      return _01(tensor);
    default:
      return Permutations.stream(Range.of(0, rank)) //
          .map(permutation -> Transpose.of(tensor, Primitives.toIntArray(permutation))) //
          .reduce(Tensor::add) //
          .orElseThrow() //
          .divide(Factorial.of(rank));
    }
  }

  /** @param tensor of rank at least 2
   * @return given tensor symmetrized in first two dimensions
   * @see Transpose */
  @PackageTestAccess // EXPERIMENTAL API
  static Tensor _01(Tensor tensor) {
    AtomicInteger atomicInteger = new AtomicInteger();
    return Tensor.of(tensor.stream() //
        .map(row -> row.add(tensor.get(Tensor.ALL, atomicInteger.getAndIncrement())).multiply(RationalScalar.HALF)));
  }
}
