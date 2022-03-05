// code by jph
package ch.alpine.tensor.alg;

import java.util.stream.IntStream;
import java.util.stream.Stream;

import ch.alpine.tensor.ScalarQ;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.fft.SpectrogramArray;

/** The tensor library implementation is consistent with Mathematica,
 * also when there are insufficient elements:
 * <pre>
 * Mathematica::Partition[{1, 2, 3, 4}, 3] == {{1, 2, 3}}
 * Tensor-Lib.::Partition[{1, 2, 3, 4}, 3] == {{1, 2, 3}}
 * </pre>
 * 
 * <p>Partition is used in {@link Transpose} and {@link SpectrogramArray}.
 * 
 * <p>inspired by
 * <a href="https://reference.wolfram.com/language/ref/Partition.html">Partition</a> */
public enum Partition {
  ;
  /** Partition[{a, b, c, d, e, f, g}, 2] == {{a, b}, {c, d}, {e, f}}
   * 
   * @param tensor
   * @param size positive
   * @return tensor of rank one higher than input tensor
   * @throws Exception if given tensor is a scalar */
  public static Tensor of(Tensor tensor, int size) {
    return of(tensor, size, size);
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
   * <p>Hint: function is used in {@link SpectrogramArray}
   * 
   * @param tensor
   * @param size not smaller than offset
   * @param offset positive
   * @return stream of tensors
   * @throws Exception if given tensor is a scalar */
  public static Stream<Tensor> stream(Tensor tensor, int size, int offset) {
    ScalarQ.thenThrow(tensor);
    if (offset <= 0 || size < offset)
      throw new IllegalArgumentException("size=" + size + " offset=" + offset);
    return IntStream.iterate(0, index -> index + offset) //
        .limit((tensor.length() + offset - size) / offset) //
        .mapToObj(index -> tensor.extract(index, index + size));
  }
}
