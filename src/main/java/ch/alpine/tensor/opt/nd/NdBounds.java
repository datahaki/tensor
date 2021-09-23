// code by jph
package ch.alpine.tensor.opt.nd;

import ch.alpine.tensor.RationalScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.sca.Clip;
import ch.alpine.tensor.sca.Clips;

public class NdBounds {
  final Tensor lBounds;
  final Tensor uBounds;

  public NdBounds(Tensor lBounds, Tensor uBounds) {
    this.lBounds = lBounds.copy();
    this.uBounds = uBounds.copy();
  }

  public Tensor lBounds() {
    return lBounds.copy();
  }

  public Tensor uBounds() {
    return uBounds.copy();
  }

  public Scalar mean(int index) {
    return lBounds.Get(index).add(uBounds.Get(index)).multiply(RationalScalar.HALF);
  }

  public Clip clip(int index) {
    return Clips.interval( //
        lBounds.Get(index), //
        uBounds.Get(index));
  }
}
