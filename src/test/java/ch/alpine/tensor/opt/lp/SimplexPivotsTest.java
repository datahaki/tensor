// code by jph
package ch.alpine.tensor.opt.lp;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;
import java.util.NavigableMap;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.RationalScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.alg.Array;
import ch.alpine.tensor.ext.Serialization;
import ch.alpine.tensor.opt.lp.LinearProgram.ConstraintType;
import ch.alpine.tensor.opt.lp.LinearProgram.Objective;
import ch.alpine.tensor.opt.lp.LinearProgram.Variables;
import ch.alpine.tensor.qty.Quantity;
import ch.alpine.tensor.red.KroneckerDelta;
import ch.alpine.tensor.sca.Chop;

class SimplexPivotsTest {
  @Test
  void testSerializable() throws ClassNotFoundException, IOException {
    for (SimplexPivot simplexPivot : SimplexPivots.values())
      Serialization.copy(simplexPivot);
  }

  @Test
  void testP14() {
    // x >= 0 that minimizes c.x subject to m.x <= b
    Tensor c = Tensors.fromString("{4[USD], 5[USD]}");
    Tensor m = Tensors.fromString("{{12[Wood], 8[Wood]}, {6[Iron], 9[Iron]}}");
    Tensor b = Tensors.fromString("{96[Wood], 72[Iron]}");
    Tensor sol = Tensors.fromString("{48/10, 48/10}");
    Tensor res = m.dot(sol).subtract(b);
    Chop.NONE.requireAllZero(res);
    c.dot(sol);
    NavigableMap<Scalar, Tensor> navigableMap = SimplexCorners.of(c, m, b, true);
    Tensor xs = navigableMap.get(Quantity.of(RationalScalar.of(216, 5), "USD"));
    assertEquals(xs, Tensors.fromString("{{24/5, 24/5}}"));
    Tensor x = xs.get(0);
    Tensor mx = m.dot(x);
    assertEquals(mx, b);
    LinearProgram lpp = LinearProgram.of(Objective.MAX, c, ConstraintType.LESS_EQUALS, m, b, Variables.NON_NEGATIVE);
    Tensor solp = SimplexCorners.of(lpp);
    assertEquals(xs, solp);
    LinearProgram lpd = lpp.toggle();
    Tensor sold = SimplexCorners.of(lpd);
    assertEquals(sold, Tensors.fromString("{{1/10[USD*Wood^-1], 7/15[Iron^-1*USD]}}"));
  }

  /** Quote from "On the Uniqueness of Solutions to Linear Programs" by G. Appa:
   * 
   * four variables actually take value zero at this basis, there are
   * 4C2 = 6 possible simplex tableaux representing the same extra LPP.
   * unique feasible solution, viz x2 = 1 and xj = 0 for j != 2.
   * 
   * It is obvious that because Cj = 0 for all j,
   * the reduced costs in any basic solution to this problem are always zero,
   * and every feasible solution is optimal */
  @Test
  void testUnique() {
    Tensor c = Array.zeros(2);
    Tensor A = Tensors.matrixInt(new int[][] { { 3, -1 }, { -3, 2 }, { 1, -1 } });
    Tensor b = Tensors.vector(-1, 2, -1);
    LinearProgram linearProgram = LinearProgram.of(Objective.MAX, c, ConstraintType.LESS_EQUALS, A, b, Variables.NON_NEGATIVE);
    Tensor x = LinearProgramming.of(linearProgram);
    assertEquals(x, Tensors.vector(0, 1));
  }

  @Test
  void testUnique2() {
    Tensor c = Array.zeros(2);
    Tensor A = Tensors.matrixInt(new int[][] { { 3, -1 }, { -3, 2 }, { 1, -1 } });
    Tensor b = Tensors.vector(-1, 2, -1);
    LinearProgram linearProgram = //
        LinearProgram.of(Objective.MIN, c, ConstraintType.LESS_EQUALS, A, b, Variables.NON_NEGATIVE);
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
  @Test
  void testAEV() {
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
    LinearProgram linearProgram = LinearProgram.of(Objective.MAX, c, ConstraintType.LESS_EQUALS, m, b, Variables.NON_NEGATIVE);
    Tensor x = LinearProgramming.of(linearProgram);
    Tensor x51 = Tensors.vector(6.5, 0.5, 0, 2.5, 0);
    Tensor x52 = Tensors.fromString("{13/2, 1/2, 0, 6, 3/2}");
    assertEquals(c.dot(x), c.dot(x51));
    assertEquals(c.dot(x), c.dot(x52));
    linearProgram.requireFeasible(x51);
    linearProgram.requireFeasible(x52);
  }

  /** problem taken from
   * Combinatorial Optimization
   * by Papadimitriou and Steiglitz
   * pp. 30 */
  @Test
  void testP30() {
    Tensor m = fromString( //
        "{1, 1, 1, 1, 0, 0, 0}", //
        "{1, 0, 0, 0, 1, 0, 0}", //
        "{0, 0, 1, 0, 0, 1, 0}", //
        "{0, 3, 1, 0, 0, 0, 1}" //
    );
    Tensor b = Tensors.vector(4, 2, 3, 6);
    Tensor c = Tensors.vector(0, 2, 0, 1, 0, 0, 5);
    LinearProgram linearProgram = //
        LinearProgram.of(Objective.MIN, c, ConstraintType.EQUALS, m, b, Variables.NON_NEGATIVE);
    Tensor x = LinearProgramming.of(linearProgram);
    Tensor X = Tensors.vector(0, 1, 3, 0, 2, 0, 0);
    assertEquals(x, X);
  }

  /** problem taken from
   * Combinatorial Optimization
   * by Papadimitriou and Steiglitz
   * pp. 51 */
  @Test
  void testCyclingP51() {
    // Tensor m = fromString( //
    // "{1/4, -8,-1 , 9}", //
    // "{1/2,-12,-1/2, 3}", //
    // "{0 , 0, 1 , 0}" //
    // );
    // Tensor b = Tensors.vector(0, 0, 1);
    // Tensor c = Tensors.fromString("{-3/4, 20, -1/2, 6}");
    // LinearProgram linearProgram = LinearProgram.of(Objective.MIN, c, ConstraintType.LESS_EQUALS, m, b, Variables.NON_NEGATIVE);
    // Tensor x = LinearProgramming.of(linearProgram);
    // Tensor X = Tensors.vector(1, 0, 1, 0);
    // assertEquals(x, X);
  }
}
