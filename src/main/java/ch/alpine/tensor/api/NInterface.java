// code by jph
package ch.alpine.tensor.api;

import java.math.MathContext;

import ch.alpine.tensor.DecimalScalar;
import ch.alpine.tensor.DoubleScalar;
import ch.alpine.tensor.Scalar;

/** interface may be implemented by {@link Scalar}
 * to support the conversion to numeric precision */
public interface NInterface {
  /** @return numerical approximation of this scalar as a {@link DoubleScalar}
   * for instance 1/3 is converted to 1.0/3.0 == 0.3333... */
  Scalar n();

  /** @param mathContext
   * @return this instance in the given context as a {@link DecimalScalar} */
  Scalar n(MathContext mathContext);
}
