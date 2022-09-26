// code by jph
package ch.alpine.tensor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.IntStream;

import ch.alpine.tensor.alg.ConstantArray;
import ch.alpine.tensor.alg.Outer;
import ch.alpine.tensor.io.StringScalar;
import ch.alpine.tensor.spa.SparseArray;

/** utility class that provides constructors of tensors for convenience.
 * 
 * <p>The methods are intentionally non-parallel to ensure a deterministic
 * construction process. Parallel stream processing can lead to significant
 * speed-up. Parallel stream processing has to be decided case by case.
 * Several parallel methods are provided in {@link Parallelize}. */
public enum Tensors {
  ;
  /** @return new modifiable tensor instance with no entries, i.e. length() == 0
   * @see #isEmpty(Tensor) */
  public static Tensor empty() {
    return new TensorImpl(new ArrayList<>());
  }

  /** @param initialCapacity non-negative
   * @return empty tensor for which initialCapacity number of {@link Tensor#append(Tensor)}
   * operations are intended
   * @throws Exception if initialCapacity is negative */
  public static Tensor reserve(int initialCapacity) {
    return new TensorImpl(new ArrayList<>(initialCapacity));
  }

  /** @param tensors array
   * @return concatenation of copies of given {@link Tensor}s or {@link Scalar}s */
  public static Tensor of(Tensor... tensors) {
    return Tensor.of(Arrays.stream(tensors).map(Tensor::copy));
  }

  /** Hint: function does not check scalar arguments for null
   * 
   * @param scalars array
   * @return vector of references to given {@link Scalar}s */
  public static Tensor of(Scalar... scalars) {
    return Tensor.of(Arrays.stream(scalars));
  }

  /** Example:
   * <pre>
   * Tensors.vector(24, 0, -5, 1.23, 9f)
   * </pre>
   * 
   * @param numbers array
   * @return vector of numbers as {@link RealScalar}s */
  public static Tensor vector(Number... numbers) {
    return Tensor.of(Arrays.stream(numbers).map(RealScalar::of));
  }

  /** @param list
   * @return vector of numbers in list as {@link RealScalar}s */
  public static Tensor vector(List<? extends Number> list) {
    return Tensor.of(list.stream().map(RealScalar::of));
  }

  /** @param function
   * @param length
   * @return vector of length with i-th entry == function.apply(i) */
  public static Tensor vector(Function<Integer, ? extends Tensor> function, int length) {
    return Tensor.of(IntStream.range(0, length).boxed().map(function));
  }

  /** @param values
   * @return tensor of {@link RationalScalar} with given values */
  public static Tensor vectorInt(int... values) {
    return Tensor.of(Arrays.stream(values).mapToObj(RealScalar::of));
  }

  /** @param values
   * @return tensor of {@link RationalScalar} with given values */
  public static Tensor vectorLong(long... values) {
    return Tensor.of(Arrays.stream(values).mapToObj(RealScalar::of));
  }

  /** @param values
   * @return tensor of {@link DoubleScalar} with given values */
  public static Tensor vectorFloat(float... values) {
    return Tensor.of(IntStream.range(0, values.length).mapToDouble(i -> values[i]).mapToObj(DoubleScalar::of));
  }

  /** @param values
   * @return tensor of {@link DoubleScalar} with given values */
  public static Tensor vectorDouble(double... values) {
    return Tensor.of(Arrays.stream(values).mapToObj(DoubleScalar::of));
  }

  /** @param biFunction
   * @param n number of rows
   * @param m number of columns
   * @return (n x m)-matrix with (i, j)th-entry == bifunction.apply(i, j)
   * @see Outer */
  public static Tensor matrix(BiFunction<Integer, Integer, ? extends Tensor> biFunction, int n, int m) {
    return Tensor.of(IntStream.range(0, n).mapToObj( //
        i -> Tensor.of(IntStream.range(0, m).mapToObj(j -> biFunction.apply(i, j)))));
  }

  /** @param data
   * @return matrix with dimensions and {@link Scalar} entries as array data */
  public static Tensor matrix(Scalar[][] data) {
    return Tensor.of(Arrays.stream(data).map(Tensors::of));
  }

  /** @param data
   * @return matrix with dimensions and {@link RealScalar} entries */
  public static Tensor matrix(Number[][] data) {
    return Tensor.of(Arrays.stream(data).map(Tensors::vector));
  }

  /** @param data
   * @return matrix with dimensions and {@link RationalScalar} entries as array data */
  public static Tensor matrixInt(int[][] data) {
    return Tensor.of(Arrays.stream(data).map(Tensors::vectorInt));
  }

  /** @param data
   * @return matrix with dimensions and {@link RationalScalar} entries as array data */
  public static Tensor matrixLong(long[][] data) {
    return Tensor.of(Arrays.stream(data).map(Tensors::vectorLong));
  }

  /** @param values
   * @return tensor of {@link DoubleScalar} with given values */
  public static Tensor matrixFloat(float[][] values) {
    return Tensor.of(Arrays.stream(values).map(Tensors::vectorFloat));
  }

  /** @param data
   * @return matrix with dimensions and {@link DoubleScalar} entries as array data */
  public static Tensor matrixDouble(double[][] data) {
    return Tensor.of(Arrays.stream(data).map(Tensors::vectorDouble));
  }

  /** Example:
   * Tensors.fromString("{1+3/2*I, {3.7[m*s], 9/4[kg^-1]}}");
   * 
   * Remark:
   * If the string does not have consistent brackets,
   * the returned tensor is instance of {@link StringScalar}.
   * 
   * @param string
   * @return tensor parsed from given string
   * @throws Exception if given string is null
   * @see StringScalar */
  public static Tensor fromString(String string) {
    return TensorParser.of(string, Scalars::fromString);
  }

  /** @param string
   * @param function that parses a string to a scalar
   * @return tensor parsed from given string
   * @throws Exception if given string is null */
  public static Tensor fromString(String string, Function<String, Scalar> function) {
    return TensorParser.of(string, function);
  }

  // ---
  /** @param tensor
   * @return true if tensor is a vector with zero entries, and
   * false if tensor contains entries or is a {@link Scalar} */
  public static boolean isEmpty(Tensor tensor) { // Marc's function
    return tensor.length() == 0;
  }

  /** @param tensor
   * @return false if tensor is a vector with zero entries, and
   * true if tensor contains entries or is a {@link Scalar} */
  public static boolean nonEmpty(Tensor tensor) {
    return tensor.length() != 0;
  }

  /** Examples:
   * A tensor returned by the method {@link Tensor#unmodifiable()},
   * in particular {@link SparseArray#unmodifiable()} and
   * {@link ConstantArray} are not modifiable.
   * 
   * Also, any {@link Scalar} is by specification immutable
   * and therefore unmodifiable.
   * 
   * Careful:
   * Some instances of tensor not modifiable for which this method
   * returns false. For instance, {@link Unprotect#using(List)}
   * with unmodifiable lists.
   * 
   * @param tensor
   * @return true if given tensor is guaranteed to be unmodifiable,
   * and false if given tensor may be modifiable. */
  public static boolean isUnmodifiable(Tensor tensor) {
    return tensor.unmodifiable() == tensor; // equal by reference intended
  }

  // ---
  private static final Tensor UNMODIFIABLE_EMPTY = empty().unmodifiable();

  /** @return singleton instance of unmodifiable empty tensor */
  public static Tensor unmodifiableEmpty() {
    return UNMODIFIABLE_EMPTY;
  }
}
