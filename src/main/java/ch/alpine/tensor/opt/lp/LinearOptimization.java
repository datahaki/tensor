// code by jph
package ch.alpine.tensor.opt.lp;

import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.opt.lp.LinearProgram.ConstraintType;

/** linear programming solution for small scale problems.
 * The implementation has only been tested on a few cases.
 * 
 * <p>implementation uses traditional simplex algorithm that performs poorly on Klee-Minty cube
 * 
 * <p>syntax mostly compatible to MATLAB::linprog
 * 
 * <p>inspired by
 * <a href="https://reference.wolfram.com/language/ref/LinearOptimization.html">LinearOptimization</a> */
public enum LinearOptimization {
  ;
  /** @param linearProgram
   * @param simplexPivot
   * @return */
  public static Tensor of(LinearProgram linearProgram, SimplexPivot simplexPivot) {
    LinearProgram lp_standard = linearProgram.standard();
    Tensor x = linearProgram.constraintType.equals(ConstraintType.LESS_EQUALS) && //
        StaticHelper.isNonNegative(linearProgram.b) //
            ? SimplexMethod.of(lp_standard, simplexPivot)
            : SimplexMethod.of(lp_standard.minObjective(), lp_standard.A, lp_standard.b, simplexPivot);
    Tensor x_extract = x.extract(0, lp_standard.var_count());
    return linearProgram.requireFeasible(x_extract);
  }

  /** @param linearProgram
   * @return */
  public static Tensor of(LinearProgram linearProgram) {
    return of(linearProgram, SimplexPivots.NONBASIC_GRADIENT);
  }
}
