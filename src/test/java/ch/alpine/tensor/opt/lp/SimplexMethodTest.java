// code by jph
package ch.alpine.tensor.opt.lp;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.NavigableMap;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.Throw;
import ch.alpine.tensor.alg.Array;
import ch.alpine.tensor.opt.lp.LinearProgram.ConstraintType;
import ch.alpine.tensor.opt.lp.LinearProgram.Objective;
import ch.alpine.tensor.opt.lp.LinearProgram.Variables;

/** with examples from CLRS */
class SimplexMethodTest {
  @Test
  void testP845() {
    Tensor m = Tensors.matrixInt(new int[][] { //
        { -2, 8, 0, 10 }, //
        { 5, 2, 0, 0 }, //
        { 3, -5, 10, -2 } });
    LinearProgram lpp = LinearProgram.of( //
        Objective.MIN, Tensors.vector(1, 1, 1, 1), //
        ConstraintType.GREATER_EQUALS, m, //
        Tensors.vector(50, 100, 25), Variables.NON_NEGATIVE);
    assertTrue(lpp.isCanonicPrimal());
    LinearProgram lpd = lpp.toggle();
    assertTrue(lpd.isCanonicDual());
    TestHelper.check(lpp, lpd);
    Tensor xp = LinearOptimization.of(lpp);
    // TODO TENSOR LP solver does not find optimal solution of primal problem!
    NavigableMap<Scalar, Tensor> map = SimplexCorners.of(lpp.c, lpp.A, lpp.b, true);
    assertTrue(map.values().contains(Tensors.of(xp)));
    Tensor xd = LinearOptimization.of(lpd);
    Tensor xdc = SimplexCorners.of(lpd);
    assertEquals(xd, xdc.get(0));
    assertEquals(map.firstKey(), xd.dot(lpd.c));
    TestHelper.check(lpp, false);
  }

  @Test
  void testP846() { // max cost = 8
    Tensor m = Tensors.matrixInt(new int[][] { //
        { 4, -1 }, //
        { 2, 1 }, //
        { -5, 2 } });
    LinearProgram lpd = LinearProgram.of( //
        Objective.MAX, Tensors.vector(1, 1), //
        ConstraintType.LESS_EQUALS, m, //
        Tensors.vector(8, 10, 2), Variables.NON_NEGATIVE);
    assertTrue(lpd.isCanonicDual());
    LinearProgram lpp = lpd.toggle();
    assertTrue(lpp.isCanonicPrimal());
    Tensor xp = LinearOptimization.of(lpd);
    TestHelper.check(lpd, true);
    assertEquals(xp, Tensors.vector(2, 6)); // see page 847
    lpd.requireFeasible(Array.zeros(2));
    lpd.requireFeasible(Tensors.vector(2, 0));
    lpd.requireFeasible(Tensors.vector(3, 4));
    lpd.requireFeasible(Tensors.vector(1, 3));
    lpd.requireFeasible(Tensors.vector(0, 1));
    assertThrows(Throw.class, () -> lpd.requireFeasible(Tensors.vector(-3, 3)));
    assertThrows(Throw.class, () -> lpd.requireFeasible(Tensors.vector(3, 3)));
    Tensor xd = LinearOptimization.of(lpp);
    assertEquals(xp.dot(lpd.c), xd.dot(lpp.c));
  }

  // same as p846 except that (0,0) is not feasible
  @Test
  void testP846var() {
    Tensor m = Tensors.matrixInt(new int[][] { { 4, -1 }, { 2, 1 }, { -5, 2 }, { -1, -1 } });
    LinearProgram lpd = LinearProgram.of( //
        Objective.MAX, Tensors.vector(1, 1), //
        ConstraintType.LESS_EQUALS, m, //
        Tensors.vector(8, 10, 2, -1), Variables.NON_NEGATIVE);
    assertTrue(lpd.isCanonicDual());
    LinearProgram lpp = lpd.toggle();
    assertTrue(lpp.isCanonicPrimal());
    TestHelper.check(lpp, lpd);
    Tensor x = LinearOptimization.of(lpd);
    // Mathematica {2, 6}
    assertEquals(x, Tensors.vector(2, 6)); // see page 847
    lpd.requireFeasible(Tensors.vector(3, 4));
    lpd.requireFeasible(Tensors.vector(1, 3));
    lpd.requireFeasible(Tensors.vector(2, 0));
    assertThrows(Throw.class, () -> lpd.requireFeasible(Tensors.vector(0, 0)));
    assertThrows(Throw.class, () -> lpd.requireFeasible(Tensors.vector(3, 3)));
  }

  @Test
  void testP854() { // max cost = 8
    Tensor m = Tensors.matrixInt(new int[][] { //
        { 1, 1, -1 }, //
        { -1, -1, 1 }, //
        { 1, -2, 2 } });
    LinearProgram lpd = LinearProgram.of( //
        Objective.MAX, Tensors.vector(2, -3, 3), //
        ConstraintType.LESS_EQUALS, m, //
        Tensors.vector(7, -7, 4), Variables.NON_NEGATIVE);
    assertTrue(lpd.isCanonicDual());
    Scalar opt = TestHelper.check(lpd.toggle(), lpd);
    Tensor x = LinearOptimization.of(lpd);
    assertEquals(opt, x.dot(lpd.c));
    // TestHelper.check(lpd, false);
  }

