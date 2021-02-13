// code by jph
package ch.ethz.idsc.tensor.opt.nd;

import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.nrm.VectorNormInterface;

public interface NdCenterInterface extends VectorNormInterface {
  /** @return center */
  Tensor center();
}