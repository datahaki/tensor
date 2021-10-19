// code by jph
package ch.alpine.tensor.io;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Function;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import ch.alpine.tensor.DecimalScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.ScalarQ;
import ch.alpine.tensor.Scalars;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.TensorRuntimeException;
import ch.alpine.tensor.alg.ArrayQ;
import ch.alpine.tensor.alg.Dimensions;
import ch.alpine.tensor.alg.Flatten;
import ch.alpine.tensor.alg.Transpose;
import ch.alpine.tensor.qty.Quantity;

/** The tensor library does not differentiate between column- and row-vectors.
 * Vectors, i.e. tensors or rank 1, are exported to MATLAB as column vectors, i.e.
 * their size in MATLAB is of the form [n, 1].
 * 
 * <p>String expressions of certain Scalar types, for instance those of
 * {@link DecimalScalar}, or {@link Quantity} may not be parsed correctly by MATLAB.
 * The user can provide a customized scalar-to-string mapping to facilitate the desired
 * import of these values.
 * 
 * <p>Scalars of type {@link Quantity} also require a custom conversion to string.
 * The tests contains an example.
 * 
 * <p>Hint:
 * for the export of vectors and matrices, {@link Pretty} may also be a solution.
 * 
 * @see Export */
public enum MatlabExport {
  ;
  /** @param tensor with array structure
   * @param function that maps a {@link Scalar} in the given tensor to a string expression
   * @return lines of MATLAB function that returns tensor
   * @throws Exception if given tensor does not satisfy {@link ArrayQ} */
  public static Stream<String> of(Tensor tensor, Function<Scalar, String> function) {
    Dimensions dimensions = new Dimensions(tensor);
    if (!dimensions.isArray())
      throw TensorRuntimeException.of(tensor);
    List<String> list = new LinkedList<>();
    list.add("function a=anonymous");
    list.add("% auto-generated code. do not modify.");
    list.add("Infinity=Inf;");
    list.add("I=i;");
    if (ScalarQ.of(tensor))
      list.add("a=" + function.apply((Scalar) tensor) + ";");
    else {
      List<Integer> dims = new ArrayList<>(dimensions.list());
      int[] sigma = new int[dims.size()];
      IntStream.range(0, dims.size()).forEach(index -> sigma[index] = dims.size() - index - 1);
      if (dims.size() == 1)
        dims.add(1); // [n, 1]
      list.add("a=zeros(" + dims + ");");
      int count = 0;
      for (Tensor _scalar : Flatten.of(Transpose.of(tensor, sigma))) {
        ++count;
        Scalar scalar = (Scalar) _scalar;
        if (Scalars.nonZero(scalar))
          list.add("a(" + count + ")=" + function.apply(scalar) + ";");
      }
    }
    return list.stream();
  }

  /** Hint: when exporting the strings to a file use {@link Export}
   * and specify a file with extension "m"
   * 
   * @param tensor with array structure
   * @return lines of MATLAB function that returns tensor
   * @throws Exception if given tensor does not satisfy {@link ArrayQ} */
  public static Stream<String> of(Tensor tensor) {
    return of(tensor, Object::toString);
  }
}
