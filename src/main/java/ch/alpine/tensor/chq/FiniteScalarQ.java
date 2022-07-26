// code by jph
package ch.alpine.tensor.chq;

import java.util.Objects;

import ch.alpine.tensor.DoubleScalar;
import ch.alpine.tensor.MultiplexScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Throw;
import ch.alpine.tensor.api.InexactScalarMarker;

/** predicate that checks whether given scalar is finite.
 * 
 * The predicate invokes function {@link InexactScalarMarker#isFinite()}
 * on scalar, or nested scalars.
 * 
 * For all exact scalars, the result is true.
 * 
 * {@link DoubleScalar} may encode Infinity, as well as NaN
 * for which false is returned:
 * 
 * <pre>
 * FiniteScalarQ[ +Infinity ] == false
 * FiniteScalarQ[ -Infinity ] == false
 * FiniteScalarQ[ NaN ] == false
 * </pre> */
public enum FiniteScalarQ {
  ;
  /** @param scalar
   * @return true otherwise true */
  public static boolean of(Scalar scalar) {
    if (scalar instanceof InexactScalarMarker inexactScalarMarker)
      return inexactScalarMarker.isFinite();
    if (scalar instanceof MultiplexScalar multiplexScalar)
      return multiplexScalar.allMatch(FiniteScalarQ::of);
    Objects.requireNonNull(scalar);
    return true;
  }

  /** @param scalar
   * @return given scalar
   * @throws Exception if given scalar is not a finite scalar */
  public static Scalar require(Scalar scalar) {
    if (of(scalar))
      return scalar;
    throw new Throw(scalar);
  }
}
