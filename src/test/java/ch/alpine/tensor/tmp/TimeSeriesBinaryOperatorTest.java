// code by jph
package ch.alpine.tensor.tmp;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import java.util.function.BinaryOperator;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;

class TimeSeriesBinaryOperatorTest {
  @Test
  void testSimple() {
    BinaryOperator<TimeSeries> binaryOperator = //
        TimeSeriesBinaryOperator.of((t, b) -> b, ResamplingMethods.HOLD_VALUE_FROM_LEFT);
    Tensor p1 = Tensors.fromString("{{1, 3}, {4, 3}, {5, 6}, {7, 5}, {10, 2}}");
    TimeSeries ts1 = TimeSeries.path(p1, ResamplingMethods.LINEAR_INTERPOLATION);
    assertEquals(ts1.resamplingMethod(), ResamplingMethods.LINEAR_INTERPOLATION);
    assertEquals(ts1.path(), p1);
    TimeSeries ts2 = TimeSeries.path( //
        Tensors.fromString("{{2, 1}, {3, 2}, {6, 3}, {8, 2}, {10, 4}, {11, 3}}"), //
        ResamplingMethods.LINEAR_INTERPOLATION);
    assertFalse(TsPredicate.equals(ts1, ts2));
    assertFalse(TsPredicate.equals(ts2, binaryOperator.apply(ts1, ts2)));
  }
}
