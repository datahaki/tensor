// code by jph
package ch.alpine.tensor.api;

import java.util.function.Predicate;
import java.util.function.UnaryOperator;

import ch.alpine.tensor.ComplexScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.lie.Quaternion;

/** marker interface to indicate that a scalar consist of
 * several sub-scalars.
 * 
 * Quote from https://www.merriam-webster.com/dictionary/multiplex
 * "Definition of multiplex
 * 1: many, multiple
 * 2: being or relating to a system of transmitting several messages
 * or signals simultaneously on the same circuit or channel"
 * 
 * Examples:
 * {@link ComplexScalar} consists of 2 scalar components, namely
 * real and imaginary part.
 * {@link Quaternion} consists of 4 scalar components. */
public interface MultiplexScalar {
  /** Example: functionality is used for rounding operations,
   * as well as mapping from exact to numerical precision.
   * 
   * @param unaryOperator
   * @return scalar with given unaryOperator applied to all scalar
   * components */
  Scalar eachMap(UnaryOperator<Scalar> unaryOperator);

  /** Example: functionality is used to check whether all
   * scalar components are in exact precision.
   * 
   * @param predicate
   * @return whether all scalar components satify given predicate */
  boolean allMatch(Predicate<Scalar> predicate);
}
