// code by jph
package ch.ethz.idsc.tensor.opt.lp;

import ch.ethz.idsc.tensor.RationalScalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.TensorRuntimeException;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Array;
import ch.ethz.idsc.tensor.alg.Transpose;
import ch.ethz.idsc.tensor.opt.lp.LinearProgram.ConstraintType;
import ch.ethz.idsc.tensor.opt.lp.LinearProgram.CostType;
import ch.ethz.idsc.tensor.opt.lp.LinearProgram.RegionType;
import ch.ethz.idsc.tensor.usr.AssertFail;
import junit.framework.TestCase;

// tests should be improved over time
public class LinearProgrammingTest extends TestCase {
  public void testCase4() {
    Tensor c = Tensors.vector(-3, -5, 0, 0, 0);
    Tensor m = Tensors.matrixInt(new int[][] { { 1, 5, 1, 0, 0 }, { 2, 1, 0, 1, 0 }, { 1, 1, 0, 0, 1 } });
    Tensor b = Tensors.vector(40, 20, 12);
    LinearProgram linearProgram = //
        LinearProgram.of(CostType.MIN, c, ConstraintType.EQUALS, m, b, RegionType.NON_NEGATIVE);
    Tensor x = LinearProgramming.of(linearProgram, SimplexPivots.NONBASIC_GRADIENT);
    // Mathematica {5, 7, 0, 3, 0}
    assertEquals(x, Tensors.vector(5, 7, 0, 3, 0));
    Tensor tensor = SimplexCorners.of(linearProgram);
    assertEquals(x, tensor.get(0));
  }

  public void testCase4max() {
    Tensor c = Tensors.vector(3, 5);
    Tensor m = Tensors.matrixInt(new int[][] { { 1, 5 }, { 2, 1 }, { 1, 1 } });
    Tensor b = Tensors.vector(40, 20, 12);
    LinearProgram linearProgram = //
        LinearProgram.of(CostType.MAX, c, ConstraintType.LESS_EQUALS, m, b, RegionType.NON_NEGATIVE);
    Tensor x = LinearProgramming.of(linearProgram, SimplexPivots.NONBASIC_GRADIENT);
    assertEquals(x, Tensors.vector(5, 7));
    Tensor tensor = SimplexCorners.of(linearProgram);
    assertEquals(x, tensor.get(0));
  }

  // MATLAB linprog example
  public void testMatlab1() { // min c.x == -10/9
    Tensor c = Tensors.fromString("{-1, -1/3}");
    Tensor m = Tensors.fromString("{{1, 1}, {1, 1/4}, {1, -1}, {-1/4, -1}, {-1, -1}, {-1, 1}}");
    Tensor b = Tensors.vector(2, 1, 2, 1, -1, 2);
    LinearProgram linearProgram = //
        LinearProgram.of(CostType.MIN, c, ConstraintType.LESS_EQUALS, m, b, RegionType.NON_NEGATIVE);
    Tensor x = LinearProgramming.of(linearProgram, SimplexPivots.NONBASIC_GRADIENT);
    assertEquals(x, Tensors.fromString("{2/3, 4/3}"));
    Tensor tensor = SimplexCorners.of(linearProgram);
    assertEquals(x, tensor.get(0));
  }

  // MATLAB linprog example
  public void testMatlab1max() { // max c.x == min -c.x == -10/9
    Tensor c = Tensors.fromString("{1, 1/3}");
    Tensor m = Tensors.fromString("{{1, 1}, {1, 1/4}, {1, -1}, {-1/4, -1}, {-1, -1}, {-1, 1}}");
    Tensor b = Tensors.vector(2, 1, 2, 1, -1, 2);
    LinearProgram lpp = //
        LinearProgram.of(CostType.MAX, c, ConstraintType.LESS_EQUALS, m, b, RegionType.NON_NEGATIVE);
    Tensor x = LinearProgramming.of(lpp, SimplexPivots.NONBASIC_GRADIENT);
    assertEquals(x, Tensors.fromString("{2/3, 4/3}"));
    Tensor solp = SimplexCorners.of(lpp);
    assertEquals(solp.dot(lpp.c).Get(0), RationalScalar.of(10, 9));
    assertEquals(x, solp.get(0));
    LinearProgram lpd = lpp.dual();
    Tensor sold = SimplexCorners.of(lpd);
    assertEquals(sold.dot(lpd.c).Get(0), RationalScalar.of(10, 9));
  }

