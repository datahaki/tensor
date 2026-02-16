// code by jph
package ch.alpine.tensor.lie;

import java.util.List;

import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.alg.Join;
import ch.alpine.tensor.alg.Transpose;
import ch.alpine.tensor.chq.ZeroDefectArrayQ;
import ch.alpine.tensor.mat.Tolerance;
import ch.alpine.tensor.sca.Chop;

public class RiemannCurvatureQ extends ZeroDefectArrayQ {
  public static final ZeroDefectArrayQ INSTANCE = new RiemannCurvatureQ(Tolerance.CHOP);

  public RiemannCurvatureQ(Chop chop) {
    super(4, chop);
  }

  @Override // from ZeroDefectArrayQ
  protected boolean isArrayWith(List<Integer> list) {
    return list.stream().distinct().count() == 1;
  }

  @Override // from ZeroDefectArrayQ
  public Tensor defect(Tensor r) {
    return Join.of( //
        Transpose.of(r, 1, 0).add(r), //
        Transpose.of(r, 0, 1, 3, 2).add(r), //
        Transpose.of(r, 2, 3, 0, 1).subtract(r), //
        BianchiIdentity.INSTANCE.defect(r));
  }
}
