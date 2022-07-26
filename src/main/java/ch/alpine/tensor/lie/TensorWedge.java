// code by jph
package ch.alpine.tensor.lie;

import java.util.Arrays;

import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.alg.Range;
import ch.alpine.tensor.alg.TensorRank;
import ch.alpine.tensor.alg.Transpose;
import ch.alpine.tensor.ext.Integers;
import ch.alpine.tensor.io.Primitives;
import ch.alpine.tensor.num.Multinomial;
import ch.alpine.tensor.sca.gam.Factorial;

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
    int rank = TensorRank.ofArray(tensor);
    return Permutations.stream(Range.of(0, rank)) // stream contains at least 1 element
        .map(permutation -> signed(tensor, Primitives.toIntArray(permutation))) //
        .reduce(Tensor::add) //
        .map(sum -> sum.divide(Factorial.of(rank))) //
        .get();
  }

  private static Tensor signed(Tensor tensor, int[] sigma) {
    Tensor transpose = Transpose.of(tensor, sigma);
    return Integers.isEven(Integers.parity(sigma)) ? transpose : transpose.negate();
  }

  /** @param tensors of any rank with dimensions [n, n, ..., n]
   * @return alternating tensor product of a and b */
  public static Tensor of(Tensor... tensors) {
    Scalar scalar = Multinomial.of(Arrays.stream(tensors).mapToInt(TensorRank::of).toArray());
    return of(TensorProduct.of(tensors)).multiply(scalar);
  }
}
