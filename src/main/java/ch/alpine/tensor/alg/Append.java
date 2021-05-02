// code by jph
package ch.alpine.tensor.alg;

import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;

/** Append implements the pattern
 * <pre>
 * Append[tensor, last] = tensor.copy().append(last)
 * </pre>
 * in a faster way.
 * 
 * <p>inspired by
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
   * @param tensor not a scalar
   * @param last
   * @return tensor with one additional last element
   * @throws Exception if given tensor is a scalar, or either input parameter is null */
  public static Tensor of(Tensor tensor, Tensor last) {
    Tensor result = Tensors.reserve(tensor.length() + 1);
    tensor.stream().forEach(result::append);
    return result.append(last);
  }
}
