// code by jph
package ch.alpine.tensor.api;

import java.io.Serializable;
import java.util.function.UnaryOperator;

import ch.alpine.tensor.Scalar;

/** serializable interface for functions that map a {@link Scalar} to another {@link Scalar} */
@FunctionalInterface
public interface ScalarUnaryOperator extends UnaryOperator<Scalar>, Serializable {
  // ---
}
