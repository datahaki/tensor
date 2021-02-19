// code by jph
package ch.ethz.idsc.tensor.api;

import ch.ethz.idsc.tensor.ComplexScalar;
import ch.ethz.idsc.tensor.Quaternion;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.TensorRuntimeException;
import ch.ethz.idsc.tensor.io.StringScalar;

/** The absolute value of {@link RealScalar}, {@link ComplexScalar}, or {@link Quaternion} is always
 * an instance of {@link RealScalar}. */
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
