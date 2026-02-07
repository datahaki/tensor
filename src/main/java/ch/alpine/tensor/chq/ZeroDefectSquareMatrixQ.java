// code by jph
package ch.alpine.tensor.chq;

import java.util.List;

import ch.alpine.tensor.sca.Chop;

public abstract class ZeroDefectSquareMatrixQ extends ZeroDefectArrayQ {
  public ZeroDefectSquareMatrixQ(Chop chop) {
    super(2, chop);
  }

  @Override // from ZeroDefectArrayQ
  public final boolean isArrayWith(List<Integer> list) {
    return list.get(0).equals(list.get(1));
  }
}
