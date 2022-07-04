// code by jph
package ch.alpine.tensor.io;

import java.util.stream.Collectors;
import java.util.stream.Stream;

import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.spa.SparseArray;

/** utility to exchange data with Wolfram Mathematica
 * 
 * <p>Mathematica::Put stores an expression to a file
 * <code>Put[{1, 2, 3}, "filePath"]</code>
 * 
 * <p>Mathematica::Get retrieves an expression from a file
 * <code>expr = Get["filePath"]</code>
 * 
 * <p>String expressions may also be compatible with Java,
 * 
 * Careful: does not work for general unicode, for example mathematica
 * exports a string consisting of the 2 Chinese characters hanzi as:
 * "\:6c49\:5b57"
 * 
 * <p>The following string notation is incompatible with Java
 * <pre>
 * 1.2630135948105083*^17
 * 3.5849905564258352*^-18
 * </pre>
 * and are converted to
 * <pre>
 * 1.2630135948105083E17
 * 3.5849905564258352E-18
 * </pre> */
public enum MathematicaFormat {
  ;
  private static final String EXPONENT_JAVA = "E";
  private static final String EXPONENT_MATH = "*^";

  /** @param tensor
   * @return strings parsed by Mathematica as given tensor */
  public static Stream<String> of(Tensor tensor) {
    final String string;
    if (tensor instanceof SparseArray sparseArray)
      string = sparseArray.toString(1) //
          .replace(EXPONENT_JAVA, EXPONENT_MATH) //
          .replace(", {", ",\n{"); // <- introduce new line
    else
      // TODO TENSOR does not handle nested SparseArray`s
      string = tensor.toString() //
          .replace(EXPONENT_JAVA, EXPONENT_MATH) //
          .replace("}, {", "},\n{"); // <- introduce new line
    return Stream.of(string.split("\n"));
  }

  /** @param stream of strings of Mathematica encoded tensor
   * @return tensor */
  public static Tensor parse(Stream<String> stream) {
    return Tensors.fromString(stream //
        .map(string -> string.replace(EXPONENT_MATH, EXPONENT_JAVA)) //
        .map(MathematicaFormat::join) //
        .collect(Collectors.joining("")));
  }

  // helper function
  private static String join(String string) {
    return string.endsWith("\\") //
        ? string.substring(0, string.length() - 1)
        : string;
  }
}
