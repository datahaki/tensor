// code by jph
package ch.alpine.tensor;

import java.util.function.Predicate;
import java.util.function.UnaryOperator;

import ch.alpine.tensor.lie.Quaternion;
import ch.alpine.tensor.qty.Quantity;
import ch.alpine.tensor.qty.Unit;

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
 * {@link Quaternion} consists of 4 scalar components.
 * {@link Quantity} consists of 1 scalar component and a {@link Unit}. */
public abstract class MultiplexScalar extends AbstractScalar {
  /** Example: functionality is used for rounding operations,
   * as well as mapping from exact to numerical precision.
   * 
   * @param unaryOperator
   * @return scalar with given unaryOperator applied to all scalar
   * components */
  public abstract Scalar eachMap(UnaryOperator<Scalar> unaryOperator);

  /** Example: functionality is used to check whether all
   * scalar components are in exact precision.
   * 
   * @param predicate
   * @return whether all scalar components satisfy given predicate */
  public abstract boolean allMatch(Predicate<Scalar> predicate);

  @Override // from Scalar
  public final Number number() {
    throw new Throw(this);
  }
}
