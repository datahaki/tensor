// code by jph
package ch.alpine.tensor.tmp;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.num.Pi;
import ch.alpine.tensor.qty.DateTime;
import ch.alpine.tensor.qty.Quantity;

class MinimumTimeIncrementTest {
  @Test
  void testSimple() {
    Tensor p1 = Tensors.fromString("{{1, 3}, {4, 3}, {5, 6}, {7, 5}, {10, 2}}");
    TimeSeries timeSeries = TimeSeries.path(p1, ResamplingMethod.LINEAR_INTERPOLATION);
    Scalar scalar = MinimumTimeIncrement.of(timeSeries);
    assertEquals(scalar, RealScalar.of(1));
  }

  @Test
  void testDateTime() {
    TimeSeries timeSeries = TimeSeries.empty(ResamplingMethod.LINEAR_INTERPOLATION);
    timeSeries.insert(DateTime.of(1980, 10, 2, 3, 4), Pi.VALUE);
    timeSeries.insert(DateTime.of(1982, 3, 20, 4, 4, 22), Pi.TWO);
    timeSeries.insert(DateTime.of(1981, 11, 2, 3, 4, 22), Pi.HALF);
    Scalar scalar = MinimumTimeIncrement.of(timeSeries);
    assertEquals(scalar, Quantity.of(11926800, "s"));
  }
}
