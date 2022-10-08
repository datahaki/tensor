// code by jph
package ch.alpine.tensor.tmp;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;

class MinimumTimeIncrementTest {
  @Test
  void testSimple() {
    Tensor p1 = Tensors.fromString("{{1, 3}, {4, 3}, {5, 6}, {7, 5}, {10, 2}}");
    TimeSeries timeSeries = TimeSeries.path(p1, ResamplingMethods.LINEAR_INTERPOLATION);
    Scalar scalar = MinimumTimeIncrement.of(timeSeries);
    assertEquals(scalar, RealScalar.of(1));
  }
}
