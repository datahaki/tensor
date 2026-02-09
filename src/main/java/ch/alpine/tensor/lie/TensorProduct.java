// code by jph
package ch.alpine.tensor.lie;

import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.alg.TensorRank;

/** Implementation is consistent with Mathematica, in particular
 * <pre>
 * TensorProduct[] == 1
 * TensorProduct[x] == x
 * </pre>
 * 
 * <p>inspired by
 * <a href="https://reference.wolfram.com/language/ref/TensorProduct.html">TensorProduct</a> */
public enum TensorProduct {
  ;
  /** the {@link TensorRank} of the result is the rank of {@code a} plus the rank of {@code b}
   * 
   * @param a
   * @param b
   * @return tensor product of a and b */
  public static Tensor of(Tensor a, Tensor b) {
    return a.maps(b::multiply);
  }

  /** @param tensors
   * @return */
  public static Tensor of(Tensor... tensors) {
    if (tensors.length == 0)
      return RealScalar.ONE;
    if (tensors.length == 1)
      return tensors[0].copy();
    Tensor tensor = tensors[0];
    for (int index = 1; index < tensors.length; ++index)
      tensor = of(tensor, tensors[index]);
    return tensor;
  }
}
