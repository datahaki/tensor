// code by jph
package ch.alpine.tensor.mat.pi;

import ch.alpine.tensor.Tensors;
import junit.framework.TestCase;

public class StaticHelperTest extends TestCase {
  public void testSimple() {
    assertFalse(StaticHelper.isMixedUnits(Tensors.fromString("{{1,2,3}}")));
    assertFalse(StaticHelper.isMixedUnits(Tensors.fromString("{{1[m],2[m],3[m]}}")));
    assertTrue(StaticHelper.isMixedUnits(Tensors.fromString("{{1[m],2,3[m]}}")));
    assertTrue(StaticHelper.isMixedUnits(Tensors.fromString("{{1[m],2[kg],3[m]}}")));
  }
}
