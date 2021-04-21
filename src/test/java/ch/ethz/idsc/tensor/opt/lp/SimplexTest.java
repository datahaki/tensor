// code by jph
package ch.ethz.idsc.tensor.opt.lp;

import java.util.stream.Stream;

import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Array;
import ch.ethz.idsc.tensor.opt.lp.LinearProgram.ConstraintType;
import ch.ethz.idsc.tensor.opt.lp.LinearProgram.Objective;
import ch.ethz.idsc.tensor.opt.lp.LinearProgram.RegionType;
import ch.ethz.idsc.tensor.red.KroneckerDelta;
import junit.framework.TestCase;

public class SimplexTest extends TestCase {
  /** Quote from "On the Uniqueness of Solutions to Linear Programs" by G. Appa:
   * 
   * four variables actually take value zero at this basis, there are
   * 4C2 = 6 possible simplex tableaux representing the same extra LPP.
   * unique feasible solution, viz x2 = 1 and xj = 0 for j != 2.
   * 
   * It is obvious that because Cj = 0 for all j,
   * the reduced costs in any basic solution to this problem are always zero,
   * and every feasible solution is optimal */
  public void testUnique() {
    Tensor c = Array.zeros(2);
    Tensor A = Tensors.matrixInt(new int[][] { { 3, -1 }, { -3, 2 }, { 1, -1 } });
    Tensor b = Tensors.vector(-1, 2, -1);
    LinearProgram linearProgram = LinearProgram.of(Objective.MAX, c, ConstraintType.LESS_EQUALS, A, b, RegionType.NON_NEGATIVE);
    Tensor x = LinearProgramming.of(linearProgram);
    assertEquals(x, Tensors.vector(0, 1));
  }

  public void testUnique2() {
    Tensor c = Array.zeros(2);
    Tensor A = Tensors.matrixInt(new int[][] { { 3, -1 }, { -3, 2 }, { 1, -1 } });
    Tensor b = Tensors.vector(-1, 2, -1);
    LinearProgram linearProgram = //
        LinearProgram.of(Objective.MIN, c, ConstraintType.LESS_EQUALS, A, b, RegionType.NON_NEGATIVE);
    Tensor x = LinearProgramming.of(linearProgram);
    assertEquals(x, Tensors.vector(0, 1));
  }

  private static Tensor fromString(String... string) {
    return Tensor.of(Stream.of(string).map(Tensors::fromString));
  }

  /** problem taken from
   * An Additive Eigenvalue Problem of Physics
   * Related to Linear Programming
   * by WEIREN CHOU and R. J. DUFFIN */
  public void testAEV() {
    Tensor m = fromString( //
        "{1, 0, 0, 0, 0}", //
        "{1, 1,-1, 0, 0}", //
        "{1, 1, 0,-1, 0}", //
        "{1, 1, 0, 0,-1}", //
        // ---
        "{1,-1, 1, 0, 0}", //
        "{1, 0, 0, 0, 0}", //
        "{1, 0, 1,-1, 0}", //
        "{1, 0, 1, 0,-1}", //
        // ---
        "{1,-1, 0, 1, 0}", //
        "{1, 0,-1, 1, 0}", //
        "{1, 0, 0, 0, 0}", //
        "{1, 0, 0, 1,-1}", //
        // ---
        "{1,-1, 0, 0, 1}", //
        "{1, 0,-1, 0, 1}", //
        "{1, 0, 0,-1, 1}", //
        "{1, 0, 0, 0, 0}" //
    );
    Tensor b = Tensors.vector(8, 7, 9, 13, 6, 10, 5, 12, 14, 15, 9, 11, 9, 8, 4, 7); //
    Tensor c = Tensors.vector(i -> KroneckerDelta.of(i, 0), 5);
    LinearProgram linearProgram = LinearProgram.of(Objective.MAX, c, ConstraintType.LESS_EQUALS, m, b, RegionType.NON_NEGATIVE);
    Tensor x = LinearProgramming.of(linearProgram);
    Tensor X51 = Tensors.vector(6.5, 0.5, 0, 2.5, 0);
    Tensor X52 = Tensors.fromString("{13/2, 1/2, 0, 6, 3/2}");
    assertEquals(c.dot(x), c.dot(X51));
    assertEquals(c.dot(x), c.dot(X52));
    linearProgram.requireFeasible(X51);
    linearProgram.requireFeasible(X52);
  }

  /** problem taken from
   * Combinatorial Optimization
   * by Papadimitriou and Steiglitz
   * pp. 30 */
  public void testP30() {
    Tensor m = fromString( //
        "{1, 1, 1, 1, 0, 0, 0}", //
        "{1, 0, 0, 0, 1, 0, 0}", //
        "{0, 0, 1, 0, 0, 1, 0}", //
        "{0, 3, 1, 0, 0, 0, 1}" //
    );
    Tensor b = Tensors.vector(4, 2, 3, 6);
    Tensor c = Tensors.vector(0, 2, 0, 1, 0, 0, 5);
    LinearProgram linearProgram = //
        LinearProgram.of(Objective.MIN, c, ConstraintType.EQUALS, m, b, RegionType.NON_NEGATIVE);
    Tensor x = LinearProgramming.of(linearProgram);
    Tensor X = Tensors.vector(0, 1, 3, 0, 2, 0, 0);
    assertEquals(x, X);
  }

  /** problem taken from
   * Combinatorial Optimization
   * by Papadimitriou and Steiglitz
   * pp. 51 */
  public void testCyclingP51() {
    Tensor m = fromString( //
        "{1/4, -8,-1  , 9}", //
        "{1/2,-12,-1/2, 3}", //
        "{0  ,  0, 1  , 0}" //
    );
    Tensor b = Tensors.vector(0, 0, 1);
    Tensor c = Tensors.fromString("{-3/4, 20, -1/2, 6}");
    LinearProgram linearProgram = LinearProgram.of(Objective.MIN, c, ConstraintType.LESS_EQUALS, m, b, RegionType.NON_NEGATIVE);
    Tensor x = LinearProgramming.of(linearProgram);
    Tensor X = Tensors.vector(1, 0, 1, 0);
    assertEquals(x, X);
  }
}
