// code by jph
package ch.ethz.idsc.tensor.alg;

import java.util.stream.Stream;

import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;

/** inspired by
 * <a href="https://reference.wolfram.com/language/ref/RotateLeft.html">RotateLeft</a>
 * 
 * @see RotateRight */
public enum RotateLeft {
  ;
  /** <pre>
   * RotateLeft[{a, b, c, d, e}, 2] == {c, d, e, a, b}
   * RotateLeft[{}, n] == {}
   * </pre>
   * 
   * @param tensor
   * @param n any integer
   * @return
   * @throws Exception if given tensor is a {@link Scalar} */
  public static Tensor of(Tensor tensor, int n) {
    if (Tensors.isEmpty(tensor))
      return Tensors.empty();
    int index = Math.floorMod(n, tensor.length());
    return Tensor.of(Stream.concat( //
        tensor.stream().skip(index), //
        tensor.stream().limit(index)).map(Tensor::copy));
  }
}
