// code by jph
package ch.ethz.idsc.tensor.api;

import ch.ethz.idsc.tensor.ComplexScalar;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.TensorRuntimeException;
import ch.ethz.idsc.tensor.io.StringScalar;
import ch.ethz.idsc.tensor.lie.Quaternion;

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
