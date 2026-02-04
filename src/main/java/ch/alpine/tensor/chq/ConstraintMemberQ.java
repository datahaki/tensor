// code by jph
package ch.alpine.tensor.chq;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;

import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Throw;
import ch.alpine.tensor.alg.Dimensions;
import ch.alpine.tensor.sca.Chop;

public abstract class ConstraintMemberQ implements MemberQ, Serializable {
  private final int rank;
  private final Chop chop;

  public ConstraintMemberQ(int rank, Chop chop) {
    this.rank = rank;
    this.chop = Objects.requireNonNull(chop);
  }

  @Override
  public final boolean isMember(Tensor tensor) {
    Dimensions dimensions = new Dimensions(tensor);
    return dimensions.maxDepth() == rank //
        && dimensions.isArrayWith(this::isArrayWith) //
        && chop.allZero(constraint(tensor));
  }

  @Override
  public final Tensor requireMember(Tensor tensor) {
    if (isMember(tensor))
      return tensor;
    throw new Throw(tensor);
  }

  /** @param list
   * @return
   * @implNote override for instance to check that all dimensions are equal */
  public boolean isArrayWith(List<Integer> list) {
    return true;
  }

  /** @param tensor with appropriate array dimensions
   * @return result that determines subject to {@link Chop#allZero(Tensor)}
   * whether tensor is member */
  public abstract Tensor constraint(Tensor tensor);
}
