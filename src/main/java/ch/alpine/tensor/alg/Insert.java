// code by jph
package ch.alpine.tensor.alg;

import java.util.stream.Stream;

import ch.alpine.tensor.Tensor;

/** Reference:
 * https://www.techempower.com/blog/2016/10/19/efficient-multiple-stream-concatenation-in-java/
 * 
 * <p>inspired by
 * <a href="https://reference.wolfram.com/language/ref/Insert.html">Insert</a> */
public enum Insert {
  ;
  /** Example:
   * <pre>
   * Insert[{a, c, d}, b, 1] == {a, b, c, d}
   * </pre>
   * 
   * @param tensor
   * @param element
   * @param index in the range 0, 1, ..., tensor.length()
   * @return new tensor equal to given tensor but with given element inserted at index
   * @throws Exception if index is out of range */
  public static Tensor of(Tensor tensor, Tensor element, int index) {
    return Tensor.of(Stream.of( //
        tensor.extract(0, index).stream(), //
        Stream.of(element.copy()), //
        tensor.extract(index, tensor.length()).stream()) //
        .flatMap(stream -> stream));
  }
}
