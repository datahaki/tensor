// code by jph
package ch.alpine.tensor.alg;

import java.util.stream.Gatherers;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Unprotect;
import ch.alpine.tensor.chq.ScalarQ;
import ch.alpine.tensor.ext.Integers;

/** The tensor library implementation is consistent with Mathematica,
 * also when there are insufficient elements:
 * <pre>
 * Mathematica::Partition[{1, 2, 3, 4}, 3] == {{1, 2, 3}}
 * Tensor-Lib.::Partition[{1, 2, 3, 4}, 3] == {{1, 2, 3}}
 * </pre>
 * 
 * <p>Partition is used in {@link Transpose} and SpectrogramArray.
 * 
 * <p>inspired by
 * <a href="https://reference.wolfram.com/language/ref/Partition.html">Partition</a> */
public enum Partition {
  ;
  /** Partition[{a, b, c, d, e, f, g}, 2] == {{a, b}, {c, d}, {e, f}}
   * 
   * Partition[{a, b, c, d, e, f, g}, 4] == {{a, b, c, d}}
   * 
   * Partition[{a, b, c, d, e, f, g}, 10] == {}
   * 
   * @param tensor
   * @param size positive
   * @return tensor of rank one higher than input tensor
   * @throws Exception if given tensor is a scalar */
  public static Tensor of(Tensor tensor, int size) {
    return of(tensor, size, size);
  }

  /** EXPERIMENTAL
   * 
   * @param tensor
   * @param size
   * @return */
  public static Tensor gather(Tensor tensor, int size) {
    return Tensor.of(tensor.stream() //
        .gather(Gatherers.windowFixed(size)).map(Unprotect::using));
  }

  /** Partition[{a, b, c, d, e, f, g}, 3, 2] == {{a, b, c}, {c, d, e}, {e, f, g}}
   * 
   * @param tensor
   * @param size not smaller than offset
   * @param offset positive
   * @return tensor of rank one higher than input tensor
   * @throws Exception if given tensor is a scalar */
  public static Tensor of(Tensor tensor, int size, int offset) {
    return Tensor.of(stream(tensor, size, offset));
  }

  /** Partition.stream[{a, b, c, d, e, f, g}, 3, 2] == stream {a, b, c}, {c, d, e}, {e, f, g}
   * 
   * <p>Hint: function is used in SpectrogramArray
   * 
   * @param tensor
   * @param size not smaller than offset
   * @param offset positive
   * @return stream of tensors
   * @throws Exception if given tensor is a scalar */
  public static Stream<Tensor> stream(Tensor tensor, int size, int offset) {
    // TODO check if Stream.gather is better
    ScalarQ.thenThrow(tensor);
    Integers.requirePositive(offset);
    Integers.requireLessEquals(offset, size);
    return IntStream.iterate(0, index -> index + offset) //
        .limit((tensor.length() + offset - size) / offset) //
        .mapToObj(index -> tensor.extract(index, index + size));
  }

  /** @param tensor
   * @param size
   * @return */
  public static Stream<Tensor> stream(Tensor tensor, int size) {
    return stream(tensor, size, size);
  }
}
