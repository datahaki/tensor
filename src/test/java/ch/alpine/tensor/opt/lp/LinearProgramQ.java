// code by jph
package ch.alpine.tensor.opt.lp;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;

public record LinearProgramQ(LinearProgram lpp) {
  public Scalar check(LinearProgram lpd) {
    Tensor sp = SimplexCorners.of(lpp);
    Tensor sd = SimplexCorners.of(lpd);
    assertEquals(sp.dot(lpp.c).Get(0), sd.dot(lpd.c).Get(0));
    return sp.dot(lpp.c).Get(0);
  }

  /** TODO TENSOR LP primal and dual do not always correspond, namely wherever check_both == false
   * 
   * @param lpp
   * @param check_both
   * @return */
  public Scalar check(boolean check_both) {
    LinearProgram lpd = lpp.toggle();
    Scalar optimal_value = check(lpd);
    // ---
    Tensor xp = LinearOptimization.of(lpp);
    Tensor xd = LinearOptimization.of(lpd);
    if (check_both)
      assertEquals(xp.dot(lpp.c), xd.dot(lpd.c));
    else {
      boolean ep = xp.dot(lpp.c).equals(optimal_value);
      boolean ed = xd.dot(lpd.c).equals(optimal_value);
      assertTrue(ep || ed);
    }
    return optimal_value;
  }
}
