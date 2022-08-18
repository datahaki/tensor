// code by jph
package ch.alpine.tensor.alg;

import java.util.List;
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
  public static Tensor of(Stream<? extends Tensor> stream, List<Integer> size) {
    Tensor tensor = Tensor.of(stream);
    int length = tensor.length();
    int numel = size.get(0);
    for (int index = size.size() - 1; 0 < index; --index) {
      int count = size.get(index);
      numel *= count;
      tensor = Partition.of(tensor, count);
    }
    Integers.requireEquals(length, numel);
    return tensor;
  }

  /** @param stream
   * @param size
   * @return
   * @see #of(Stream, List) */
  public static Tensor of(Stream<? extends Tensor> stream, int... size) {
    return of(stream, Integers.asList(size));
  }

  /** @param tensor
   * @param dimensions non-empty
   * @return
   * @throws Exception if the product of the elements in dimensions
   * does not equal the number of scalars in given tensor */
  public static Tensor of(Tensor tensor, List<Integer> dimensions) {
    return of(tensor.flatten(-1), dimensions);
  }

  /** @param tensor
   * @param dimensions non-empty
   * @return
   * @see #of(Tensor, List) */
  public static Tensor of(Tensor tensor, int... dimensions) {
    return of(tensor, Integers.asList(dimensions));
  }
}
