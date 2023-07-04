// code by jph
package ch.alpine.tensor.alg;

import java.util.Arrays;
import java.util.stream.Stream;

import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;

/** {@link Flatten} collects the stream of tensors into a {@link Tensor}.
 * 
 * <p>{@link Flatten} undoes the work of {@link Partition}.
 * 
 * <p>For scalar input, the implementation is not consistent with Mathematica.
 * <pre>
 * Tensor::Flatten[3.14] == {3.14}
 * Mathematica::Flatten[3.14] gives an error
 * </pre>
 * 
 * <p>inspired by
 * <a href="https://reference.wolfram.com/language/ref/Flatten.html">Flatten</a> */
public enum Flatten {
  ;
  /** More general implementation than Mathematica::Flatten as multiple
   * arguments are permitted. Examples:
   * <pre>
   * Flatten[{{a, b, c}, {{d}, e}}] == {a, b, c, d, e}
   * Flatten[{1, 2, 3}, 4, {{5}, 6}] == {1, 2, 3, 4, 5, 6}
   * </pre>
   * 
   * @param tensors one or more
   * @return */
  public static Tensor of(Tensor... tensors) {
    // input and output share references to scalar instances only
    // therefore Tensor::copy is not necessary
    return Tensor.of(Arrays.stream(tensors).flatMap(tensor -> stream(tensor, -1)));
  }

  /** Special case of {@link Flatten#of(Tensor...)}
   * 
   * Implementation as in Mathematica::Flatten
   * <pre>
   * Flatten[{{a, b, c}, {{d}, e}}] == {a, b, c, d, e}
   * </pre>
   * 
   * @param tensor
   * @return */
  /** stream access to the entries at given level of this tensor.
   * entries at given level can be tensors or scalars.
   * 
   * <p>For the input <code>level == -1</code>, the return stream consists
   * of all {@link Scalar}s in this tensor.
   * 
   * <p>If this tensor has been marked as unmodifiable, the elements of
   * the stream are unmodifiable as well.
   * 
   * <p>Unlike {@link #stream()}, function {@link #flatten(int)} may be
   * invoked on a {@link Scalar}. In that case the return value is the
   * stream with the scalar as single element.
   * 
   * @param level
   * @return non-parallel stream, the user may invoke .parallel() */
  public static Stream<Tensor> stream(Tensor tensor, int level) {
    if (tensor instanceof Scalar)
      return Stream.of(tensor);
    if (level == 0)
      return tensor.stream();
    int ldecr = level - 1;
    return tensor.stream().flatMap(entry -> stream(entry, ldecr));
  }

  public static Stream<Scalar> scalars(Tensor tensor) {
    return stream(tensor, -1).map(Scalar.class::cast);
  }

  /** Remark: in the special case of level == 0, the
   * function returns an exact copy of the input tensor.
   * 
   * @param tensor
   * @param level
   * @return */
  public static Tensor of(Tensor tensor, int level) {
    return Tensor.of(stream(tensor, level).map(Tensor::copy));
  }
}
