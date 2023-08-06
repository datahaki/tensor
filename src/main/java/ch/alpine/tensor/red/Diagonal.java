// code by jph
package ch.alpine.tensor.red;

import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.Unprotect;

/** {@link #of(Tensor)} requies the input to have array structure up to level 2
 * 
 * {@link #mathematica(Tensor)} is consistent with Mathematica.
 * 
 * <pre>
 * Diagonal[{{{0}, 1}, {2, 3}, {4, 5}}] == {{0}, 3}
 * Diagonal[{1, 2, 3, 4}] => {}
 * </pre>
 * 
 * For Scalar input, the result is undefined
 * <pre>
 * Diagonal[3] => Exception
 * </pre>
 * 
 * Mathematica additionally defines
 * <pre>
 * Diagonal[{{1}, {2}, {4, 5, 6}}] == {1}
 * Diagonal[{{1}, {2, 3}, {4, 5, 6}}] == {1, 3, 6}
 * </pre>
 * 
 * <p>inspired by
 * <a href="https://reference.wolfram.com/language/ref/Diagonal.html">Diagonal</a> */
public enum Diagonal {
  ;
  /** @param tensor
   * @return vector of entries on diagonal of given tensor
   * @throws Exception if tensor is a scalar
   * @throws Exception if tensors on level 1 have different {@link Tensor#length()} */
  public static Tensor of(Tensor tensor) {
    int cols = Unprotect.dimension1(tensor);
    return Tensors.vector(i -> tensor.get(i, i), Math.min(tensor.length(), cols));
  }

  /** @param tensor
   * @param k
   * @return */
  public static Tensor of(Tensor tensor, int k) {
    int cols = Unprotect.dimension1(tensor);
    return 0 <= k //
        ? Tensors.vector(i -> tensor.get(i, i + k), Math.min(tensor.length(), cols - k))
        : Tensors.vector(i -> tensor.get(i - k, i), Math.min(tensor.length() + k, cols));
  }

  /** @param tensor
   * @return */
  public static Tensor mathematica(Tensor tensor) {
    Tensor vector = Tensors.empty();
    int count = 0;
    for (Tensor row : tensor) {
      if (count < row.length())
        vector.append(row.get(count));
      else
        break;
      ++count;
    }
    return vector;
  }
}
