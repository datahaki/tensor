// code by jph
package ch.ethz.idsc.tensor.opt.lp;

import java.io.IOException;
import java.util.NavigableMap;

import ch.ethz.idsc.tensor.RationalScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.ext.Serialization;
import ch.ethz.idsc.tensor.opt.lp.LinearProgram.ConstraintType;
import ch.ethz.idsc.tensor.opt.lp.LinearProgram.Objective;
import ch.ethz.idsc.tensor.opt.lp.LinearProgram.RegionType;
import ch.ethz.idsc.tensor.qty.Quantity;
import ch.ethz.idsc.tensor.sca.Chop;
import junit.framework.TestCase;

public class SimplexPivotsTest extends TestCase {
  public void testSerializable() throws ClassNotFoundException, IOException {
    for (SimplexPivot simplexPivot : SimplexPivots.values())
      Serialization.copy(simplexPivot);
  }

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
    LinearProgram lpp = LinearProgram.of(Objective.MAX, c, ConstraintType.LESS_EQUALS, m, b, RegionType.NON_NEGATIVE);
    Tensor solp = SimplexCorners.of(lpp);
    assertEquals(xs, solp);
    LinearProgram lpd = lpp.dual();
    Tensor sold = SimplexCorners.of(lpd);
    assertEquals(sold, Tensors.fromString("{{1/10[USD*Wood^-1], 7/15[Iron^-1*USD]}}"));
    // System.out.println(Pretty.of(lpd.A));
    // System.out.println(Pretty.of(lpd.b));
    // Tensor soll = LinearProgramming.of(lpp);
    // assertEquals(soll, xs.get(0));
    // Tensor sole = LinearProgramming.of(lpd);
  }
}
