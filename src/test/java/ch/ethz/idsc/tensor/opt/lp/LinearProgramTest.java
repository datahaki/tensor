// code by jph
package ch.ethz.idsc.tensor.opt.lp;

import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.opt.lp.LinearProgram.ConstraintType;
import ch.ethz.idsc.tensor.opt.lp.LinearProgram.Objective;
import ch.ethz.idsc.tensor.opt.lp.LinearProgram.RegionType;
import junit.framework.TestCase;

/** Reference: "Linear and Integer Programming made Easy", 2016 */
public class LinearProgramTest extends TestCase {
  public void testP59a() {
    LinearProgram lpp = LinearProgram.of( //
        Objective.MAX, //
        Tensors.vector(2, 1), //
        ConstraintType.LESS_EQUALS, //
        Tensors.fromString("{{4, 1}, {2, -3}}"), //
        Tensors.vector(150, -40), //
        RegionType.NON_NEGATIVE);
    Tensor solp = SimplexCorners.of(lpp);
    assertEquals(solp, Tensors.fromString("{{0, 150}}"));
    LinearProgram lpd = lpp.dual();
    assertEquals(lpd.objective, Objective.MIN);
    assertEquals(lpd.constraintType, ConstraintType.GREATER_EQUALS);
    assertEquals(lpd.regionType, RegionType.NON_NEGATIVE);
    Tensor sold = SimplexCorners.of(lpd);
    assertEquals(solp.dot(lpp.c), sold.dot(lpd.c));
  }

  public void testP60a() {
    LinearProgram linearProgram = LinearProgram.of( //
        Objective.MIN, Tensors.vector(-1, 1), //
        ConstraintType.LESS_EQUALS, //
        Tensors.fromString("{{1, 2}, {3, -6}}"), //
        Tensors.vector(100, 650), //
        RegionType.COMPLETE);
    Tensor sol1 = SimplexCorners.of(linearProgram);
    assertEquals(sol1, Tensors.fromString("{{650/3, 0}}"));
  }

  public void testP62() {
    LinearProgram lpp = LinearProgram.of( //
        Objective.MIN, Tensors.vector(1, 2, 3), //
        ConstraintType.GREATER_EQUALS, //
        Tensors.fromString("{{4, 5, 6}, {8, 9, 10}}"), //
        Tensors.vector(7, 11), //
        RegionType.NON_NEGATIVE);
    Tensor sol2 = SimplexCorners.of(lpp);
    assertEquals(sol2, Tensors.fromString("{{7/4, 0, 0}}"));
    LinearProgram lpd = lpp.dual();
    assertEquals(lpd.c, Tensors.vector(7, 11));
    assertEquals(lpd.constraintType, ConstraintType.LESS_EQUALS);
    assertEquals(lpd.A, Tensors.fromString("{{4, 8}, {5, 9}, {6, 10}}"));
    assertEquals(lpd.b, Tensors.vector(1, 2, 3));
    assertEquals(lpd.regionType, RegionType.NON_NEGATIVE);
    Tensor sol3 = SimplexCorners.of(lpd);
    assertEquals(sol3, Tensors.fromString("{{1/4, 0}}"));
  }

  // MATLAB linprog example
  public void testLinProgMatlab1() { // min c.x == -10/9
    Tensor c = Tensors.fromString("{-1,-1/3}");
    Tensor m = Tensors.fromString("{{1, 1}, {1, 1/4}, {1, -1}, {-1/4, -1}, {-1, -1}, {-1, 1}}");
    Tensor b = Tensors.fromString("{2[m], 1[m], 2[m], 1[m], -1[m], 2[m]}");
    LinearProgram linearProgram = LinearProgram.of( //
        Objective.MIN, c, ConstraintType.LESS_EQUALS, m, b, RegionType.NON_NEGATIVE);
    Tensor x = LinearProgramming.of(linearProgram);
    assertEquals(x, Tensors.fromString("{2/3[m], 4/3[m]}"));
    Tensor solp = SimplexCorners.of(linearProgram);
    assertEquals(solp.get(0), x);
  }

  // MATLAB linprog example
  public void testMatlab1() { // min c.x == -10/9
    Tensor c = Tensors.fromString("{-1[m], -1/3[s]}");
    Tensor m = Tensors.fromString("{{1[m], 1[s]}, {1[m], 1/4[s]}, {1[m], -1[s]}, {-1/4[m], -1[s]}, {-1[m], -1[s]}, {-1[m], 1[s]}}");
    Tensor b = Tensors.vector(2, 1, 2, 1, -1, 2);
    LinearProgram linearProgram = LinearProgram.of( //
        Objective.MIN, c, ConstraintType.LESS_EQUALS, m, b, RegionType.NON_NEGATIVE);
    Tensor solp = SimplexCorners.of(linearProgram);
    assertEquals(solp, Tensors.fromString("{{2/3[m^-1], 4/3[s^-1]}}"));
    Tensor x = LinearProgramming.of(linearProgram);
    assertEquals(solp.get(0), x);
  }

  public void testObjective() {
    assertEquals(Objective.MIN.flip(), Objective.MAX);
    assertEquals(Objective.MAX.flip(), Objective.MIN);
  }
}
