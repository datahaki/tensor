// code by jph
package ch.alpine.tensor.api;

import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.qty.Quantity;
import ch.alpine.tensor.sca.Ceiling;
import ch.alpine.tensor.sca.Floor;
import ch.alpine.tensor.sca.Round;

/** An implementation of {@link Scalar} may implement {@link RoundingInterface}
 * to support the use of {@link Ceiling}, {@link Floor}, and {@link Round}.
 * 
 * Examples of are {@link RealScalar}, and {@link Quantity}. */
public interface RoundingInterface {
  /** @return equal or higher scalar with integer components */
  Scalar ceiling();

  /** @return equal or lower scalar with integer components */
  Scalar floor();

  /** @return closest scalar with integer components */
  Scalar round();
}
