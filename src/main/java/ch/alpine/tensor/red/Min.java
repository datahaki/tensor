// code by jph
package ch.alpine.tensor.red;

import java.util.function.BinaryOperator;
import java.util.function.UnaryOperator;

import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.api.ScalarUnaryOperator;

/** Example use:
 * <pre>
 * vector.stream().reduce(Min::of).get();
 * matrix.set(Min.function(RealScalar.ONE), Tensor.ALL, 2);
 * tensor.map(Min.function(RealScalar.ZERO));
 * </pre>
 * 
 * <p>inspired by
 * <a href="https://reference.wolfram.com/language/ref/Min.html">Min</a> */
public enum Min {
  ;
  /** function is a {@link BinaryOperator} that can be used in reduce()
   * 
   * Symmetry shall be guaranteed
   * <pre>
   * Min[a, b] == Min[b, a]
   * </pre>
   * 
   * @param a
   * @param b
   * @return the smaller one among a and b
   * @throws Exception if a cannot be compared to b */
  public static <T> T of(T a, T b) {
    @SuppressWarnings("unchecked")
    Comparable<T> comparable = (Comparable<T>) a;
    return comparable.compareTo(b) > 0 ? b : a;
  }

  /** @param a
   * @return function that maps input to the smaller one of input and a */
  public static ScalarUnaryOperator function(Scalar a) {
    return b -> of(a, b);
  }

  /** @param a
   * @return function that maps input to the smaller one of input and a */
  public static <T> UnaryOperator<T> function(T a) {
    return b -> of(a, b);
  }
}
