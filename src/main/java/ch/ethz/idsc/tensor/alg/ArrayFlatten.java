// code by jph
package ch.ethz.idsc.tensor.alg;

import java.util.stream.Stream;

import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.opt.rn.LagrangeMultiplier;

/** inspired by
 * <a href="https://reference.wolfram.com/language/ref/ArrayFlatten.html">ArrayFlatten</a>
 * 
 * @see Join */
public enum ArrayFlatten {
  ;
  /** @param tensors
   * @return
   * @see LagrangeMultiplier */
  public static Tensor of(Tensor[][] tensors) {
    return Tensor.of(Stream.of(tensors) //
        .map(tensor -> Join.of(1, tensor)) //
        .flatMap(Tensor::stream));
  }
}
