// code by jph
package ch.alpine.tensor.api;

import ch.alpine.tensor.DecimalScalar;
import ch.alpine.tensor.DoubleScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.chq.ExactScalarQ;
import ch.alpine.tensor.chq.FiniteScalarQ;

/** a {@link Scalar} may implement the interface to signal that the value is not an exact encoding.
 * 
 * <p>a {@link Scalar} that does not implement {@link InexactScalarMarker} is assumed to
 * be encoded with exact precision by {@link ExactScalarQ}.
 * 
 * @see DoubleScalar
 * @see DecimalScalar */
public interface InexactScalarMarker {
  /** Examples:
   * FiniteScalarQ[Pi.VALUE] == true
   * 
   * FiniteScalarQ[DoubleScalar.POSITIVE_INFINITY] == false
   * FiniteScalarQ[DoubleScalar.NEGATIVE_INFINITY] == false
   * FiniteScalarQ[NaN] == false
   * 
   * @return whether this scalar is finite
   * @see FiniteScalarQ */
  boolean isFinite();
}
