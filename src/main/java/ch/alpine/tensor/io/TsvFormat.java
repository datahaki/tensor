// code by jph
package ch.alpine.tensor.io;

import java.util.function.Function;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;

/** tab separated values file format */
/* package */ enum TsvFormat {
  ;
  private static final Collector<CharSequence, ?, String> COLLECTOR = Collectors.joining("\t");

  public static Stream<String> of(Tensor tensor) {
    return tensor.flatten(0).map(TsvFormat::row);
  }

  public static Tensor parse(Stream<String> stream) {
    return parse(stream, TsvFormat::embrace);
  }

  public static Tensor parse(Stream<String> stream, Function<String, Tensor> function) {
    return Tensor.of(stream.parallel().map(function).sequential());
  }

  // helper function
  private static String row(Tensor tensor) {
    return tensor.flatten(0).map(Tensor::toString).collect(COLLECTOR);
  }

  private static Tensor embrace(String string) {
    return Tensors.fromString(Tensor.OPENING_BRACKET + string.replace('\t', ',') + Tensor.CLOSING_BRACKET);
  }
}