  // MATLAB linprog example
  public void testMatlab2() {
    Tensor c = Tensors.fromString("{-1,-1/3}");
    Tensor A = Tensors.fromString("{{1, 1}, {1, 1/4}, {1, -1}, {-1/4, -1}, {-1, -1}, {-1, 1}, {1, 1/4}}");
    Tensor b = Tensors.fromString("{2, 1, 2, 1, -1, 2, 1/2}");
    LinearProgram linearProgram = LinearProgram.of(CostType.MIN, c, ConstraintType.LESS_EQUALS, A, b, RegionType.NON_NEGATIVE);
    // Tensor solp = SimplexCorners.of(linearProgram);
    Tensor x = LinearProgramming.of(linearProgram, SimplexPivots.NONBASIC_GRADIENT);
    assertEquals(x, Tensors.vector(0, 2));
    Tensor solp = SimplexCorners.of(linearProgram);
    assertEquals(x, solp.get(0));
  }

  public void testClrsP846() { // max cost = 8
    Tensor c = Tensors.vector(1, 1);
    Tensor m = Tensors.matrixInt(new int[][] { { 4, -1 }, { 2, 1 }, { -5, 2 } });
    Tensor b = Tensors.vector(8, 10, 2);
    LinearProgram linearProgram = //
        LinearProgram.of(CostType.MAX, c, ConstraintType.LESS_EQUALS, m, b, RegionType.NON_NEGATIVE);
    Tensor x = LinearProgramming.of(linearProgram, SimplexPivots.NONBASIC_GRADIENT);
    assertEquals(x, Tensors.vector(2, 6)); // see page 847
    assertTrue(LinearProgrammingTest.isFeasible(m, Array.zeros(2), b));
    assertTrue(LinearProgrammingTest.isFeasible(m, Tensors.vector(3, 4), b));
    assertTrue(LinearProgrammingTest.isFeasible(m, Tensors.vector(1, 3), b));
    assertTrue(LinearProgrammingTest.isFeasible(m, Tensors.vector(2, 0), b));
    assertFalse(LinearProgrammingTest.isFeasible(m, Tensors.vector(3, 3), b));
  }

  public void testClrsP846Dual() {
    Tensor c = Tensors.vector(8, 10, 2);
    Tensor m = Transpose.of(Tensors.matrixInt(new int[][] { { 4, -1 }, { 2, 1 }, { -5, 2 } })).negate();
    Tensor b = Tensors.vector(1, 1).negate();
    TensorRuntimeException.of(c, m, b);
    // Tensor x = LinearProgramming.minLessEquals(c, m, b);
    // System.out.println(x);
    // System.out.println("cost "+c.dot(x));
  }

  // same as p846 except that (0,0) is not feasible
  public void testClrsP846var() {
    Tensor c = Tensors.vector(-1, -1);
    Tensor m = Tensors.matrixInt(new int[][] { { 4, -1 }, { 2, 1 }, { -5, 2 }, { -1, -1 } });
    Tensor b = Tensors.vector(8, 10, 2, -1);
    LinearProgram linearProgram = LinearProgram.of(CostType.MIN, c, ConstraintType.LESS_EQUALS, m, b, RegionType.NON_NEGATIVE);
    Tensor x = LinearProgramming.of(linearProgram, SimplexPivots.NONBASIC_GRADIENT);
    // Mathematica {2, 6}
    assertEquals(x, Tensors.vector(2, 6)); // see page 847
    assertFalse(LinearProgrammingTest.isFeasible(m, Array.zeros(2), b));
    assertTrue(LinearProgrammingTest.isFeasible(m, Tensors.vector(3, 4), b));
    assertTrue(LinearProgrammingTest.isFeasible(m, Tensors.vector(1, 3), b));
    assertTrue(LinearProgrammingTest.isFeasible(m, Tensors.vector(2, 0), b));
    assertFalse(LinearProgrammingTest.isFeasible(m, Tensors.vector(3, 3), b));
  }

