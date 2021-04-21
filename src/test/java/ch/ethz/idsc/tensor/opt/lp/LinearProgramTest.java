// code by jph
package ch.ethz.idsc.tensor.opt.lp;

import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.opt.lp.LinearProgram.ConstraintType;
import ch.ethz.idsc.tensor.opt.lp.LinearProgram.CostType;
import ch.ethz.idsc.tensor.opt.lp.LinearProgram.RegionType;
import junit.framework.TestCase;

public class LinearProgramTest extends TestCase {
  public void testP59a() {
    LinearProgram lp1 = LinearProgram.of( //
        CostType.MAX, Tensors.vector(2, 1), //
        ConstraintType.LESS_EQUALS, //
        Tensors.fromString("{{4,1},{2,-3}}"), //
        Tensors.vector(150, -40), //
        RegionType.NON_NEGATIVE);
    Tensor sol1 = SimplexCorners.of(lp1);
    assertEquals(sol1, Tensors.fromString("{{205/7, 230/7}}"));
    LinearProgram lp2 = lp1.dual();
    assertEquals(lp2.costType, CostType.MIN);
    assertEquals(lp2.constraintType, ConstraintType.GREATER_EQUALS);
    assertEquals(lp2.regionType, RegionType.NON_NEGATIVE);
  }

  public void testP60a() {
    LinearProgram linearProgram = LinearProgram.of( //
        CostType.MIN, Tensors.vector(-1, 1), //
        ConstraintType.LESS_EQUALS, //
        Tensors.fromString("{{1,2},{3,-6}}"), //
        Tensors.vector(100, 650), //
        RegionType.COMPLETE);
    Tensor sol1 = SimplexCorners.of(linearProgram.equality());
    assertEquals(sol1, Tensors.fromString("{{650/3, 0}}"));
  }

  public void testP62() {
    LinearProgram lp1 = LinearProgram.of( //
        CostType.MIN, Tensors.vector(1, 2, 3), //
        ConstraintType.GREATER_EQUALS, //
        Tensors.fromString("{{4,5,6},{8,9,10}}"), //
        Tensors.vector(7, 11), //
        RegionType.NON_NEGATIVE);
    LinearProgram lp1e = lp1.equality();
    Tensor sol2 = SimplexCorners.of(lp1e);
    assertEquals(sol2, Tensors.fromString("{{7/4, 0, 0}}"));
    LinearProgram lp3 = lp1.dual();
    assertEquals(lp3.c, Tensors.vector(7, 11));
    assertEquals(lp3.constraintType, ConstraintType.LESS_EQUALS);
    assertEquals(lp3.A, Tensors.fromString("{{4,8},{5,9},{6,10}}"));
    assertEquals(lp3.b, Tensors.vector(1, 2, 3));
    assertEquals(lp3.regionType, RegionType.NON_NEGATIVE);
    LinearProgram lp3e = lp3.equality();
    Tensor sol3 = SimplexCorners.of(lp3e);
    assertEquals(sol3, Tensors.fromString("{{1/4, 0}}"));
  }
}
