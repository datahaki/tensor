// code by jph
package ch.alpine.tensor.sca.gam;

import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.alg.Range;
import ch.alpine.tensor.usr.AssertFail;
import junit.framework.TestCase;

public class SubfactorialTest extends TestCase {
  public void testSimple() {
    Tensor vector = Range.of(0, 10).map(Subfactorial.FUNCTION);
    Tensor expect = Tensors.vector(1, 0, 1, 2, 9, 44, 265, 1854, 14833, 133496);
    assertEquals(vector, expect);
  }

  public void testNegative() {
    AssertFail.of(() -> Subfactorial.of(-1));
  }
}
