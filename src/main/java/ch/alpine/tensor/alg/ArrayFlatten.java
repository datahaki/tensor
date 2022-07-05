// code by jph
package ch.alpine.tensor.alg;

import java.util.Arrays;

import ch.alpine.tensor.Tensor;

/** inspired by
 * <a href="https://reference.wolfram.com/language/ref/ArrayFlatten.html">ArrayFlatten</a>
 * 
 * @see Join */
public enum ArrayFlatten {
  ;
  /** Example:
   * For matrices A, B, C, D with appropriate dimensions
   * <pre>
   * ArrayFlatten[{{A, B}, {C, D}}]
   * </pre>
   * gives the matrix consisting of the blocks
   * <pre>
   * A B
   * C D
   * </pre>
   * 
   * @param tensors
   * @return */
  public static Tensor of(Tensor[][] tensors) {
    return Tensor.of(Arrays.stream(tensors) //
        .map(tensor -> Join.of(1, tensor)) //
        .flatMap(Tensor::stream));
  }
}
