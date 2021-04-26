// code by jph
package ch.ethz.idsc.tensor.opt.lp;

import java.util.Arrays;
import java.util.Collections;
import java.util.Map;
import java.util.NavigableMap;

import ch.ethz.idsc.tensor.RationalScalar;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Array;
import ch.ethz.idsc.tensor.alg.Dimensions;
import ch.ethz.idsc.tensor.opt.lp.LinearProgram.ConstraintType;
import ch.ethz.idsc.tensor.opt.lp.LinearProgram.Objective;
import ch.ethz.idsc.tensor.opt.lp.LinearProgram.Variables;
import ch.ethz.idsc.tensor.qty.Quantity;
import ch.ethz.idsc.tensor.sca.Chop;
import junit.framework.TestCase;

/** Reference:
 * "Linear and Integer Programming made Easy"
 * by T.C. Hu, Andrew B. Kahng, 2016 */
public class HuKahngTest extends TestCase {
  public void testP14() {
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
    LinearProgram lpd = LinearProgram.of( //
        Objective.MAX, c, ConstraintType.LESS_EQUALS, m, b, Variables.NON_NEGATIVE);
    assertTrue(lpd.isCanonicDual());
    Tensor solp = SimplexCorners.of(lpd);
    assertEquals(xs, solp);
    LinearProgram lpp = lpd.toggle();
    assertTrue(lpp.isCanonicPrimal());
    Tensor sold = SimplexCorners.of(lpp);
    assertEquals(sold, Tensors.fromString("{{1/10[USD*Wood^-1], 7/15[Iron^-1*USD]}}"));
    Tensor xd = LinearProgramming.of(lpp);
    assertEquals(sold.get(0), xd);
  }

  public void testP18_2() {
    Tensor c = Tensors.fromString("{12[USD], 10[USD], 1[USD]}");
    Tensor m = Tensors.fromString("{{11[lb], 10[lb], 9[lb]}}");
    Tensor b = Tensors.fromString("{20[lb]}");
    NavigableMap<Scalar, Tensor> navigableMap = SimplexCorners.of(c, m, b, true);
    Tensor sol1 = navigableMap.get(Quantity.of(RationalScalar.of(240, 11), "USD"));
    assertEquals(sol1, Tensors.fromString("{{20/11, 0, 0}}"));
    LinearProgram lpd = LinearProgram.of( //
        Objective.MAX, c, ConstraintType.LESS_EQUALS, m, b, Variables.NON_NEGATIVE);
    assertTrue(lpd.isCanonicDual());
    Tensor sol2 = SimplexCorners.of(lpd);
    assertEquals(sol1, sol2);
    Tensor xp = LinearProgramming.of(lpd);
    assertEquals(sol1.get(0), xp);
    LinearProgram lpp = lpd.toggle();
    assertTrue(lpp.isCanonicPrimal());
    Tensor xd = LinearProgramming.of(lpp);
    assertEquals(xp.dot(lpd.c), xd.dot(lpp.c));
  }

  public void testP18_3() {
    Tensor c = Tensors.vector(1, 1, 1);
    Tensor m = Tensors.matrixInt(new int[][] { { 6, 3, 1 }, { 4, 5, 6 } });
    Tensor b = Tensors.vector(15, 15);
    LinearProgram lpd = LinearProgram.of( //
        Objective.MAX, c, ConstraintType.LESS_EQUALS, m, b, Variables.NON_NEGATIVE);
    assertTrue(lpd.isCanonicDual());
    Scalar scalar = TestHelper.check(lpd, true);
    assertEquals(scalar, RationalScalar.of(10, 3));
  }

