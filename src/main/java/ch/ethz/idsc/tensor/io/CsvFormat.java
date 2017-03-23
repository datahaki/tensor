// code by jph
package ch.ethz.idsc.tensor.io;

import java.util.stream.Collectors;
import java.util.stream.Stream;

import ch.ethz.idsc.tensor.Scalars;
import ch.ethz.idsc.tensor.Tensor;

/** Files.lines(Paths.get("filePath"))
 * Files.write(Paths.get("filePath"), (Iterable<String>) stream::iterator); */
public class CsvFormat {
  private static final String COMMA = ",";

  // does tensor have to be 2d array?
  public static Stream<String> ofMatrix(Tensor matrix) {
    return ofMatrix(matrix, COMMA);
  }

  public static Stream<String> ofMatrix(Tensor matrix, CharSequence delimiter) {
    return matrix.flatten(0).parallel() //
        .map(vector -> String.join(delimiter, //
            vector.flatten(0).map(Tensor::toString).collect(Collectors.toList())));
  }

  public static Tensor parse(Stream<String> stream) {
    return parse(stream, COMMA);
  }

  public static Tensor parse(Stream<String> stream, String regex) {
    return Tensor.of(stream.parallel() //
        .filter(line -> !line.isEmpty()) //
        .map(line -> Tensor.of(Stream.of(line.split(regex)).map(Scalars::fromString))));
  }
}
