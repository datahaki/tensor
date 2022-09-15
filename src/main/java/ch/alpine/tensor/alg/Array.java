// code by jph
package ch.alpine.tensor.alg;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.ext.Integers;
import ch.alpine.tensor.spa.SparseArray;

/** The implementation is consistent with Mathematica.
 * Special examples:
 * <pre>
 * Array[3 &, {}] == 3
 * Array[0 &amp;, {0, 1}] == {}
 * Array.zeros(0, 1) == {}
 * Array[0 &amp;, {1, 0, 1}] == {{}}
 * Array.zeros(1, 0, 1) == {{}}
 * </pre>
 * 
 * <p>inspired by
 * <a href="https://reference.wolfram.com/language/ref/Array.html">Array</a>
 * 
 * @see ConstantArray
 * @see Tuples */
public enum Array {
  ;
  /** @param function maps given index to {@link Tensor}, or {@link Scalar}
   * @param dimensions with non-negative entries
   * @return tensor with given dimensions and entries as function(index)
   * @throws Exception if any dimension is negative */
  public static Tensor of(Function<List<Integer>, ? extends Tensor> function, int... dimensions) {
    return of(function, Integers.asList(dimensions));
  }

  /** @param function maps given index to {@link Tensor}, or {@link Scalar}
   * @param dimensions with non-negative entries
   * @return tensor with given dimensions and entries as function(index)
   * @throws Exception if any dimension is negative */
  public static Tensor of(Function<List<Integer>, ? extends Tensor> function, List<Integer> dimensions) {
    dimensions.forEach(Integers::requirePositiveOrZero);
    return of(function, 0, dimensions, new ArrayList<>(dimensions));
  }

  // helper function
  private static Tensor of(Function<List<Integer>, ? extends Tensor> function, int level, List<Integer> dimensions, List<Integer> index) {
    if (level == dimensions.size())
      return function.apply(index);
    return Tensor.of(IntStream.range(0, dimensions.get(level)) //
        .mapToObj(count -> {
          index.set(level, count);
          return of(function, level + 1, dimensions, index);
        }));
  }

  // ---
  /** @param supplier
   * @param dimensions
   * @return */
  public static Tensor fill(Supplier<? extends Scalar> supplier, int... dimensions) {
    return fill(supplier, 0, Integers.asList(dimensions));
  }

  /** @param supplier
   * @param dimensions
   * @return */
  public static Tensor fill(Supplier<? extends Scalar> supplier, List<Integer> dimensions) {
    if (dimensions.isEmpty())
      return supplier.get();
    dimensions.forEach(Integers::requirePositiveOrZero);
    return fill(supplier, 0, dimensions);
  }

  // helper function
  private static Tensor fill(Supplier<? extends Scalar> supplier, int level, List<Integer> dimensions) {
    int next = level + 1;
    return Tensor.of(Stream.generate(dimensions.size() == next //
        ? supplier
        : () -> fill(supplier, next, dimensions)) //
        .limit(dimensions.get(level)));
  }

  // ---
  /** Careful:
   * {@link #zeros(int...)} is not consistent with MATLAB::zeros.
   * In the tensor library, the number of integer parameters equals the rank
   * of the returned tensor. In Matlab this is not the case.
   * 
   * Examples:
   * <pre>
   * Array.zeros(3) == Tensors.vector(0, 0, 0) == {0, 0, 0}
   * Array.zeros(2, 3) == {{0, 0, 0}, {0, 0, 0}}
   * </pre>
   * 
   * @param dimensions
   * @return tensor of {@link RealScalar#ZERO} with given dimensions
   * @throws Exception if any of the integer parameters is negative */
  public static Tensor zeros(int... dimensions) {
    return zeros(Integers.asList(dimensions));
  }

  /** @param dimensions
   * @return tensor of {@link RealScalar#ZERO} with given dimensions
   * @throws Exception if any of the integer parameters is negative */
  public static Tensor zeros(List<Integer> dimensions) {
    if (dimensions.isEmpty())
      return RealScalar.ZERO;
    dimensions.forEach(Integers::requirePositiveOrZero);
    return fill(() -> RealScalar.ZERO, 0, dimensions);
  }

  /** @param dimensions
   * @return empty sparse array with given dimensions and {@link RealScalar#ZERO} as fallback */
  public static Tensor sparse(int... dimensions) {
    return SparseArray.of(RealScalar.ZERO, dimensions);
  }

  // ---
  /** traverses the indices of a hypothetical array of given dimensions
   * 
   * Example:
   * Array.stream(2, 1, 3) gives the following stream of integer lists
   * [0, 0, 0]
   * [0, 0, 1]
   * [0, 0, 2]
   * [1, 0, 0]
   * [1, 0, 1]
   * [1, 0, 2]
   * 
   * @param dimensions with non-negative entries
   * @return stream of unmodifiable lists of integers
   * @throws Exception if any dimension is negative */
  public static Stream<List<Integer>> stream(int... dimensions) {
    return stream(Integers.asList(dimensions));
  }

  /** @param dimensions with non-negative entries
   * @return stream of unmodifiable lists of integers
   * @throws Exception if any dimension is negative */
  public static Stream<List<Integer>> stream(List<Integer> dimensions) {
    dimensions.forEach(Integers::requirePositiveOrZero);
    return recur(Stream.of(new int[dimensions.size()]), 0, dimensions) //
        .map(int[]::clone) //
        .map(Integers::asList);
  }

  private static Stream<int[]> recur(Stream<int[]> stream, int level, List<Integer> dimensions) {
    return level == dimensions.size() //
        ? stream
        : recur(stream.flatMap(array -> IntStream.range(0, dimensions.get(level)).mapToObj(i -> {
          array[level] = i;
          return array;
        })), level + 1, dimensions);
  }
}
