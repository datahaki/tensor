// code by jph
package ch.alpine.tensor.tmp;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.function.BinaryOperator;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.red.Entrywise;
import ch.alpine.tensor.sca.Clip;
import ch.alpine.tensor.sca.Clips;
import ch.alpine.tensor.sca.N;

class TsEntrywiseTest {
  private static void checkSymmetric(BinaryOperator<TimeSeries> op, TimeSeries ts1, TimeSeries ts2) {
    assertTrue(TsPredicate.equals(op.apply(ts1, ts2), op.apply(ts2, ts1)));
  }

  @Test
  void testMathematica1() {
    Tensor p1 = Tensors.fromString("{{1, 3}, {4, 3}, {5, 6}, {7, 5}, {10, 2}}");
    TimeSeries ts1 = TimeSeries.path(p1, ResamplingMethod.LINEAR_INTERPOLATION);
    assertEquals(ts1.resamplingMethod(), ResamplingMethod.LINEAR_INTERPOLATION);
    assertEquals(ts1.path(), p1);
    TimeSeries ts2 = TimeSeries.path( //
        Tensors.fromString("{{2, 1}, {3, 2}, {6, 3}, {8, 2}, {10, 4}, {11, 3}}"), //
        ResamplingMethod.LINEAR_INTERPOLATION);
    Clip clip = Clips.intersection(ts1.domain(), ts2.domain());
    assertEquals(clip, Clips.interval(2, 10));
    {
      TimeSeries timeSeries = TsEntrywise.plus(ts1, ts2);
      assertEquals(timeSeries.path(), Tensors.fromString( // consistent with mathematica:
          "{{2, 4}, {3, 5}, {4, 16/3}, {5, 26/3}, {6, 17/2}, {7, 15/2}, {8, 6}, {10, 6}}"));
      checkSymmetric(TsEntrywise::plus, ts1, ts2);
    }
    {
      TimeSeries timeSeries = TsEntrywise.minus(ts1, ts2);
      assertEquals(timeSeries.path(), Tensors.fromString( // consistent with mathematica:
          "{{2, 2}, {3, 1}, {4, 2/3}, {5, 10/3}, {6, 5/2}, {7, 5/2}, {8, 2}, {10, -2}}"));
    }
    {
      TimeSeries timeSeries = TsEntrywise.times(ts1, ts2);
      assertEquals(timeSeries.path(), Tensors.fromString( // consistent with mathematica:
          "{{2, 3}, {3, 6}, {4, 7}, {5, 16}, {6, 33/2}, {7, 25/2}, {8, 8}, {10, 8}}"));
      checkSymmetric(TsEntrywise::times, ts1, ts2);
    }
    {
      TimeSeries timeSeries = TsEntrywise.min(ts1, ts2);
      String string = "{{2, 1}, {3, 2}, {4, 7/3}, {5, 8/3}, {6, 3}, {7, 5/2}, {8, 2}, {10, 2}}";
      assertEquals(timeSeries.path(), Tensors.fromString(string));
    }
    {
      TimeSeries timeSeries = TsEntrywise.max(ts1, ts2);
      String string = "{{2, 3}, {3, 3}, {4, 3}, {5, 6}, {6, 11/2}, {7, 5}, {8, 4}, {10, 4}}";
      assertEquals(timeSeries.path(), Tensors.fromString(string));
    }
  }

  @Test
  void testMathematica1Sparse() {
    Tensor p1 = Tensors.fromString("{{1, 3}, {4, 3}, {5, 6}, {7, 5}, {10, 2}}");
    TimeSeries ts1 = TimeSeries.path(p1, ResamplingMethod.LINEAR_INTERPOLATION_SPARSE);
    assertEquals(ts1.resamplingMethod(), ResamplingMethod.LINEAR_INTERPOLATION_SPARSE);
    assertEquals(ts1.path(), p1);
    TimeSeries ts2 = TimeSeries.path( //
        Tensors.fromString("{{2, 1}, {3, 2}, {6, 3}, {8, 2}, {10, 4}, {11, 3}}"), //
        ResamplingMethod.LINEAR_INTERPOLATION_SPARSE);
    Clip clip = Clips.intersection(ts1.domain(), ts2.domain());
    assertEquals(clip, Clips.interval(2, 10));
    {
      TimeSeries timeSeries = TsEntrywise.plus(ts1, ts2);
      assertEquals(timeSeries.path(), Tensors.fromString( // consistent with mathematica:
          "{{2, 4}, {3, 5}, {4, 16/3}, {5, 26/3}, {6, 17/2}, {7, 15/2}, {8, 6}, {10, 6}}"));
      checkSymmetric(TsEntrywise::plus, ts1, ts2);
    }
    {
      TimeSeries timeSeries = TsEntrywise.minus(ts1, ts2);
      assertEquals(timeSeries.path(), Tensors.fromString( // consistent with mathematica:
          "{{2, 2}, {3, 1}, {4, 2/3}, {5, 10/3}, {6, 5/2}, {7, 5/2}, {8, 2}, {10, -2}}"));
    }
    {
      TimeSeries timeSeries = TsEntrywise.times(ts1, ts2);
      assertEquals(timeSeries.path(), Tensors.fromString( // consistent with mathematica:
          "{{2, 3}, {3, 6}, {4, 7}, {5, 16}, {6, 33/2}, {7, 25/2}, {8, 8}, {10, 8}}"));
      checkSymmetric(TsEntrywise::times, ts1, ts2);
    }
    {
      TimeSeries timeSeries = TsEntrywise.min(ts1, ts2);
      String string = "{{2, 1}, {3, 2}, {4, 7/3}, {5, 8/3}, {6, 3}, {7, 5/2}, {8, 2}, {10, 2}}";
      assertEquals(timeSeries.path(), Tensors.fromString(string));
    }
    {
      TimeSeries timeSeries = TsEntrywise.max(ts1, ts2);
      String string = "{{2, 3}, {4, 3}, {5, 6}, {6, 11/2}, {7, 5}, {8, 4}, {10, 4}}";
      assertEquals(timeSeries.path(), Tensors.fromString(string));
    }
  }

