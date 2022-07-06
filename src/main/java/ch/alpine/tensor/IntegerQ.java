// code by jph
package ch.alpine.tensor;

import java.util.Objects;

import ch.alpine.tensor.chq.ExactScalarQ;

/** implementation consistent with Mathematica but requires scalar type as input
 * 
 * <p>Examples:
 * <pre>
 * IntegerQ.of(RationalScalar.of(7, 1)) == true
 * IntegerQ.of(RationalScalar.of(7, 2)) == false
 * IntegerQ.of(DoubleScalar.of(7)) == false
 * </pre>
 * 
 * <p>inspired by
 * <a href="https://reference.wolfram.com/language/ref/IntegerQ.html">IntegerQ</a>
 * 
 * @see ExactScalarQ */
public enum IntegerQ {
  ;
  /** @param scalar
   * @return true, if given scalar is instance of {@link RationalScalar} with denominator == 1 */
  public static boolean of(Scalar scalar) {
    return Objects.requireNonNull(scalar) instanceof RationalScalar rationalScalar //
        && rationalScalar.isInteger();
  }

  /** @param scalar
   * @return given scalar
   * @throws Exception if given scalar is not an integer in exact precision */
  public static Scalar require(Scalar scalar) {
    if (of(scalar))
      return scalar;
    throw Throw.of(scalar);
  }
}
