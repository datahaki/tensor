// code by jph
package ch.alpine.tensor.api;

import ch.alpine.tensor.ComplexScalar;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.TensorRuntimeException;
import ch.alpine.tensor.io.StringScalar;
import ch.alpine.tensor.lie.Quaternion;

/** classes that implement {@link AbsInterface} should also implement
 * {@link SignInterface} and achieve the identity
 * <pre>
 * scalar == Sign[scalar] * Abs[scalar]
 * </pre>
 * 
 * The absolute value of {@link RealScalar}, {@link ComplexScalar}, or {@link Quaternion}
 * is a non-negative instance of {@link RealScalar}. */
public interface AbsInterface {
  /** absolute value
   * 
   * <pre>
   * Abs[x] == Sqrt[x * Conjugate[x]]
   * </pre>
   * 
   * @return non-negative distance from zero of this
   * @throws TensorRuntimeException if absolute value is not defined
   * in the case of {@link StringScalar} for instance */
  Scalar abs();

  /** absolute value squared
   * 
   * <pre>
   * AbsSquared[x] == x * Conjugate[x]
   * </pre>
   * 
   * @return multiply(conjugate())
   * @see ConjugateInterface */
  Scalar absSquared();
}
