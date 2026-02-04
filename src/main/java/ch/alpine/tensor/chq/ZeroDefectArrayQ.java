// code by jph
package ch.alpine.tensor.chq;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;

import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Throw;
import ch.alpine.tensor.alg.Dimensions;
import ch.alpine.tensor.ext.Integers;
import ch.alpine.tensor.sca.Chop;

public abstract class ZeroDefectArrayQ implements MemberQ, Serializable {
  private final int rank;
  private final Chop chop;

  /** @param rank non-negative
   * @param chop */
  public ZeroDefectArrayQ(int rank, Chop chop) {
    this.rank = Integers.requirePositiveOrZero(rank);
    this.chop = Objects.requireNonNull(chop);
  }

  @Override // from MemberQ
  public final boolean isMember(Tensor tensor) {
    Dimensions dimensions = new Dimensions(tensor);
    return dimensions.list().size() == rank //
        && dimensions.isArrayWith(this::isArrayWith) //
        && chop.allZero(defect(tensor));
  }

  @Override // from MemberQ
  public final Tensor requireMember(Tensor tensor) {
    if (isMember(tensor))
      return tensor;
    throw new Throw(tensor);
  }

  /** @param list
   * @return
   * @implNote override for instance to check that all dimensions are equal */
  protected boolean isArrayWith(List<Integer> list) {
    return true;
  }

  /** @param tensor with appropriate array dimensions
   * @return defect that determines whether tensor is member subject to
   * {@link Chop#allZero(Tensor)} */
  public abstract Tensor defect(Tensor tensor);
}
