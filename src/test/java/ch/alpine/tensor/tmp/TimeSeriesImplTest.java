// code by jph
package ch.alpine.tensor.tmp;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.ext.Serialization;
import ch.alpine.tensor.qty.DateTime;
import ch.alpine.tensor.qty.Quantity;
import ch.alpine.tensor.qty.UnitSystem;
import ch.alpine.tensor.sca.Clip;
import test.wrap.SerializableQ;

class TimeSeriesImplTest {
  @Test
  void testInterp1() {
    TimeSeries timeSeries = TimeSeries.empty(ResamplingMethod.LINEAR_INTERPOLATION);
    assertEquals(timeSeries.size(), 0);
    assertTrue(timeSeries.isEmpty());
    DateTime dateTime = DateTime.of(1993, 4, 5, 4, 5);
    assertThrows(Exception.class, () -> timeSeries.insert(dateTime, null));
    timeSeries.insert(dateTime, Tensors.vector(1, 2, 3));
    TimeSeries copy = timeSeries.copy();
    timeSeries.insert(dateTime.add(Quantity.of(3, "h")), Tensors.vector(2, 3, 0));
    timeSeries.insert(dateTime.add(Quantity.of(24, "h")), Tensors.vector(0, 0, 6));
    assertEquals(timeSeries.size(), 3);
    assertEquals(timeSeries.evaluate(dateTime.add(Quantity.of(1, "h"))), Tensors.fromString("{4/3, 7/3, 2}"));
    assertEquals(timeSeries.evaluate(dateTime.add(Quantity.of(0, "h"))), Tensors.vector(1, 2, 3));
    assertEquals(timeSeries.evaluate(dateTime.add(Quantity.of(3, "h"))), Tensors.vector(2, 3, 0));
    assertEquals(timeSeries.evaluate(dateTime.add(Quantity.of(24, "h"))), Tensors.vector(0, 0, 6));
    assertThrows(Exception.class, () -> timeSeries.evaluate(dateTime.add(Quantity.of(-1, "h"))));
    assertThrows(Exception.class, () -> timeSeries.evaluate(dateTime.add(Quantity.of(25, "h"))));
    assertEquals(copy.size(), 1);
    assertTrue(timeSeries.toString().startsWith("TimeSeries["));
    TimeSeries copy_unmodif = SerializableQ.require(timeSeries.unmodifiable());
    assertThrows(Exception.class, () -> copy_unmodif.insert(dateTime, Tensors.vector(0, 0, 6)));
    Tensor integral = TimeSeriesIntegrate.of(timeSeries, timeSeries.domain());
    assertEquals(integral, Tensors.fromString("{91800[s], 140400[s], 243000[s]}"));
    TimeSeries integrate = TimeSeriesIntegrate.of(timeSeries);
    assertEquals(integrate.evaluate(timeSeries.domain().max()), integral);
    Scalar scalar = MinimumTimeIncrement.of(timeSeries);
    assertEquals(scalar, Quantity.of(10800, "s"));
  }

  @Test
  void testHoldLo() {
    TimeSeries timeSeries = TimeSeries.empty(ResamplingMethod.HOLD_VALUE_FROM_LEFT);
    assertEquals(timeSeries.size(), 0);
    assertTrue(timeSeries.isEmpty());
    DateTime dateTime = DateTime.of(1993, 4, 5, 4, 5);
    assertThrows(Exception.class, () -> timeSeries.insert(dateTime, null));
    timeSeries.insert(dateTime, Tensors.vector(1, 2, 3));
    TimeSeries copy = timeSeries.copy();
    timeSeries.insert(dateTime.add(Quantity.of(3, "h")), Tensors.vector(2, 3, 0));
    timeSeries.insert(dateTime.add(Quantity.of(4, "h")), Tensors.vector(2, 3, 0));
    timeSeries.insert(dateTime.add(Quantity.of(24, "h")), Tensors.vector(0, 0, 6));
    assertDoesNotThrow(() -> Serialization.copy(timeSeries));
    assertEquals(timeSeries.size(), 4);
    assertFalse(timeSeries.isEmpty());
    Clip clip = timeSeries.domain();
    assertEquals(clip.width(), UnitSystem.SI().apply(Quantity.of(24, "h")));
    assertEquals(timeSeries.evaluate(dateTime.add(Quantity.of(0, "h"))), Tensors.vector(1, 2, 3));
    assertEquals(timeSeries.evaluate(dateTime.add(Quantity.of(1, "h"))), Tensors.vector(1, 2, 3));
    assertThrows(Exception.class, () -> timeSeries.evaluate(dateTime.add(Quantity.of(-1, "h"))));
    assertThrows(Exception.class, () -> timeSeries.evaluate(dateTime.add(Quantity.of(25, "h"))));
    assertEquals(copy.size(), 1);
    Tensor integral = TimeSeriesIntegrate.of(timeSeries, timeSeries.domain());
    assertEquals(integral, Tensors.fromString("{162000[s], 248400[s], 32400[s]}"));
    TimeSeries integrate = TimeSeriesIntegrate.of(timeSeries);
    assertEquals(integrate.evaluate(timeSeries.domain().max()), integral);
  }

