// code by jph
package ch.alpine.tensor.red;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.NavigableMap;
import java.util.TreeMap;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.pdf.BinCounts;
import ch.alpine.tensor.pdf.CDF;

/** consistent with Mathematica:
 * Tally requires a list as input. Tally of a scalar, for example
 * Mathematica::Tally[100] gives an error.
 * 
 * <p>inspired by
 * <a href="https://reference.wolfram.com/language/ref/Tally.html">Tally</a> */
public enum Tally {
  ;
  /** Careful: the keys in the map are references to selected elements in the provided tensor.
   * 
   * @param tensor
   * @return map that assigns elements of the tensor their multiplicity in given tensor
   * @throws Exception if given tensor is a {@link Scalar} */
  public static Map<Tensor, Long> of(Tensor tensor) {
    return of(tensor.stream());
  }

  /** Careful: the keys in the map are references to selected elements in the provided stream.
   * 
   * @param stream
   * @return map that assigns elements of the stream their multiplicity in given stream */
  public static <T> Map<T, Long> of(Stream<T> stream) {
    return stream.collect(Collectors.groupingBy( //
        Function.identity(), LinkedHashMap::new, Collectors.counting()));
  }

  /** Careful: the keys in the map are references to selected elements in the provided tensor.
   * 
   * function can be used to compute
   * <ul>
   * <li>a histogram,
   * <li>a cumulative distribution function, see {@link CDF}, or
   * <li>{@link BinCounts}
   * </ul>
   * 
   * @param tensor
   * @return navigable map that assigns entries of the tensor their multiplicity in the tensor
   * @throws Exception if given tensor is a {@link Scalar} */
  public static NavigableMap<Tensor, Long> sorted(Tensor tensor) {
    return sorted(tensor.stream());
  }

  /** Careful: the keys in the map are references to selected elements in the provided stream.
   * 
   * @param stream
   * @return */
  public static <T> NavigableMap<T, Long> sorted(Stream<T> stream) {
    return stream.collect(Collectors.groupingBy( //
        Function.identity(), TreeMap::new, Collectors.counting()));
  }
}
