// code by jph
package ch.alpine.tensor.qty;

import ch.alpine.tensor.Scalar;

/** marker interface for implementations of {@link Scalar} to delegate
 * the addition of an instance of {@link Quantity} to this scalar */
public interface QuantityCompatibleScalar extends Scalar {
  // ---
}