  // infeasible
  @Test
  void testP858_6() {
    Tensor m = Tensors.matrixInt(new int[][] { { 1, 1 }, { -2, -2 } });
    Tensor b = Tensors.vector(2, -10);
    LinearProgram lpd = LinearProgram.of( //
        Objective.MAX, Tensors.vector(3, -2), //
        ConstraintType.LESS_EQUALS, m, b, Variables.NON_NEGATIVE);
    assertTrue(lpd.isCanonicDual());
    LinearProgram lpp = lpd.toggle();
    assertTrue(lpp.isCanonicPrimal());
    // TODO TENSOR LP apparently non symmetric
    // TestHelper.check(lpp, lpd);
    Tensor sols = SimplexCorners.of(lpd);
    assertEquals(sols, Tensors.empty());
    assertThrows(Throw.class, () -> LinearOptimization.of(lpd));
  }

  // unbounded
  @Test
  void testP858_7() {
    Tensor m = Tensors.matrixInt(new int[][] { { -2, 1 }, { -1, -2 } });
    Tensor b = Tensors.vector(-1, -2);
    LinearProgram lpd = LinearProgram.of( //
        Objective.MAX, Tensors.vector(1, -1), //
        ConstraintType.LESS_EQUALS, m, b, Variables.NON_NEGATIVE);
    assertTrue(lpd.isCanonicDual());
    assertThrows(Throw.class, () -> LinearOptimization.of(lpd));
  }

  @Test
  void testP865() {
    Tensor m = Tensors.matrixInt(new int[][] { //
        { 1, 1, 3 }, //
        { 2, 2, 5 }, //
        { 4, 1, 2 } });
    LinearProgram lpd = LinearProgram.of( //
        Objective.MAX, Tensors.vector(3, 1, 2), //
        ConstraintType.LESS_EQUALS, m, //
        Tensors.vector(30, 24, 36), Variables.NON_NEGATIVE);
    assertTrue(lpd.isCanonicDual());
    Scalar opt = TestHelper.check(lpd.toggle(), lpd);
    Tensor x = LinearOptimization.of(lpd);
    assertEquals(x, Tensors.vector(8, 4, 0)); // p868
    assertEquals(opt, x.dot(lpd.c));
    TestHelper.check(lpd, true);
  }

  @Test
  void testP878_5() {
    Tensor m = Tensors.matrixInt(new int[][] { { 1, 1 }, { 1, 0 }, { 0, 1 } });
    Tensor b = Tensors.vector(20, 12, 16);
    LinearProgram lpd = LinearProgram.of( //
        Objective.MAX, Tensors.vector(18, 12.5), //
        ConstraintType.LESS_EQUALS, m, b, Variables.NON_NEGATIVE);
    assertTrue(lpd.isCanonicDual());
    Tensor x = LinearOptimization.of(lpd);
    assertEquals(x, Tensors.vector(12, 8)); // confirmed with linprog
    Tensor cp = x.dot(lpd.c);
    Tensor solp = SimplexCorners.of(lpd);
    assertEquals(x, solp.get(0));
    LinearProgram lpp = lpd.toggle();
    assertTrue(lpp.isCanonicPrimal());
    Tensor xd = LinearOptimization.of(lpp);
    Tensor cd = xd.dot(lpp.c);
    assertEquals(cp, cd);
    TestHelper.check(lpd, true);
  }

  @Test
  void testP879_6() {
    Tensor m = Tensors.matrixInt(new int[][] { { 1, -1 }, { 2, 1 } });
    Tensor b = Tensors.vector(1, 2);
    LinearProgram lpd = LinearProgram.of( //
        Objective.MAX, Tensors.vector(5, -3), //
        ConstraintType.LESS_EQUALS, m, b, Variables.NON_NEGATIVE);
    assertTrue(lpd.isCanonicDual());
    Tensor x = LinearOptimization.of(lpd);
    assertEquals(x, Tensors.vector(1, 0)); // confirmed with linprog
    Tensor sold = SimplexCorners.of(lpd);
    assertEquals(x, sold.get(0));
    TestHelper.check(lpd, true);
  }

  @Test
  void testP879_7() {
    Tensor c = Tensors.vector(1, 1, 1);
    Tensor m = Tensors.fromString("{{-2, -7.5, -3}, {-20, -5, -10}}");
    Tensor b = Tensors.vector(-10000, -30000);
    LinearProgram linearProgram = //
        LinearProgram.of(Objective.MIN, c, ConstraintType.LESS_EQUALS, m, b, Variables.NON_NEGATIVE);
    assertThrows(Throw.class, () -> LinearOptimization.of(linearProgram));
    // MATLAB
    // A=[[-2, -7.5, -3];[-20, -5, -10]];
    // b=[-10000;-30000]
    // c=[1,1,1];
    // linprog(c,A,b)
    // ...
    // Exiting: One or more of the residuals, duality gap, or total relative error
    // has grown 100000 times greater than its minimum value so far:
    // the dual appears to be infeasible (and the primal unbounded).
    // (The primal residual < OptimalityTolerance=1.00e-08.)
    // ans =
    // 1.0e+32 *
    // 2.0744
    // 1.3829
    // -4.8403
  }
}
