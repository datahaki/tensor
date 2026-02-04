// code by jph
package ch.alpine.tensor.mat;

import java.io.Serializable;

import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.alg.Dimensions;
import ch.alpine.tensor.chq.MemberQ;

/** consistent with Mathematica, in particular SquareMatrixQ[{}] == false
 * 
 * <p>inspired by
 * <a href="https://reference.wolfram.com/language/ref/SquareMatrixQ.html">SquareMatrixQ</a> */
public class SquareMatrixQ implements MemberQ, Serializable {
  public static final MemberQ INSTANCE = new SquareMatrixQ();

  /** @param tensor
   * @return true if tensor is a square matrix, otherwise false */
  @Override
  public boolean isMember(Tensor tensor) {
    return new Dimensions(tensor).isArrayWith(list -> list.size() == 2 && list.get(0).equals(list.get(1)));
  }
}
