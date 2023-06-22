// code by jph
package ch.alpine.tensor.io;

import java.util.stream.Collectors;

import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.alg.ArrayQ;
import ch.alpine.tensor.alg.TensorRank;

/** Pretty helps to format tensors for easy reading in the console.
 * 
 * <p>Pretty can be used to export tensors as string expressions to MATLAB.
 * 
 * <p>inspired by
 * <a href="https://reference.wolfram.com/language/ref/MatrixForm.html">MatrixForm</a> */
public class Pretty {
  private static final String OPENING = "[\n";
  private static final String CLOSING = "]\n";

  /** @param tensor
   * @return string expression of tensor for use in System.out.println
   * @see Put#string(Tensor) */
  public static String of(Tensor tensor) {
    return new Pretty(tensor).toString();
  }

  // ---
  private final StringBuilder stringBuilder = new StringBuilder();
  private final String format;
  private int level = 0;

  private Pretty(Tensor tensor) {
    int max = tensor.flatten(-1) //
        .map(Object::toString) //
        .mapToInt(String::length) //
        .max().orElse(0);
    format = " %" + max + "s ";
    branch(tensor);
  }

  private void branch(Tensor tensor) {
    if (ArrayQ.of(tensor))
      stringBuilder.append(array(tensor));
    else {
      stringBuilder.append(spaces(level++)).append(OPENING);
      tensor.forEach(this::branch);
      stringBuilder.append(spaces(--level)).append(CLOSING);
    }
  }

  private String array(Tensor tensor) {
    switch (TensorRank.of(tensor)) {
    case 0: // scalar
      return String.format(format, tensor.toString());
    case 1: // vector
      return tensor.stream() //
          .map(this::array) //
          .collect(Collectors.joining("", spaces(level) + "[", CLOSING));
    default:
      StringBuilder stringBuilder = new StringBuilder();
      stringBuilder.append(spaces(level++)).append(OPENING);
      tensor.stream().map(this::array).forEach(stringBuilder::append);
      stringBuilder.append(spaces(--level)).append(CLOSING);
      return stringBuilder.toString();
    }
  }

  @Override
  public String toString() {
    return stringBuilder.toString().strip();
  }

  // helper function
  private static String spaces(int level) {
    return " ".repeat(level);
  }
}
