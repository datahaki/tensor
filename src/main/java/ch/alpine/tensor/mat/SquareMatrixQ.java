// code by jph
package ch.alpine.tensor.mat;

import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.chq.ConstraintSquareMatrixQ;
import ch.alpine.tensor.chq.MemberQ;
import ch.alpine.tensor.sca.Chop;

/** consistent with Mathematica, in particular SquareMatrixQ[{}] == false
 * 
 * <p>inspired by
 * <a href="https://reference.wolfram.com/language/ref/SquareMatrixQ.html">SquareMatrixQ</a> */
public enum SquareMatrixQ {
  ;
  public static final MemberQ INSTANCE = new ConstraintSquareMatrixQ(Chop.NONE) {
    @Override
    public Tensor defect(Tensor tensor) {
      return RealScalar.ZERO;
    }
  };
}
