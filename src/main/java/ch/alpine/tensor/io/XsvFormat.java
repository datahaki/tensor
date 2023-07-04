// code by jph
package ch.alpine.tensor.io;

import java.util.function.Function;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.alg.Flatten;

/** implementation for comma- or tab- separated values file format */
/* package */ enum XsvFormat {
  /** comma separated values format */
  CSV(',') {
    @Override
    Tensor embrace(String string) {
      return Tensors.fromString(Tensor.OPENING_BRACKET + string + Tensor.CLOSING_BRACKET);
    }
  },
  /** tab separated values file format */
  TSV('\t') {
    @Override
    Tensor embrace(String string) {
      return Tensors.fromString(Tensor.OPENING_BRACKET + string.replace('\t', ',') + Tensor.CLOSING_BRACKET);
    }
  };

  private final Collector<CharSequence, ?, String> collector;

  XsvFormat(int delimiter) {
    collector = Collectors.joining(Character.toString(delimiter));
  }

  /** In Mathematica the csv file is imported using
   * A=Import["filename.csv"];
   * 
   * <p>In MATLAB the csv file can be imported using
   * A=load('filename.csv');
   * 
   * @param tensor that may also be a {@link Scalar}
   * @return stream of lines that make up the csv format */
  public Stream<String> of(Tensor tensor) {
    // flatten(0) handles scalars as opposed to stream()
    return Flatten.stream(tensor, 0).map(this::row);
  }

  /** Example: The stream of the following strings
   * <pre>
   * "10, 200, 3"
   * "78"
   * "-3, 2.3"
   * </pre>
   * results in the tensor {{10, 200, 3}, {78}, {-3, 2.3}}
   * 
   * <p>Hint: To import a table from a csv file use {@link Import}.
   * 
   * @param stream of lines of file
   * @return tensor with rows defined by the entries of the input stream */
  public Tensor parse(Stream<String> stream) {
    return parse(stream.parallel(), this::embrace);
  }

  /** Default function for parsing:
   * Tensors::fromString
   * 
   * @param stream of lines of file
   * @param function that parses a row to a tensor
   * @return */
  public static Tensor parse(Stream<String> stream, Function<String, Tensor> function) {
    return Tensor.of(stream.map(function).sequential());
  }

  // helper function
  private String row(Tensor tensor) {
    // flatten(0) handles scalars as opposed to stream()
    return Flatten.stream(tensor, 0).map(Tensor::toString).collect(collector);
  }

  // helper function
  abstract Tensor embrace(String string);
}