  public void testP21() {
    Tensor c = Tensors.fromString("{1[USD], 1[USD], 1[USD]}");
    Tensor m = Tensors.fromString("{{4[ap], 1[ap], 3[ap]}, {1[or], 4[or], 2[or]}}");
    Tensor b = Tensors.fromString("{15[ap], 15[or]}");
    NavigableMap<Scalar, Tensor> navigableMap = SimplexCorners.of(c, m, b, true);
    Map<Scalar, Tensor> map2 = Collections.singletonMap(Quantity.of(6, "USD"), Tensors.fromString("{{3, 3, 0}, {0, 3/2, 9/2}}"));
    assertEquals(navigableMap, map2);
    LinearProgram lpd = LinearProgram.of( //
        Objective.MAX, c, ConstraintType.LESS_EQUALS, m, b, Variables.NON_NEGATIVE);
    assertTrue(lpd.isCanonicDual());
    Tensor solp = SimplexCorners.of(lpd);
    assertEquals(solp, Tensors.fromString("{{3, 3, 0}, {0, 3/2, 9/2}}"));
    assertEquals(solp.dot(lpd.c), Tensors.fromString("{6[USD], 6[USD]}"));
    LinearProgram lpp = lpd.toggle();
    assertTrue(lpp.isCanonicPrimal());
    Tensor sold = SimplexCorners.of(lpp);
    assertEquals(sold, Tensors.fromString("{{1/5[USD*ap^-1], 1/5[USD*or^-1]}}"));
    assertEquals(sold.dot(lpp.c), Tensors.fromString("{6[USD]}"));
    Tensor xd = LinearProgramming.of(lpp);
    assertEquals(sold.get(0), xd);
  }

  public void testP23() {
    Tensor c = Tensors.fromString("{15[USD], 7[USD], 4[USD], 6[USD]}");
    Tensor m = Tensors.fromString("{{3[va], 1[va], 0[va], -1[va]}, {1[vb], 1[vb], 1[vb], 2[vb]}}");
    Tensor b = Tensors.fromString("{3[va], 5[vb]}");
    NavigableMap<Scalar, Tensor> navigableMap = SimplexCorners.of(c, m, b, true);
    Scalar key = Quantity.of(29, "USD");
    Tensor tensor = navigableMap.get(key);
    assertEquals(tensor, Tensors.fromString("{{0, 3, 2, 0}}"));
    LinearProgram lpp = LinearProgram.of(Objective.MIN, c, ConstraintType.GREATER_EQUALS, m, b, Variables.NON_NEGATIVE);
    Tensor solp = SimplexCorners.of(lpp);
    assertEquals(tensor, solp);
    assertEquals(solp.dot(lpp.c).Get(0), key);
    LinearProgram lpd = lpp.toggle();
    String string = "{{3[va], 1[vb]}, {1[va], 1[vb]}, {0[va], 1[vb]}, {-1[va], 2[vb]}}";
    assertEquals(lpd.A, Tensors.fromString(string));
    assertEquals(lpd.b, c);
    Tensor sold = SimplexCorners.of(lpd);
    assertEquals(sold, Tensors.fromString("{{3[USD*va^-1], 4[USD*vb^-1]}}"));
    assertEquals(sold.dot(lpd.c).Get(0), key);
  }

  public void testP42() {
    Tensor c = Array.zeros(4);
    Tensor m = Tensors.matrixInt(new int[][] { { 4, 1, 0, 0 }, { 8, 0, 2, 0 }, { 10, 0, 1, 3 } });
    Tensor b = Tensors.vector(3, 4, 5);
    NavigableMap<Scalar, Tensor> navigableMap = SimplexCorners.of(c, m, b, true);
    Tensor xs = navigableMap.get(RealScalar.ZERO);
    assertEquals(Dimensions.of(xs), Arrays.asList(3, 4));
  }

  public void testP44() {
    Tensor c = Tensors.vector(1, 1, 2, 1);
    Tensor m = Tensors.matrixInt(new int[][] { { 1, 0, 2, -2 }, { 0, 1, 1, 4 } });
    Tensor b = Tensors.vector(2, 6);
    NavigableMap<Scalar, Tensor> navigableMap = SimplexCorners.of(c, m, b, true);
    assertEquals(navigableMap.size(), 4);
    Tensor tensor = navigableMap.get(RealScalar.of(5));
    assertEquals(tensor, Tensors.fromString("{{0,0,2,1}}"));
    LinearProgram lpp = LinearProgram.of( //
        Objective.MIN, c, ConstraintType.EQUALS, m, b, Variables.NON_NEGATIVE);
    assertTrue(lpp.isStandardPrimal());
    Tensor cp = SimplexCorners.of(lpp);
    assertEquals(tensor, cp);
    Tensor xp = LinearProgramming.of(lpp);
    assertEquals(xp, Tensors.vector(0, 0, 2, 1)); // as stated on p.45
  }

