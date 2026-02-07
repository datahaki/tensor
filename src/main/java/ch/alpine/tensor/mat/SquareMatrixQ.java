// code by jph
package ch.alpine.tensor.mat;

import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.chq.ZeroDefectArrayQ;
import ch.alpine.tensor.chq.ZeroDefectSquareMatrixQ;
import ch.alpine.tensor.sca.Chop;

/** consistent with Mathematica, in particular SquareMatrixQ[{}] == false
 * 
 * <p>inspired by
 * <a href="https://reference.wolfram.com/language/ref/SquareMatrixQ.html">SquareMatrixQ</a> */
public enum SquareMatrixQ {
  ;
  public static final ZeroDefectArrayQ INSTANCE = new ZeroDefectSquareMatrixQ(Chop.NONE) {
    @Override
    public Tensor defect(Tensor tensor) {
      return RealScalar.ZERO;
    }
  };
}
