// code by jph
package ch.alpine.tensor.red;

import java.util.stream.Stream;

import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.api.ScalarUnaryOperator;
import ch.alpine.tensor.num.Boole;

/** not consistent with Mathematica for singleton collection
 * <pre>
 * Mathematica::KroneckerDelta[3] == 0
 * Tensor-Lib.::KroneckerDelta[3] == 1
 * </pre>
 * 
 * <p>consistent with Mathematica for empty collection
 * <pre>
 * Mathematica::KroneckerDelta[ ] == 1
 * Tensor-Lib.::KroneckerDelta[ ] == 1
 * </pre>
 * 
 * <p>inspired by
 * <a href="https://reference.wolfram.com/language/ref/KroneckerDelta.html">KroneckerDelta</a> */
public enum KroneckerDelta {
  ;
  /** @param scalar
   * @return function that maps input to 1 if input matches scalar, otherwise gives 0 */
  public static ScalarUnaryOperator function(Scalar scalar) {
    return value -> of(scalar, value);
  }

  /** @param objects
   * @return RealScalar.ONE if there are no two objects are distinct,
   * otherwise RealScalar.ZERO */
  public static Scalar of(Object... objects) {
    return of(Stream.of(objects));
  }

  /** @param stream
   * @return RealScalar.ONE if there are no two objects in the stream that are distinct,
   * otherwise RealScalar.ZERO */
  public static Scalar of(Stream<?> stream) {
    return Boole.of(stream.distinct().limit(2).count() <= 1);
  }
}
