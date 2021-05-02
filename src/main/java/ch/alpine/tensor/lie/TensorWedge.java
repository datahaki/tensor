// code by jph
package ch.alpine.tensor.lie;

import java.util.stream.IntStream;
import java.util.stream.Stream;

import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.alg.Range;
import ch.alpine.tensor.alg.TensorRank;
import ch.alpine.tensor.alg.Transpose;
import ch.alpine.tensor.num.Multinomial;
import ch.alpine.tensor.sca.Factorial;

/** Implementation is consistent with Mathematica, in particular
 * <pre>
 * TensorWedge[] == 1
 * </pre>
 * 
 * <p>inspired by
 * <a href="https://reference.wolfram.com/language/ref/TensorWedge.html">TensorWedge</a>
 * 
 * @see Permutations
 * @see Transpose
 * @see Signature */
public enum TensorWedge {
  ;
  /** the antisymmetrized tensor product
   * 
   * @param tensor of any rank with dimensions [n, n, ..., n]
   * @return alternating tensor
   * @throws Exception if given tensor does not have regular dimensions */
  public static Tensor of(Tensor tensor) {
    Tensor sum = tensor.map(Scalar::zero);
    int rank = TensorRank.of(tensor);
    for (Tensor permutation : Permutations.of(Range.of(0, rank))) {
      int[] sigma = IntStream.range(0, rank) //
          .mapToObj(permutation::Get) //
          .map(Scalar::number) //
          .mapToInt(Number::intValue).toArray();
      Tensor transpose = Transpose.of(tensor, sigma);
      sum = Signature.of(sigma).equals(RealScalar.ONE) //
          ? sum.add(transpose)
          : sum.subtract(transpose);
    }
    return sum.divide(Factorial.of(rank));
  }

  /** @param tensors of any rank with dimensions [n, n, ..., n]
   * @return alternating tensor product of a and b */
  public static Tensor of(Tensor... tensors) {
    Scalar scalar = Multinomial.of(Stream.of(tensors).mapToInt(TensorRank::of).toArray());
    return of(TensorProduct.of(tensors)).multiply(scalar);
  }
}
