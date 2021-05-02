// code by jph
package ch.alpine.tensor.opt.lp;

import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import junit.framework.Assert;

/* package */ enum TestHelper {
  ;
  public static Scalar check(LinearProgram lpp, LinearProgram lpd) {
    Tensor sp = SimplexCorners.of(lpp);
    Tensor sd = SimplexCorners.of(lpd);
    Assert.assertEquals(sp.dot(lpp.c).Get(0), sd.dot(lpd.c).Get(0));
    return sp.dot(lpp.c).Get(0);
  }

  /** TODO primal and dual do not always correspond, namely wherever check_both == false
   * 
   * @param lpp
   * @param check_both
   * @return */
  public static Scalar check(LinearProgram lpp, boolean check_both) {
    LinearProgram lpd = lpp.toggle();
    Scalar optimal_value = check(lpp, lpd);
    // ---
    Tensor xp = LinearProgramming.of(lpp);
    Tensor xd = LinearProgramming.of(lpd);
    if (check_both)
      Assert.assertEquals(xp.dot(lpp.c), xd.dot(lpd.c));
    else {
      boolean ep = xp.dot(lpp.c).equals(optimal_value);
      boolean ed = xd.dot(lpd.c).equals(optimal_value);
      Assert.assertTrue(ep || ed);
    }
    return optimal_value;
  }
}
