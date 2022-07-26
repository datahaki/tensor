// code by jph
package ch.alpine.tensor.chq;

import ch.alpine.tensor.ComplexScalar;
import ch.alpine.tensor.IntegerQ;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Throw;
import ch.alpine.tensor.qty.Quantity;

/** implementation consistent with Mathematica
 * 
 * <p>Examples:
 * <pre>
 * NumberQ[ 13 / 17 ] == true
 * NumberQ[ 3.1415 ] == true
 * NumberQ[ 3.1415 + 1/2*I ] == true
 * 
 * NumberQ[ Infinity ] == false
 * NumberQ[ Indeterminate ] == false
 * NumberQ.of(Quantity.of(3, "m")) == false
 * </pre>
 * 
 * <p>{@link ScalarQ} returns true in all cases and is therefore not equivalent to NumberQ.
 * 
 * <pre>
 * NumberQ[ { ... } ] == false
 * </pre>
 * 
 * <p>NumberQ does not indicate whether {@link Scalar#number()} returns a Number.
 * For instance, {@link ComplexScalar#number()} throws an exception.
 * 
 * <p>inspired by
 * <a href="https://reference.wolfram.com/language/ref/NumberQ.html">NumberQ</a>
 * 
 * @see ExactScalarQ
 * @see IntegerQ */
/* package */ enum MathematicaNumberQ {
  ;
  /** @param scalar
   * @return whether scalar satisfies the NumberQ predicate */
  public static boolean of(Scalar scalar) {
    if (scalar instanceof ComplexScalar) {
      ComplexScalar complexScalar = (ComplexScalar) scalar;
      return of(complexScalar.real()) //
          && of(complexScalar.imag());
    }
    if (scalar instanceof Quantity)
      return false;
    return FiniteScalarQ.of(scalar) //
        || ExactScalarQ.of(scalar);
  }

  /** @param scalar
   * @return given scalar
   * @throws Exception if given scalar does not satisfy {@link #of(Scalar)} */
  public static Scalar require(Scalar scalar) {
    if (of(scalar))
      return scalar;
    throw new Throw(scalar);
  }

  /** @param tensor
   * @return whether all scalar entries in given tensor satisfy the predicate {@link #of(Scalar)}} */
  public static boolean all(Tensor tensor) {
    return tensor.flatten(-1).map(Scalar.class::cast).allMatch(MathematicaNumberQ::of);
  }
}
