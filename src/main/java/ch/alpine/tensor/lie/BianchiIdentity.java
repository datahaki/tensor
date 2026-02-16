// code by jph
package ch.alpine.tensor.lie;

import java.util.List;

import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.alg.Transpose;
import ch.alpine.tensor.chq.ZeroDefectArrayQ;
import ch.alpine.tensor.mat.Tolerance;
import ch.alpine.tensor.sca.Chop;

/** check whether tensor satisfies the Bianchi identity
 * 
 * @param rie tensor of rank 4
 * @return array of all-zeros if rie is a Riemannian-curvature tensor */
public class BianchiIdentity extends ZeroDefectArrayQ {
  public static final ZeroDefectArrayQ INSTANCE = new BianchiIdentity(Tolerance.CHOP);

  public BianchiIdentity(Chop chop) {
    super(4, chop);
  }

  @Override // from ZeroDefectArrayQ
  protected boolean isArrayWith(List<Integer> list) {
    return list.stream().distinct().count() == 1;
  }

  @Override // from ZeroDefectArrayQ
  public Tensor defect(Tensor rie) {
    return rie // == Transpose.of(rie, 0, 1, 2, 3) // identity
        .add(Transpose.of(rie, 0, 2, 3, 1)) //
        .add(Transpose.of(rie, 0, 3, 1, 2));
  }
}
