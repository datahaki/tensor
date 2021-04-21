// code by jph
package ch.ethz.idsc.tensor.opt.lp;

import ch.ethz.idsc.tensor.Tensor;

/** linear programming solution for small scale problems.
 * The implementation has only been tested on a few cases.
 * 
 * <p>implementation uses traditional simplex algorithm that performs poorly on Klee-Minty cube
 * 
 * <p>syntax mostly compatible to MATLAB::linprog
 * 
 * <p>inspired by
 * <a href="https://reference.wolfram.com/language/ref/LinearProgramming.html">LinearProgramming</a> */
public enum LinearProgramming {
  ;
  public static Tensor of(LinearProgram linearProgram, SimplexPivot simplexPivot) {
    LinearProgram lp_equality = linearProgram.equality();
    Tensor x = SimplexMethod.of(lp_equality.minCost(), lp_equality.A, lp_equality.b, simplexPivot);
    return linearProgram.requireFeasible(x.extract(0, lp_equality.variables));
  }
}