  @Test
  void testHoldLoSparse() {
    TimeSeries timeSeries = TimeSeries.empty(ResamplingMethod.HOLD_VALUE_FROM_LEFT_SPARSE);
    assertEquals(timeSeries.size(), 0);
    assertTrue(timeSeries.isEmpty());
    DateTime dateTime = DateTime.of(1993, 4, 5, 4, 5);
    assertThrows(Exception.class, () -> timeSeries.insert(dateTime, null));
    timeSeries.insert(dateTime, Tensors.vector(1, 2, 3));
    TimeSeries copy = timeSeries.copy();
    timeSeries.insert(dateTime.add(Quantity.of(3, "h")), Tensors.vector(2, 3, 0));
    timeSeries.insert(dateTime.add(Quantity.of(4, "h")), Tensors.vector(2, 3, 0));
    timeSeries.insert(dateTime.add(Quantity.of(24, "h")), Tensors.vector(0, 0, 6));
    SerializableQ.require(timeSeries);
    assertEquals(timeSeries.size(), 3);
    assertFalse(timeSeries.isEmpty());
    Clip clip = timeSeries.domain();
    assertEquals(clip.width(), UnitSystem.SI().apply(Quantity.of(24, "h")));
    assertEquals(timeSeries.evaluate(dateTime.add(Quantity.of(0, "h"))), Tensors.vector(1, 2, 3));
    assertEquals(timeSeries.evaluate(dateTime.add(Quantity.of(1, "h"))), Tensors.vector(1, 2, 3));
    assertThrows(Exception.class, () -> timeSeries.evaluate(dateTime.add(Quantity.of(-1, "h"))));
    assertThrows(Exception.class, () -> timeSeries.evaluate(dateTime.add(Quantity.of(25, "h"))));
    assertEquals(copy.size(), 1);
    Tensor integral = TimeSeriesIntegrate.of(timeSeries, timeSeries.domain());
    assertEquals(integral, Tensors.fromString("{162000[s], 248400[s], 32400[s]}"));
    TimeSeries integrate = TimeSeriesIntegrate.of(timeSeries);
    assertEquals(integrate.evaluate(timeSeries.domain().max()), integral);
  }

  @Test
  void testHoldHi() {
    TimeSeries timeSeries = TimeSeries.empty(ResamplingMethod.HOLD_VALUE_FROM_RIGHT_SPARSE);
    assertEquals(timeSeries.size(), 0);
    assertTrue(timeSeries.isEmpty());
    DateTime dateTime = DateTime.of(1993, 4, 5, 4, 5);
    assertThrows(Exception.class, () -> timeSeries.insert(dateTime, null));
    timeSeries.insert(dateTime, Tensors.vector(1, 2, 3));
    TimeSeries copy = timeSeries.copy();
    timeSeries.insert(dateTime.add(Quantity.of(4, "h")), Tensors.vector(2, 3, 0));
    assertThrows(Exception.class, () -> timeSeries.insert(dateTime.add(Quantity.of(3, "h")), Tensors.vector(2, 3, 0, 2)));
    timeSeries.insert(dateTime.add(Quantity.of(3, "h")), Tensors.vector(2, 3, 0));
    timeSeries.insert(dateTime.add(Quantity.of(24, "h")), Tensors.vector(0, 0, 6));
    SerializableQ.require(timeSeries);
    assertEquals(timeSeries.size(), 3);
    assertFalse(timeSeries.isEmpty());
    Clip clip = timeSeries.domain();
    assertEquals(clip.width(), UnitSystem.SI().apply(Quantity.of(24, "h")));
    assertEquals(timeSeries.evaluate(dateTime.add(Quantity.of(0, "h"))), Tensors.vector(1, 2, 3));
    assertEquals(timeSeries.evaluate(dateTime.add(Quantity.of(1, "h"))), Tensors.vector(2, 3, 0));
    assertThrows(Exception.class, () -> timeSeries.evaluate(dateTime.add(Quantity.of(-1, "h"))));
    assertThrows(Exception.class, () -> timeSeries.evaluate(dateTime.add(Quantity.of(25, "h"))));
    assertEquals(copy.size(), 1);
    Tensor integral = TimeSeriesIntegrate.of(timeSeries, timeSeries.domain());
    assertEquals(integral, Tensors.fromString("{28800[s], 43200[s], 432000[s]}"));
    TimeSeries integrate = TimeSeriesIntegrate.of(timeSeries);
    assertEquals(integrate.evaluate(timeSeries.domain().max()), integral);
  }
}
