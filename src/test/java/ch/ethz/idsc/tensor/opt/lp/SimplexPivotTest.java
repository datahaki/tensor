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
import ch.ethz.idsc.tensor.alg.Join;
import ch.ethz.idsc.tensor.mat.IdentityMatrix;
import ch.ethz.idsc.tensor.opt.lp.LinearProgram.ConstraintType;
import ch.ethz.idsc.tensor.opt.lp.LinearProgram.CostType;
import ch.ethz.idsc.tensor.opt.lp.LinearProgram.RegionType;
import ch.ethz.idsc.tensor.qty.Quantity;
import ch.ethz.idsc.tensor.sca.Chop;
import junit.framework.TestCase;

/** Reference: Linear and Integer Programming made Easy, 2016 */
public class SimplexPivotTest extends TestCase {
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
    LinearProgram lpp = LinearProgram.of(CostType.MAX, c, ConstraintType.LESS_EQUALS, m, b, RegionType.NON_NEGATIVE);
    Tensor solp = SimplexCorners.of(lpp);
    assertEquals(xs, solp);
    Tensor sold = SimplexCorners.of(lpp.dual());
    assertEquals(sold, Tensors.fromString("{{1/10[USD*Wood^-1], 7/15[Iron^-1*USD]}}"));
    Tensor sole = SimplexCorners.of(lpp.dual().equality());
    assertEquals(sold, sole);
  }

  public void testP18_2() {
    Tensor c = Tensors.fromString("{12[USD], 10[USD], 1[USD]}");
    Tensor m = Tensors.fromString("{{11[lb], 10[lb], 9[lb]}}");
    Tensor b = Tensors.fromString("{20[lb]}");
    NavigableMap<Scalar, Tensor> navigableMap = SimplexCorners.of(c, m, b, true);
    Tensor sol1 = navigableMap.get(Quantity.of(RationalScalar.of(240, 11), "USD"));
    assertEquals(sol1, Tensors.fromString("{{20/11, 0, 0}}"));
    LinearProgram lp1 = LinearProgram.of(CostType.MAX, c, ConstraintType.LESS_EQUALS, m, b, RegionType.NON_NEGATIVE);
    Tensor sol2 = SimplexCorners.of(lp1.equality());
    assertEquals(sol1, sol2);
  }

  public void testP18_3() {
    Tensor c = Tensors.vector(1, 1, 1);
    Tensor m = Tensors.fromString("{{6,3,1},{4,5,6}}");
    Tensor b = Tensors.vector(15, 15);
    LinearProgram lpp = LinearProgram.of(CostType.MAX, c, ConstraintType.LESS_EQUALS, m, b, RegionType.NON_NEGATIVE);
    Tensor solp = SimplexCorners.of(lpp.equality());
    assertEquals(solp.dot(lpp.c), Tensors.of(RationalScalar.of(10, 3)));
    LinearProgram lpd = lpp.dual();
    Tensor sold = SimplexCorners.of(lpd.equality());
    assertEquals(sold.dot(lpd.c), Tensors.of(RationalScalar.of(10, 3)));
  }

  public void testP21() {
    Tensor c = Tensors.fromString("{1[USD], 1[USD], 1[USD]}");
    Tensor m = Tensors.fromString("{{4[ap], 1[ap], 3[ap]}, {1[or], 4[or], 2[or]}}");
    Tensor b = Tensors.fromString("{15[ap], 15[or]}");
    NavigableMap<Scalar, Tensor> navigableMap = SimplexCorners.of(c, m, b, true);
    Map<Scalar, Tensor> map2 = Collections.singletonMap(Quantity.of(6, "USD"), Tensors.fromString("{{3, 3, 0}, {0, 3/2, 9/2}}"));
    assertEquals(navigableMap, map2);
    LinearProgram lpp = LinearProgram.of(CostType.MAX, c, ConstraintType.LESS_EQUALS, m, b, RegionType.NON_NEGATIVE);
    Tensor solp = SimplexCorners.of(lpp);
    assertEquals(solp, Tensors.fromString("{{3, 3, 0}, {0, 3/2, 9/2}}"));
    assertEquals(solp.dot(lpp.c), Tensors.fromString("{6[USD], 6[USD]}"));
    LinearProgram lpd = lpp.dual();
    Tensor sold = SimplexCorners.of(lpd.equality());
    assertEquals(sold, Tensors.fromString("{{1/5[USD*ap^-1], 1/5[USD*or^-1]}}"));
    assertEquals(sold.dot(lpd.c), Tensors.fromString("{6[USD]}"));
  }

  public void testP23() {
    Tensor c = Tensors.fromString("{15[USD], 7[USD], 4[USD], 6[USD]}");
    Tensor m = Tensors.fromString("{{3[va], 1[va], 0[va], -1[va]}, {1[vb], 1[vb], 1[vb], 2[vb]}}");
    Tensor b = Tensors.fromString("{3[va], 5[vb]}");
    NavigableMap<Scalar, Tensor> navigableMap = SimplexCorners.of(c, m, b, true);
    Scalar key = Quantity.of(29, "USD");
    Tensor tensor = navigableMap.get(key);
    assertEquals(tensor, Tensors.fromString("{{0, 3, 2, 0}}"));
    LinearProgram lpp = LinearProgram.of(CostType.MIN, c, ConstraintType.GREATER_EQUALS, m, b, RegionType.NON_NEGATIVE);
    Tensor solp = SimplexCorners.of(lpp);
    assertEquals(tensor, solp);
    assertEquals(solp.dot(lpp.c).Get(0), key);
    LinearProgram lpd = lpp.dual();
    String string = "{{3[va], 1[vb]}, {1[va], 1[vb]}, {0[va], 1[vb]}, {-1[va], 2[vb]}}";
    assertEquals(lpd.A, Tensors.fromString(string));
    assertEquals(lpd.b, c);
    Tensor sold = SimplexCorners.of(lpd.equality());
    assertEquals(sold, Tensors.fromString("{{3[USD*va^-1], 4[USD*vb^-1]}}"));
    assertEquals(sold.dot(lpd.c).Get(0), key);
  }

  public void testP42() {
    Tensor c = Tensors.fromString("{0,0,0,0}");
    Tensor m = Tensors.fromString("{{4,1,0,0},{8,0,2,0},{10,0,1,3}}");
    Tensor b = Tensors.fromString("{3,4,5}");
    NavigableMap<Scalar, Tensor> navigableMap = SimplexCorners.of(c, m, b, true);
    Tensor xs = navigableMap.get(RealScalar.ZERO);
    assertEquals(Dimensions.of(xs), Arrays.asList(3, 4));
  }

  public void testP44() {
    Tensor c = Tensors.fromString("{1,1,2,1}");
    Tensor m = Tensors.fromString("{{1,0,2,-2},{0,1,1,4}}");
    Tensor b = Tensors.fromString("{2,6}");
    NavigableMap<Scalar, Tensor> navigableMap = SimplexCorners.of(c, m, b, true);
    assertEquals(navigableMap.size(), 4);
    Tensor tensor = navigableMap.get(RealScalar.of(5));
    assertEquals(tensor, Tensors.fromString("{{0,0,2,1}}"));
    LinearProgram lpe = LinearProgram.of(CostType.MIN, c, ConstraintType.EQUALS, m, b, RegionType.NON_NEGATIVE);
    Tensor sole = SimplexCorners.of(lpe);
    assertEquals(tensor, sole);
  }

  public void testP45manual() {
    Tensor c = Tensors.fromString("{1,2}");
    c = Join.of(c, Array.zeros(4));
    Tensor m = Tensors.fromString("{{-1,1},{0,1},{1,1},{4,1}}");
    m = Join.of(1, m, IdentityMatrix.of(4));
    Tensor b = Tensors.fromString("{6,8,12,36}");
    NavigableMap<Scalar, Tensor> navigableMap = SimplexCorners.of(c, m, b, true);
    assertEquals(navigableMap.size(), 6);
    Tensor tensor = navigableMap.get(RealScalar.of(20));
    assertEquals(tensor, Tensors.fromString("{{4, 8, 2, 0, 0, 12}}"));
  }

  public void testP45auto() {
    Tensor c = Tensors.fromString("{1,2}");
    Tensor m = Tensors.fromString("{{-1,1},{0,1},{1,1},{4,1}}");
    Tensor b = Tensors.fromString("{6,8,12,36}");
    LinearProgram lpp = LinearProgram.of(CostType.MAX, c, ConstraintType.LESS_EQUALS, m, b, RegionType.NON_NEGATIVE);
    Tensor solp = SimplexCorners.of(lpp.equality());
    assertEquals(solp, Tensors.fromString("{{4, 8}}"));
  }
}
