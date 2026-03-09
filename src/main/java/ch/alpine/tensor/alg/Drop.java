// code by jph
package ch.alpine.tensor.alg;

import java.util.stream.Stream;

import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.ext.Integers;

/** inspired by
 * <a href="https://reference.wolfram.com/language/ref/Drop.html">Drop</a> */
public enum Drop {
  ;
  /** gives tensor with its first n elements dropped
   * 
   * @param tensor
   * @param n
   * @return
   * @throws Exception if given tensor has less than n entries */
  public static Tensor head(Tensor tensor, int n) {
    return tensor.extract(n, tensor.length());
  }

  /** gives tensor with its last n elements dropped
   * 
   * @param tensor
   * @param n
   * @return
   * @throws Exception if given tensor has less than n entries */
  public static Tensor tail(Tensor tensor, int n) {
    return tensor.extract(0, tensor.length() - n);
  }

  /** corresponds to Mathematica::Delete
   * 
   * @param tensor
   * @param index
   * @return */
  public static Tensor index(Tensor tensor, int index) {
    Integers.requireLessThan(index, tensor.length());
    return Tensor.of(Stream.concat( //
        tensor.stream().limit(index), //
        tensor.stream().skip(index + 1)) //
        .map(Tensor::copy));
  }
}
