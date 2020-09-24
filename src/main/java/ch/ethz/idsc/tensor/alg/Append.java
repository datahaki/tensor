// code by jph
package ch.ethz.idsc.tensor.alg;

import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;

/** inspired by
 * <a href="https://reference.wolfram.com/language/ref/Append.html">Append</a>
 * 
 * @see Join */
public enum Append {
  ;
  /** Example:
   * <pre>
   * Append[{x, y}, z] == {x, y, z}
   * Append[{1, 2}, {3, 4}] == {1, 2, {3, 4}}
   * </pre>
   * 
   * @param tensor
   * @param last
   * @return
   * @throws Exception if given tensor is a Scalar */
  public static Tensor of(Tensor tensor, Tensor last) {
    Tensor result = Tensors.reserve(tensor.length() + 1);
    tensor.stream().forEach(result::append);
    return result.append(last);
  }
}
