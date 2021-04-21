// code by jph
package ch.ethz.idsc.tensor.opt.lp;

import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.opt.lp.LinearProgram.ConstraintType;
import ch.ethz.idsc.tensor.opt.lp.LinearProgram.CostType;
import ch.ethz.idsc.tensor.opt.lp.LinearProgram.RegionType;
import junit.framework.TestCase;

public class LinearProgrammingQuantityTest extends TestCase {
  // MATLAB linprog example
  public void testLinProgMatlab1() { // min c.x == -10/9
    Tensor c = Tensors.fromString("{-1,-1/3}");
    Tensor m = Tensors.fromString("{{1, 1}, {1, 1/4}, {1, -1}, {-1/4, -1}, {-1, -1}, {-1, 1}}");
    Tensor b = Tensors.fromString("{2[m], 1[m], 2[m], 1[m], -1[m], 2[m]}");
    LinearProgram linearProgram = //
        LinearProgram.of(CostType.MIN, c, ConstraintType.LESS_EQUALS, m, b, RegionType.NON_NEGATIVE);
    Tensor x = LinearProgramming.of(linearProgram, SimplexPivots.NONBASIC_GRADIENT);
    assertEquals(x, Tensors.fromString("{2/3[m], 4/3[m]}"));
    Tensor solp = SimplexCorners.of(linearProgram);
    assertEquals(solp.get(0), x);
  }

  // MATLAB linprog example
  public void testMatlab1() { // min c.x == -10/9
    Tensor c = Tensors.fromString("{-1[m], -1/3[s]}");
    Tensor m = Tensors.fromString("{{1[m], 1[s]}, {1[m], 1/4[s]}, {1[m], -1[s]}, {-1/4[m], -1[s]}, {-1[m], -1[s]}, {-1[m], 1[s]}}");
    Tensor b = Tensors.vector(2, 1, 2, 1, -1, 2);
    LinearProgram linearProgram = //
        LinearProgram.of(CostType.MIN, c, ConstraintType.LESS_EQUALS, m, b, RegionType.NON_NEGATIVE);
    Tensor solp = SimplexCorners.of(linearProgram);
    assertEquals(solp, Tensors.fromString("{{2/3[m^-1], 4/3[s^-1]}}"));
  }
}
