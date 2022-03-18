// code by jph
package ch.alpine.tensor.opt.lp;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.RationalScalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.opt.lp.LinearProgram.ConstraintType;
import ch.alpine.tensor.opt.lp.LinearProgram.Objective;
import ch.alpine.tensor.opt.lp.LinearProgram.Variables;

public class LinearProgrammingTest {
  @Test
  public void testCase4() {
    Tensor m = Tensors.matrixInt(new int[][] { { 1, 5, 1, 0, 0 }, { 2, 1, 0, 1, 0 }, { 1, 1, 0, 0, 1 } });
    Tensor b = Tensors.vector(40, 20, 12);
    LinearProgram linearProgram = LinearProgram.of( //
        Objective.MIN, Tensors.vector(-3, -5, 0, 0, 0), //
        ConstraintType.EQUALS, m, b, Variables.NON_NEGATIVE);
    assertTrue(linearProgram.isStandardPrimal());
    Tensor x = LinearProgramming.of(linearProgram);
    // Mathematica {5, 7, 0, 3, 0}
    assertEquals(x, Tensors.vector(5, 7, 0, 3, 0));
    Tensor tensor = SimplexCorners.of(linearProgram);
    assertEquals(x, tensor.get(0));
  }

  @Test
  public void testCase4max() {
    LinearProgram lpd = LinearProgram.of( //
        Objective.MAX, Tensors.vector(3, 5), //
        ConstraintType.LESS_EQUALS, //
        Tensors.matrixInt(new int[][] { { 1, 5 }, { 2, 1 }, { 1, 1 } }), //
        Tensors.vector(40, 20, 12), Variables.NON_NEGATIVE);
    assertTrue(lpd.isCanonicDual());
    // TODO TENSOR LP primal vs dual
    TestHelper.check(lpd, false);
    Tensor x = LinearProgramming.of(lpd);
    assertEquals(x, Tensors.vector(5, 7));
    Tensor tensor = SimplexCorners.of(lpd);
    assertEquals(x, tensor.get(0));
  }

  // MATLAB linprog example
  @Test
  public void testMatlab1() { // min c.x == -10/9
    Tensor m = Tensors.fromString("{{1, 1}, {1, 1/4}, {1, -1}, {-1/4, -1}, {-1, -1}, {-1, 1}}");
    Tensor b = Tensors.vector(2, 1, 2, 1, -1, 2);
    LinearProgram lpd = LinearProgram.of( //
        Objective.MAX, Tensors.fromString("{1, 1/3}"), //
        ConstraintType.LESS_EQUALS, m, b, Variables.NON_NEGATIVE);
    assertTrue(lpd.isCanonicDual());
    Tensor x = LinearProgramming.of(lpd);
    assertEquals(x, Tensors.fromString("{2/3, 4/3}"));
    Tensor tensor = SimplexCorners.of(lpd);
    assertEquals(x, tensor.get(0));
    LinearProgram lpc = lpd.toggle().toggle();
    assertEquals(lpd.objective, lpc.objective);
    assertEquals(lpd.c, lpc.c);
    assertEquals(lpd.constraintType, lpc.constraintType);
    assertEquals(lpd.A, lpc.A);
    assertEquals(lpd.b, lpc.b);
    assertEquals(lpd.var_count(), lpc.var_count());
    // TODO TENSOR LP primal vs dual, strictly speaking the lp in its
    // current form is not covered by the duality theorem
    // TestHelper.check(lpd, false);
  }

  // MATLAB linprog example
  @Test
  public void testMatlab1max() { // max c.x == min -c.x == -10/9
    Tensor m = Tensors.fromString("{{1, 1}, {1, 1/4}, {1, -1}, {-1/4, -1}, {-1, -1}, {-1, 1}}");
    Tensor b = Tensors.vector(2, 1, 2, 1, -1, 2);
    LinearProgram lpd = LinearProgram.of( //
        Objective.MAX, Tensors.fromString("{1, 1/3}"), //
        ConstraintType.LESS_EQUALS, m, b, Variables.NON_NEGATIVE);
    assertTrue(lpd.isCanonicDual());
    // TestHelper.check(lpd, false);
    Tensor xp = LinearProgramming.of(lpd);
    assertEquals(xp, Tensors.fromString("{2/3, 4/3}"));
    Tensor solp = SimplexCorners.of(lpd);
    assertEquals(solp.dot(lpd.c).Get(0), RationalScalar.of(10, 9));
    assertEquals(xp, solp.get(0));
    LinearProgram lpp = lpd.toggle();
    assertTrue(lpp.isCanonicPrimal());
    // LinearProgramming.of(lpd); // TODO TENSOR LP throws exception due to "unbounded"
    Tensor sold = SimplexCorners.of(lpp);
    assertEquals(sold.dot(lpp.c).Get(0), RationalScalar.of(10, 9));
    assertThrows(IllegalArgumentException.class, () -> lpp.requireFeasible(Tensors.vector(1, 1)));
    // TODO TENSOR LP primal vs dual
    // TestHelper.check(lpp, false);
  }

  // MATLAB linprog example
  @Test
  public void testMatlab2() {
    Tensor A = Tensors.fromString("{{1, 1}, {1, 1/4}, {1, -1}, {-1/4, -1}, {-1, -1}, {-1, 1}, {1, 1/4}}");
    Tensor b = Tensors.fromString("{2, 1, 2, 1, -1, 2, 1/2}");
    LinearProgram lpd = LinearProgram.of( //
        Objective.MAX, Tensors.fromString("{1, 1/3}"), //
        ConstraintType.LESS_EQUALS, A, b, Variables.NON_NEGATIVE);
    assertTrue(lpd.isCanonicDual());
    LinearProgram lpp = lpd.toggle();
    assertTrue(lpp.isCanonicPrimal());
    TestHelper.check(lpp, lpd);
    Tensor x = LinearProgramming.of(lpd);
    assertEquals(x, Tensors.vector(0, 2));
    Tensor solp = SimplexCorners.of(lpd);
    assertEquals(x, solp.get(0));
    // TODO TENSOR LP primal vs dual
    // TestHelper.check(lpp);
  }
}
