// code by jph
package ch.alpine.tensor.img;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.io.StringScalarQ;

class ColorNegateTest {
  @Test
  void testGray() {
    Tensor inp = Tensors.fromString("{{1,3,10,255,10}}");
    Tensor res = ColorNegate.of(inp);
    assertEquals(res, Tensors.fromString("{{254, 252, 245, 0, 245}}"));
  }

  @Test
  void testColor() {
    Tensor inp = Tensors.fromString("{{{1,3,10,255},{10,255,200,10}}}");
    assertFalse(StringScalarQ.any(inp));
    Tensor res = ColorNegate.of(inp);
    assertEquals(res, Tensors.fromString("{{{254, 252, 245, 255}, {245, 0, 55, 10}}}"));
  }
}
