// code by jph
package ch.alpine.tensor.api;

import java.io.Serializable;
import java.util.function.BinaryOperator;

import ch.alpine.tensor.Scalar;

/** interface for serializable functions that maps two {@link Scalar}s to a {@link Scalar} */
@FunctionalInterface
public interface ScalarBinaryOperator extends BinaryOperator<Scalar>, Serializable {
  // ---
}
