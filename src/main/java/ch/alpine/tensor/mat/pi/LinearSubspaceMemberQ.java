// code by jph
package ch.alpine.tensor.mat.pi;

import java.util.List;

import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.alg.Array;
import ch.alpine.tensor.alg.Dimensions;
import ch.alpine.tensor.chq.ZeroDefectArrayQ;
import ch.alpine.tensor.sca.Chop;

public class LinearSubspaceMemberQ extends ZeroDefectArrayQ {
  public static ZeroDefectArrayQ of(LinearSubspace linearSubspace, Chop chop) {
    List<Integer> list = Dimensions.of(linearSubspace.apply(Array.zeros(linearSubspace.dimensions())));
    return new LinearSubspaceMemberQ(linearSubspace, list.size(), chop);
  }

  private final LinearSubspace linearSubspace;

  private LinearSubspaceMemberQ(LinearSubspace linearSubspace, int rank, Chop chop) {
    super(rank, chop);
    this.linearSubspace = linearSubspace;
  }

  @Override
  public Tensor defect(Tensor tensor) {
    return linearSubspace.projection(tensor).subtract(tensor);
  }
}
