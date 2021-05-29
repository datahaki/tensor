// code by jph
package ch.alpine.tensor.lie;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.IntStream;

import ch.alpine.tensor.RationalScalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.TensorRuntimeException;
import ch.alpine.tensor.alg.Dimensions;
import ch.alpine.tensor.alg.Range;
import ch.alpine.tensor.alg.TensorRank;
import ch.alpine.tensor.alg.Transpose;
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
    Dimensions dimensions = new Dimensions(tensor);
    if (dimensions.isArray()) {
      int rank = dimensions.list().size();
      switch (rank) {
      case 0: // scalar
        return tensor;
      case 1: // vector
        return tensor.copy();
      case 2: // matrix
        return _01(tensor);
      default:
        return Permutations.stream(Range.of(0, rank)) //
            .map(permutation -> Transpose.of(tensor, IntStream.range(0, rank) //
                .map(index -> permutation.Get(index).number().intValue()) //
                .toArray()))
            .reduce(Tensor::add).orElseThrow() //
            .divide(Factorial.of(rank));
      }
    }
    throw TensorRuntimeException.of(tensor);
  }

  /** @param tensor
   * @return given tensor symmetrized in first two dimensions */
  /* package */ static Tensor _01(Tensor tensor) {
    AtomicInteger atomicInteger = new AtomicInteger();
    return Tensor.of(tensor.stream() //
        .map(row -> row.add(tensor.get(Tensor.ALL, atomicInteger.getAndIncrement())).multiply(RationalScalar.HALF)));
  }
}
