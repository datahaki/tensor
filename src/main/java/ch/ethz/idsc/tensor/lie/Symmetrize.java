// code by jph
package ch.ethz.idsc.tensor.lie;

import java.util.stream.IntStream;

import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.TensorRuntimeException;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Dimensions;
import ch.ethz.idsc.tensor.alg.Range;
import ch.ethz.idsc.tensor.alg.TensorRank;
import ch.ethz.idsc.tensor.alg.Transpose;
import ch.ethz.idsc.tensor.sca.Factorial;

/** inspired by
 * <a href="https://reference.wolfram.com/language/ref/Symmetrize.html">Symmetrize</a>
 * 
 * @see Permutations
 * @see TensorRank
 * @see Transpose */
public enum Symmetrize {
  ;
  private static final Scalar TWO = RealScalar.of(2);

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
        return Tensors.vector(i -> tensor.get(Tensor.ALL, i).add(tensor.get(i)).divide(TWO), tensor.length());
      default:
        return Permutations.stream(Range.of(0, rank)) //
            .map(permutation -> Transpose.of(tensor, IntStream.range(0, rank) //
                .mapToObj(index -> permutation.Get(index).number()) //
                .toArray(Integer[]::new)))
            .reduce(Tensor::add).get() //
            .divide(Factorial.of(rank));
      }
    }
    throw TensorRuntimeException.of(tensor);
  }
}
