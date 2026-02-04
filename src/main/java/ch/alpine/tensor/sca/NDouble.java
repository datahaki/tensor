// code by jph
package ch.alpine.tensor.sca;

import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.api.NInterface;

/* package */ class NDouble extends NBase {
  @Override
  protected Scalar numeric(NInterface nInterface) {
    return nInterface.n();
  }
}