  @Test
  void testMathematica2() {
    Tensor p1 = Tensors.fromString("{{1, 3}, {4, 3}, {5, 6}, {7, 5}, {10, 2}}");
    TimeSeries ts1 = TimeSeries.path(p1, ResamplingMethod.LINEAR_INTERPOLATION);
    assertEquals(ts1.resamplingMethod(), ResamplingMethod.LINEAR_INTERPOLATION);
    assertEquals(ts1.path(), p1);
    TimeSeries ts2 = TimeSeries.path( //
        Tensors.fromString("{{2, 1}, {3, 2}, {6, 3}, {8, 2}, {10, 4}, {11, 3}}"), //
        ResamplingMethod.LINEAR_INTERPOLATION);
    Clip clip = Clips.intersection(ts1.domain(), ts2.domain());
    assertEquals(clip, Clips.interval(2, 10));
    BinaryOperator<TimeSeries> binaryOperator = TimeSeriesBinaryOperator.of(Entrywise.max(), null);
    {
      TimeSeries timeSeries = binaryOperator.apply(ts1, ts2);
      assertEquals(timeSeries.path(), Tensors.fromString( //
          "{{2, 3}, {3, 3}, {4, 3}, {5, 6}, {6, 11/2}, {7, 5}, {8, 4}, {10, 4}}"));
    }
  }

  @Test
  void testExactAndInexact() {
    Tensor p1 = Tensors.fromString("{{1, 3}, {4, 3}, {5, 6}, {7, 5}, {10, 2}}");
    TimeSeries ts1 = TimeSeries.path(p1, ResamplingMethod.LINEAR_INTERPOLATION);
    TimeSeries ts2 = TimeSeries.path(p1.maps(N.DOUBLE), ResamplingMethod.LINEAR_INTERPOLATION);
    TimeSeries timeSeries = TsEntrywise.plus(ts1, ts2);
    assertEquals(timeSeries.size(), 5);
  }

  @Test
  void testEmpty() {
    Tensor p1 = Tensors.fromString("{{1, 3}, {4, 3}, {5, 6}, {7, 5}, {10, 2}}");
    TimeSeries ts1 = TimeSeries.path(p1, ResamplingMethod.LINEAR_INTERPOLATION);
    assertTrue(ts1.containsKey(RealScalar.ONE));
    assertFalse(ts1.containsKey(RealScalar.of(2)));
    TimeSeries ts2 = TimeSeries.empty(ResamplingMethod.HOLD_VALUE_FROM_LEFT);
    assertTrue(TsEntrywise.plus(ts1, ts2).isEmpty());
    assertTrue(TsEntrywise.plus(ts2, ts1).isEmpty());
    assertTrue(TsEntrywise.plus(ts2, ts2).isEmpty());
    TimeSeries timeSeries = ts1.block(Clips.interval(2, 7), true);
    assertEquals(timeSeries.size(), 3);
  }

  @Test
  void testNonOverlap() {
    Tensor p1 = Tensors.fromString("{{1, 3}, {4, 3}, {5, 6}}");
    Tensor p2 = Tensors.fromString("{{7, 5}, {10, 2}}");
    TimeSeries ts1 = TimeSeries.path(p1, ResamplingMethod.LINEAR_INTERPOLATION);
    TimeSeries ts2 = TimeSeries.path(p2, ResamplingMethod.HOLD_VALUE_FROM_LEFT);
    assertTrue(TsEntrywise.plus(ts1, ts2).isEmpty());
    assertTrue(TsEntrywise.plus(ts2, ts1).isEmpty());
    assertFalse(TsPredicate.equals(ts1, ts2));
  }
}