  // infeasible
  public void testClrsP858_6() {
    Tensor c = Tensors.vector(-3, 2);
    Tensor m = Tensors.matrixInt(new int[][] { { 1, 1 }, { -2, -2 } });
    Tensor b = Tensors.vector(2, -10);
    LinearProgram linearProgram = LinearProgram.of(CostType.MIN, c, ConstraintType.LESS_EQUALS, m, b, RegionType.NON_NEGATIVE);
    Tensor sols = SimplexCorners.of(linearProgram);
    assertEquals(sols, Tensors.empty());
    AssertFail.of(() -> LinearProgramming.of(linearProgram, SimplexPivots.NONBASIC_GRADIENT));
  }

  // unbounded
  public void testClrsP858_7() {
    Tensor c = Tensors.vector(-1, 1);
    Tensor m = Tensors.matrixInt(new int[][] { { -2, 1 }, { -1, -2 } });
    Tensor b = Tensors.vector(-1, -2);
    LinearProgram linearProgram = //
        LinearProgram.of(CostType.MIN, c, ConstraintType.LESS_EQUALS, m, b, RegionType.NON_NEGATIVE);
    AssertFail.of(() -> LinearProgramming.of(linearProgram, SimplexPivots.NONBASIC_GRADIENT));
  }

  public void testClrsP879_5() {
    Tensor c = Tensors.vector(18, 12.5);
    Tensor m = Tensors.matrixInt(new int[][] { { 1, 1 }, { 1, 0 }, { 0, 1 } });
    Tensor b = Tensors.vector(20, 12, 16);
    LinearProgram linearProgram = //
        LinearProgram.of(CostType.MAX, c, ConstraintType.LESS_EQUALS, m, b, RegionType.NON_NEGATIVE);
    Tensor x = LinearProgramming.of(linearProgram, SimplexPivots.NONBASIC_GRADIENT);
    assertEquals(x, Tensors.vector(12, 8)); // confirmed with linprog
  }

  public void testClrsP879_5Dual() {
    Tensor c = Tensors.vector(20, 12, 16);
    Tensor m = Transpose.of(Tensors.matrixInt(new int[][] { { 1, 1 }, { 1, 0 }, { 0, 1 } })).negate();
    Tensor b = Tensors.vector(18, 12.5).negate();
    TensorRuntimeException.of(c, m, b);
    // System.out.println(Pretty.of(m));
    // System.out.println(Pretty.of(b));
    // Tensor x = LinearProgramming.minLessEquals(c, m, b);
    // assertEquals(x, Tensors.fromString("[12,8]")); // confirmed with linprog
    // System.out.println(x);
  }

  public void testClrsP879_6() {
    Tensor c = Tensors.vector(5, -3);
    Tensor m = Tensors.matrixInt(new int[][] { { 1, -1 }, { 2, 1 } });
    Tensor b = Tensors.vector(1, 2);
    LinearProgram linearProgram = LinearProgram.of(CostType.MAX, c, ConstraintType.LESS_EQUALS, m, b, RegionType.NON_NEGATIVE);
    Tensor x = LinearProgramming.of(linearProgram, SimplexPivots.NONBASIC_GRADIENT);
    assertEquals(x, Tensors.vector(1, 0)); // confirmed with linprog
  }

  public void testClrsP879_7() {
    Tensor c = Tensors.vector(1, 1, 1);
    Tensor m = Tensors.fromString("{{-2, -7.5, -3}, {-20, -5, -10}}");
    Tensor b = Tensors.vector(-10000, -30000);
    LinearProgram linearProgram = //
        LinearProgram.of(CostType.MIN, c, ConstraintType.LESS_EQUALS, m, b, RegionType.NON_NEGATIVE);
    AssertFail.of(() -> LinearProgramming.of(linearProgram, SimplexPivots.NONBASIC_GRADIENT));
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

  /** @param m
   * @param x
   * @param b
   * @return true if x >= 0 and m.x <= b */
  public static boolean isFeasible(Tensor m, Tensor x, Tensor b) {
    return StaticHelper.isNonNegative(x) //
        && StaticHelper.isNonNegative(b.subtract(m.dot(x)));
  }
}
