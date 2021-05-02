// code by jph
package ch.alpine.tensor.io;

import ch.alpine.tensor.Tensor;
import junit.framework.TestCase;

public class StringTensorTest extends TestCase {
  public void testVector() {
    Tensor tensor = StringTensor.vector("IDSC", "ETH-Z", "ch");
    assertTrue(StringScalarQ.of(tensor.Get(0)));
    assertEquals(tensor.Get(0).toString(), "IDSC");
  }
}
