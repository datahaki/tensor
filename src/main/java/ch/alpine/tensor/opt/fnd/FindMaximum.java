package ch.alpine.tensor.opt.fnd;

import ch.alpine.tensor.api.ScalarUnaryOperator;
import ch.alpine.tensor.mat.Tolerance;
import ch.alpine.tensor.sca.Chop;

public class FindMaximum extends FindBase {
  public static FindMaximum of(ScalarUnaryOperator function) {
    return new FindMaximum(function, Tolerance.CHOP);
  }
  // ---

  private FindMaximum(ScalarUnaryOperator function, Chop chop) {
    super(function, chop, true);
  }
}
