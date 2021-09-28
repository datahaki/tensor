// code by jph
package ch.alpine.tensor.alg;

import java.util.stream.Stream;

import ch.alpine.tensor.Tensor;

/** {@link Flatten} is for convenience to wrap the Stream<Tensor>
 * returned by Tensor::flatten into a {@link Tensor}.
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
    return Tensor.of(Stream.of(tensors).flatMap(tensor -> tensor.flatten(-1)));
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
  public static Tensor of(Tensor tensor) {
    // input and output share references to scalar instances only
    // therefore Tensor::copy is not necessary
    return Tensor.of(tensor.flatten(-1));
  }

  /** Remark: in the special case of level == 0, the
   * function returns an exact copy of the input tensor.
   * 
   * @param tensor
   * @param level
   * @return */
  public static Tensor of(Tensor tensor, int level) {
    return level == 0 //
        ? tensor.copy()
        : Tensor.of(tensor.flatten(level).map(Tensor::copy));
  }
}
