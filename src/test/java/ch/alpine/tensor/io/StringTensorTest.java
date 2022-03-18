// code by jph
package ch.alpine.tensor.io;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.Tensor;

public class StringTensorTest {
  @Test
  public void testVector() {
    Tensor tensor = StringTensor.vector("IDSC", "ETH-Z", "ch");
    assertTrue(StringScalarQ.of(tensor.Get(0)));
    assertEquals(tensor.Get(0).toString(), "IDSC");
  }
}