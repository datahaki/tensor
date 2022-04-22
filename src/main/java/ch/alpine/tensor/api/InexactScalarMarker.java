// code by jph
package ch.alpine.tensor.api;

import ch.alpine.tensor.ExactScalarQ;
import ch.alpine.tensor.Scalar;

/** a {@link Scalar} may implement the interface to signal that the value is in exact precision.
 * 
 * <p>a {@link Scalar} that does not implement {@link InexactScalarMarker} is assumed to
 * <em>not</em> represent an exact quantity by {@link ExactScalarQ}. */
public interface InexactScalarMarker {
  // ---
}
