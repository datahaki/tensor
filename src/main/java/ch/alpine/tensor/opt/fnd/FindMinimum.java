// code by jph
package ch.alpine.tensor.opt.fnd;

import ch.alpine.tensor.api.ScalarUnaryOperator;
import ch.alpine.tensor.mat.Tolerance;
import ch.alpine.tensor.sca.Chop;

/** inspired by
 * <a href="https://reference.wolfram.com/language/ref/FindMinimum.html">FindMinimum</a> */
public class FindMinimum extends FindBase {
  public static FindMinimum of(ScalarUnaryOperator function) {
    return new FindMinimum(function, Tolerance.CHOP);
  }
  // ---

  private FindMinimum(ScalarUnaryOperator function, Chop chop) {
    super(function, chop, false);
  }
}
