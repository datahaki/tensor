// code by jph
package ch.alpine.tensor.alg;

import java.util.stream.Stream;

import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.ext.Integers;

/** ArrayReshape is used in {@link Transpose}
 * 
 * <p>inspired by
 * <a href="https://reference.wolfram.com/language/ref/ArrayReshape.html">ArrayReshape</a> */
public enum ArrayReshape {
  ;
  /** compliant with Mathematica's ArrayReshape
   * <code>
   * ArrayReshape[{a, b, c, d, e, f}, {2, 3}] == {{a, b, c}, {d, e, f}}
   * ArrayReshape[{a, b, c, d, e, f}, {2, 3, 1}] == {{{a}, {b}, {c}}, {{d}, {e}, {f}}}
   * </code>
   * 
   * implementation requires
   * <code>stream.count() == prod(size)</code>
   * 
   * @param stream
   * @param size the product of the entries have to equal the count of elements in the given stream
   * @return tensor with entries from stream and first dimensions determined by size
   * @throws Exception if the product of the elements in size does not equal the number of elements
   * in the given stream */
  public static Tensor of(Stream<? extends Tensor> stream, int... size) {
    Tensor transpose = Tensor.of(stream);
    int length = transpose.length();
    int numel = size[0];
    for (int index = size.length - 1; 0 < index; --index) {
      numel *= size[index];
      transpose = Partition.of(transpose, size[index]);
    }
    Integers.requireEquals(length, numel);
    return transpose;
  }

  /** @param tensor
   * @param dimensions non-empty
   * @return
   * @throws Exception if the product of the elements in dimensions
   * does not equal the number of scalars in given tensor */
  public static Tensor of(Tensor tensor, int... dimensions) {
    return of(tensor.flatten(-1), dimensions);
  }
}
