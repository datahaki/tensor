// code by jph
package ch.alpine.tensor.opt.lp;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.opt.lp.LinearProgram.ConstraintType;
import ch.alpine.tensor.opt.lp.LinearProgram.Objective;
import ch.alpine.tensor.opt.lp.LinearProgram.Variables;

/** Reference: "Linear and Integer Programming made Easy", 2016 */
class LinearProgramTest {
  @Test
  void testP59a() {
    LinearProgram lpp = LinearProgram.of( //
        Objective.MAX, //
        Tensors.vector(2, 1), //
        ConstraintType.LESS_EQUALS, //
        Tensors.fromString("{{4, 1}, {2, -3}}"), //
        Tensors.vector(150, -40), //
        Variables.NON_NEGATIVE);
    Tensor solp = SimplexCorners.of(lpp);
    assertEquals(solp, Tensors.fromString("{{0, 150}}"));
    LinearProgram lpd = lpp.toggle();
    assertEquals(lpd.objective, Objective.MIN);
    assertEquals(lpd.constraintType, ConstraintType.GREATER_EQUALS);
    assertEquals(lpd.variables, Variables.NON_NEGATIVE);
    Tensor sold = SimplexCorners.of(lpd);
    assertEquals(solp.dot(lpp.c), sold.dot(lpd.c));
  }

  @Test
  void testP60a() {
    LinearProgram linearProgram = LinearProgram.of( //
        Objective.MIN, Tensors.vector(-1, 1), //
        ConstraintType.LESS_EQUALS, //
        Tensors.fromString("{{1, 2}, {3, -6}}"), //
        Tensors.vector(100, 650), //
        Variables.UNRESTRICTED);
    Tensor sol1 = SimplexCorners.of(linearProgram);
    assertEquals(sol1, Tensors.fromString("{{650/3, 0}}"));
    assertThrows(RuntimeException.class, linearProgram::toggle);
  }

  @Test
  void testP62() {
    LinearProgram lpp = LinearProgram.of( //
        Objective.MIN, Tensors.vector(1, 2, 3), //
        ConstraintType.GREATER_EQUALS, //
        Tensors.fromString("{{4, 5, 6}, {8, 9, 10}}"), //
        Tensors.vector(7, 11), //
        Variables.NON_NEGATIVE);
    Tensor sol2 = SimplexCorners.of(lpp);
    assertEquals(sol2, Tensors.fromString("{{7/4, 0, 0}}"));
    LinearProgram lpd = lpp.toggle();
    assertEquals(lpd.c, Tensors.vector(7, 11));
    assertEquals(lpd.constraintType, ConstraintType.LESS_EQUALS);
    assertEquals(lpd.A, Tensors.fromString("{{4, 8}, {5, 9}, {6, 10}}"));
    assertEquals(lpd.b, Tensors.vector(1, 2, 3));
    assertEquals(lpd.variables, Variables.NON_NEGATIVE);
    Tensor sol3 = SimplexCorners.of(lpd);
    assertEquals(sol3, Tensors.fromString("{{1/4, 0}}"));
  }

  // MATLAB linprog example
  @Test
  void testLinProgMatlab1() { // min c.x == -10/9
    Tensor c = Tensors.fromString("{-1,-1/3}");
    Tensor m = Tensors.fromString("{{1, 1}, {1, 1/4}, {1, -1}, {-1/4, -1}, {-1, -1}, {-1, 1}}");
    Tensor b = Tensors.fromString("{2[m], 1[m], 2[m], 1[m], -1[m], 2[m]}");
    LinearProgram linearProgram = LinearProgram.of( //
        Objective.MIN, c, ConstraintType.LESS_EQUALS, m, b, Variables.NON_NEGATIVE);
    Tensor x = LinearOptimization.of(linearProgram);
    assertEquals(x, Tensors.fromString("{2/3[m], 4/3[m]}"));
    Tensor solp = SimplexCorners.of(linearProgram);
    assertEquals(solp.get(0), x);
  }

  // MATLAB linprog example
  @Test
  void testMatlab1() { // min c.x == -10/9
    Tensor c = Tensors.fromString("{-1[m], -1/3[s]}");
    Tensor m = Tensors.fromString("{{1[m], 1[s]}, {1[m], 1/4[s]}, {1[m], -1[s]}, {-1/4[m], -1[s]}, {-1[m], -1[s]}, {-1[m], 1[s]}}");
    Tensor b = Tensors.vector(2, 1, 2, 1, -1, 2);
    LinearProgram linearProgram = LinearProgram.of( //
        Objective.MIN, c, ConstraintType.LESS_EQUALS, m, b, Variables.NON_NEGATIVE);
    Tensor solp = SimplexCorners.of(linearProgram);
    assertEquals(solp, Tensors.fromString("{{2/3[m^-1], 4/3[s^-1]}}"));
    Tensor x = LinearOptimization.of(linearProgram);
    assertEquals(solp.get(0), x);
  }

  @Test
  void testObjective() {
    assertEquals(Objective.MIN.flip(), Objective.MAX);
    assertEquals(Objective.MAX.flip(), Objective.MIN);
  }

  @Test
  void testConstraint() {
    assertEquals(ConstraintType.LESS_EQUALS.flipInequality(), ConstraintType.GREATER_EQUALS);
    assertEquals(ConstraintType.GREATER_EQUALS.flipInequality(), ConstraintType.LESS_EQUALS);
    assertThrows(RuntimeException.class, ConstraintType.EQUALS::flipInequality);
  }
}