  public void testP45() {
    Tensor c = Tensors.vector(1, 2);
    Tensor A = Tensors.matrixInt(new int[][] { { -1, 1 }, { 0, 1 }, { 1, 1 }, { 4, 1 } });
    Tensor b = Tensors.vector(6, 8, 12, 36);
    LinearProgram lpd = LinearProgram.of( //
        Objective.MAX, c, ConstraintType.LESS_EQUALS, A, b, Variables.NON_NEGATIVE);
    assertTrue(lpd.isCanonicDual());
    Tensor solp = SimplexCorners.of(lpd);
    assertEquals(solp, Tensors.fromString("{{4, 8}}"));
    Scalar value = TestHelper.check(lpd, false);
    assertEquals(value, RealScalar.of(20));
    LinearProgram lpp = lpd.standard();
    NavigableMap<Scalar, Tensor> navigableMap = SimplexCorners.of(lpp.c, lpp.A, lpp.b, true);
    assertEquals(navigableMap.size(), 6);
    Tensor tensor = navigableMap.get(RealScalar.of(20));
    assertEquals(tensor, Tensors.fromString("{{4, 8, 2, 0, 0, 12}}")); // as stated on p.46
  }

  public void testP71_1() {
    LinearProgram lpp = LinearProgram.of( //
        Objective.MIN, Tensors.vector(3, 3), //
        ConstraintType.GREATER_EQUALS, //
        Tensors.matrixInt(new int[][] { { 2, 1 }, { 1, 2 } }), //
        Tensors.vector(4, 4), Variables.NON_NEGATIVE);
    assertTrue(lpp.isCanonicPrimal());
    TestHelper.check(lpp, true);
  }

  public void testP71_2() {
    LinearProgram lpp = LinearProgram.of( //
        Objective.MIN, Tensors.vector(4, 1, 1), //
        ConstraintType.GREATER_EQUALS, //
        Tensors.matrixInt(new int[][] { { 3, 2, 1 }, { 1, 0, 1 }, { 8, 1, 2 } }), //
        Tensors.vector(23, 10, 40), Variables.NON_NEGATIVE);
    assertTrue(lpp.isCanonicPrimal());
    TestHelper.check(lpp, false);
  }

  public void testP72_3() {
    LinearProgram lpd = LinearProgram.of( //
        Objective.MAX, Tensors.vector(-5, -35, -20), //
        ConstraintType.LESS_EQUALS, //
        Tensors.matrixInt(new int[][] { { 1, -2, -1 }, { -1, -3, 0 } }), //
        Tensors.vector(-2, -3), Variables.NON_NEGATIVE);
    TestHelper.check(lpd.toggle(), lpd);
    LinearProgram lp2 = LinearProgram.of( //
        lpd.objective.flip(), lpd.c.negate(), //
        lpd.constraintType.flipInequality(), //
        lpd.A.negate(), //
        lpd.b.negate(), Variables.NON_NEGATIVE);
    assertTrue(lp2.isCanonicPrimal());
    TestHelper.check(lp2, true);
  }

  public void testP72_4() {
    LinearProgram lpd = LinearProgram.of( //
        Objective.MAX, Tensors.vector(-2, -1, 0), //
        ConstraintType.LESS_EQUALS, //
        Tensors.matrixInt(new int[][] { { -2, 1, 1 }, { 1, 2, -1 } }), //
        Tensors.vector(-4, -6), Variables.NON_NEGATIVE);
    TestHelper.check(lpd.toggle(), lpd);
    LinearProgram lp2 = LinearProgram.of( //
        lpd.objective.flip(), lpd.c.negate(), //
        lpd.constraintType.flipInequality(), //
        lpd.A.negate(), //
        lpd.b.negate(), Variables.NON_NEGATIVE);
    assertTrue(lp2.isCanonicPrimal());
    TestHelper.check(lp2, true);
  }

  public void testP72_5() {
    LinearProgram lpd = LinearProgram.of( //
        Objective.MAX, Tensors.vector(-4, -12, -18), //
        ConstraintType.GREATER_EQUALS, //
        Tensors.matrixInt(new int[][] { { 1, 3, 0 }, { 0, 2, 2 } }), //
        Tensors.vector(3, 5), Variables.NON_NEGATIVE);
    Tensor x = LinearProgramming.of(lpd);
    lpd.requireFeasible(x);
    // System.out.println(x);
    // System.out.println(x.dot(lpd.c));
    Tensor sd = SimplexCorners.of(lpd);
    lpd.requireFeasible(sd.get(0));
    // Tensor sp = SimplexCorners.of(lpd.toggle());
    // System.out.println(sd);
    // System.out.println(sd.dot(lpd.c));
    // System.out.println(sp);
    // TestHelper.check(lpd.toggle(), lpd);
  }
}
