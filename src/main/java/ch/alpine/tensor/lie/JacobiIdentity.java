// code by jph
package ch.alpine.tensor.lie;

import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.chq.ZeroDefectArrayQ;
import ch.alpine.tensor.mat.Tolerance;
import ch.alpine.tensor.sca.Chop;

/** ad tensor of Lie-algebra
 * 
 * non-linear constraint */
public class JacobiIdentity extends ZeroDefectArrayQ {
  public static final ZeroDefectArrayQ INSTANCE = new JacobiIdentity(Tolerance.CHOP);

  public JacobiIdentity(Chop chop) {
    super(3, chop);
  }

  @Override // from ZeroDefectArrayQ
  public Tensor defect(Tensor ad) {
    return BianchiIdentity.INSTANCE.defect(ad.dot(ad));
  }
}
