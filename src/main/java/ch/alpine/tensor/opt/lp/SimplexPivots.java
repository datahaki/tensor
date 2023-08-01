// code by jph
package ch.alpine.tensor.opt.lp;

import java.util.Objects;

import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Scalars;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.mat.Tolerance;
import ch.alpine.tensor.sca.Sign;

/** implementation is specific for
 * objective == minimize */
public enum SimplexPivots implements SimplexPivot {
  /** nonbasic gradient method, or "steepest decent policy"
   * 
   * <p>pivot designed by G. B. Dantzig that works decent for most practical problems
   * but performs poorly on the Klee-Minty cube */
  NONBASIC_GRADIENT {
    @SuppressWarnings("null")
    @Override // from SimplexPivot
    public int get(Tensor tab, int j, int n) {
      Integer pivot = null;
      Scalar min = null;
      int m = tab.length() - 1;
      for (int i = 0; i < m; ++i) {
        Scalar tab_ij = tab.Get(i, j);
        if (Sign.isPositive(Tolerance.CHOP.apply(tab_ij))) {
          Scalar ratio = tab.Get(i, n).divide(tab_ij);
          if (Objects.isNull(min) || Scalars.lessThan(ratio, min)) {
            min = ratio;
            pivot = i;
          }
        }
      }
      return pivot;
    }
  },
  /* p.50 greatest increment method NOT YET IMPLEMENTED */
  /* p.50 all variable gradient method NOT YET IMPLEMENTED */
}
