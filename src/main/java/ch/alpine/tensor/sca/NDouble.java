// code by jph
package ch.alpine.tensor.sca;

import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.api.NInterface;

/* package */ class NDouble extends N {
  static final N INSTANCE = new NDouble();

  private NDouble() {
    // ---
  }

  @Override
  protected Scalar numeric(NInterface nInterface) {
    return nInterface.n();
  }
}
