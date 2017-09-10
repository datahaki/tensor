// code by jph
package ch.ethz.idsc.tensor.io;

import java.util.LinkedList;
import java.util.List;
import java.util.function.Function;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.ScalarQ;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.alg.Dimensions;
import ch.ethz.idsc.tensor.alg.Flatten;
import ch.ethz.idsc.tensor.alg.Transpose;

/** vectors, i.e. tensors or rank 1, are exported to MATLAB as column vectors
 * 
 * certain scalar's are not supported by the function Scalar::toString:
 * Double.POSITIVE_INFINITY -> results in "Infinity" whereas MATLAB requires "Inf"
 * the user must provide a customized scalar to string mapping to cover these cases.
 * 
 * Hint:
 * for the export of vectors and matrices, {@link Pretty} may also be a solution. */
// EXPERIMENTAL
public enum MatlabExport {
  ;
  /** The stream of strings can be written to a file using
   * <code>Files.write(Paths.get("filePath"), (Iterable<String>) stream::iterator);</code>
   * 
   * @param tensor must not be {@link Scalar}. For scalars, use Tensors.of(scalar);
   * @param function that maps a {@link Scalar} to a string expression
   * @return lines of MATLAB function that returns tensor */
  @SuppressWarnings("incomplete-switch")
  public static Stream<String> of(Tensor tensor, Function<Scalar, String> function) {
    ScalarQ.thenThrow(tensor);
    List<String> list = new LinkedList<>();
    list.add("function a=anonymous");
    list.add("% auto-generated code. do not modify.");
    list.add("I=i;");
    List<Integer> dims = Dimensions.of(tensor);
    Integer[] sigma = new Integer[dims.size()];
    IntStream.range(0, dims.size()).forEach(i -> sigma[i] = dims.size() - i - 1);
    // ---
    switch (dims.size()) {
    case 0:
      dims.add(0); // [0]
      break;
    case 1:
      dims.add(1); // [n, 1]
      break;
    }
    list.add("a=zeros(" + dims + ");");
    int count = 0;
    for (Tensor scalar : Flatten.of(Transpose.of(tensor, sigma)))
      list.add("a(" + (++count) + ")=" + function.apply(scalar.Get()) + ";");
    return list.stream();
  }

  /** @param tensor must not be {@link Scalar}. For scalars, use Tensors.of(scalar);
   * @return lines of MATLAB function that returns tensor */
  public static Stream<String> of(Tensor tensor) {
    return of(tensor, Object::toString);
  }
}
