// code by jph
package ch.alpine.tensor.api;

import java.io.Serializable;
import java.util.function.UnaryOperator;

import ch.alpine.tensor.Scalar;

/** interface for serializable functions that map a {@link Scalar} to another {@link Scalar} */
@FunctionalInterface
public interface ScalarUnaryOperator extends UnaryOperator<Scalar>, Serializable {
  // ---
}
