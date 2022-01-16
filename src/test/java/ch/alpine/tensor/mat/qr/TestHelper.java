// code by jph
package ch.alpine.tensor.mat.qr;

import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.mat.re.Inverse;
import ch.alpine.tensor.mat.re.LinearSolve;
import ch.alpine.tensor.sca.Chop;

/* package */ enum TestHelper {
  ;
  public static void checkPInv(Tensor pInv, Tensor r, Tensor qInv) {
    Chop._08.requireClose(pInv, LinearSolve.of(r, qInv));
    Chop._08.requireClose(pInv, Inverse.of(r).dot(qInv));
  }
}
