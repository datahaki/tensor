// code by jph
package ch.ethz.idsc.tensor.opt.lp;

import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import junit.framework.Assert;

/* package */ enum TestHelper {
  ;
  public static Scalar check(LinearProgram lpp) {
    LinearProgram lpd = lpp.dual();
    // ---
    Tensor sp = SimplexCorners.of(lpp);
    Tensor sd = SimplexCorners.of(lpd);
    Assert.assertEquals(sp.dot(lpp.c), sd.dot(lpd.c));
    // ---
    Tensor xp = LinearProgramming.of(lpp);
    Tensor xd = LinearProgramming.of(lpd);
    Scalar optimal_value = (Scalar) xp.dot(lpp.c);
    Assert.assertEquals(optimal_value, xd.dot(lpd.c));
    return optimal_value;
  }
}
