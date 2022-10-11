// code by jph
package ch.alpine.tensor.tmp;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;

class HoldHiSparseTest {
  @Test
  void testPack() {
    TimeSeries timeSeries = TimeSeries.path( //
        Tensors.fromString("{{2, 1}, {3, 1}, {6, 1}, {8, 2}, {10, 4}, {11, 4}}"), //
        ResamplingMethods.HOLD_VALUE_FROM_RIGHT_SPARSE);
    assertEquals(Tensors.fromString("{{2, 1}, {6, 1}, {8, 2}, {11, 4}}"), timeSeries.path());
  }

  @Test
  void testSize2() {
    Tensor in = Tensors.fromString("{{2, 1}, {3, 1}}");
    TimeSeries timeSeries = TimeSeries.path(in, //
        ResamplingMethods.HOLD_VALUE_FROM_RIGHT_SPARSE);
    assertEquals(timeSeries.path(), in);
  }
}
