// code by jph
package ch.alpine.tensor.opt.nd;

import ch.alpine.tensor.Tensors;
import junit.framework.TestCase;

public class NdPrintTest extends TestCase {
  public void testSimple() {
    for (int n = 0; n < 10; ++n)
      for (int d = 1; d < 4; ++d) {
        NdTreeMap<String> ndTreeMap = //
            new NdTreeMap<>(Tensors.vector(-2, -3), Tensors.vector(8, 9), n, d);
        ndTreeMap.toString();
        assertTrue(ndTreeMap.isEmpty());
        ndTreeMap.add(Tensors.vector(1, 1), "d1");
        assertFalse(ndTreeMap.isEmpty());
        ndTreeMap.add(Tensors.vector(1, 0), "d2");
        ndTreeMap.add(Tensors.vector(0, 1), "d3");
        ndTreeMap.add(Tensors.vector(1, 1), "d4");
        ndTreeMap.add(Tensors.vector(0.1, 0.1), "d5");
        ndTreeMap.add(Tensors.vector(6, 7), "d6");
        NdPrint<String> ndPrint = new NdPrint<>();
        ndTreeMap.visit(ndPrint);
        ndPrint.toString();
        // System.out.println(ndPrint);
      }
  }
}
