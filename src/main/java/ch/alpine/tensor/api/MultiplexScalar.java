// code by jph
package ch.alpine.tensor.api;

import java.util.function.Predicate;
import java.util.function.UnaryOperator;

import ch.alpine.tensor.Scalar;

public interface MultiplexScalar {
  /** @param unaryOperator
   * @return */
  Scalar eachMap(UnaryOperator<Scalar> unaryOperator);

  /** @param predicate
   * @return */
  boolean allMatch(Predicate<Scalar> predicate);
}
