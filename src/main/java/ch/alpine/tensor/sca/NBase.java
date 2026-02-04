// code by jph
package ch.alpine.tensor.sca;

import java.util.Objects;

import ch.alpine.tensor.MultiplexScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.api.NInterface;
import ch.alpine.tensor.api.ScalarUnaryOperator;

/* package */ abstract class NBase implements ScalarUnaryOperator {
  @Override
  public final Scalar apply(Scalar scalar) {
    if (scalar instanceof NInterface nInterface)
      return numeric(nInterface);
    if (scalar instanceof MultiplexScalar multiplexScalar)
      return multiplexScalar.eachMap(this);
    return Objects.requireNonNull(scalar);
  }

  /** @param nInterface
   * @return scalar in numeric precision from given nInterface */
  protected abstract Scalar numeric(NInterface nInterface);
}
