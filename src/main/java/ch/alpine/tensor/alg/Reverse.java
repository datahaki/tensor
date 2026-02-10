// code by jph
package ch.alpine.tensor.alg;

import java.util.stream.IntStream;

import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.chq.ScalarQ;

/** inspired by
 * <a href="https://reference.wolfram.com/language/ref/Reverse.html">Reverse</a>
 * 
 * @see Transpose
 * @see Rotate
 * @see RotateRight */
public enum Reverse {
  ;
  /** Reverse[{a, b, c}] == {c, b, a}
   * 
   * Implementation consistent with Mathematica:
   * Reverse of a scalar is not defined
   * Reverse[ 3.14 ] throws an exception
   * 
   * @param tensor
   * @return tensor with entries on first level reversed
   * @throws Exception if tensor is a scalar */
  public static Tensor of(Tensor tensor) {
    ScalarQ.thenThrow(tensor);
    int last = tensor.length() - 1;
    return Tensor.of(IntStream.range(0, tensor.length()) //
        .map(index -> last - index) //
        .mapToObj(tensor::get));
  }

  /** @param tensor
   * @return tensor with entries on all levels reversed */
  public static Tensor all(Tensor tensor) {
    return tensor instanceof Scalar //
        ? tensor
        : of(Tensor.of(tensor.stream().map(Reverse::all)));
  }
}
