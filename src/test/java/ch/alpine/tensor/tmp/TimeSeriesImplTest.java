// code by jph
package ch.alpine.tensor.tmp;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.ext.Serialization;
import ch.alpine.tensor.qty.DateTime;
import ch.alpine.tensor.qty.Quantity;
import ch.alpine.tensor.qty.UnitSystem;
import ch.alpine.tensor.sca.Clip;

class TimeSeriesImplTest {
  @Test
  void testInterp1() throws ClassNotFoundException, IOException {
    TimeSeries timeSeries = TimeSeries.empty(ResamplingMethods.INTERPOLATION_1);
    assertEquals(timeSeries.size(), 0);
    assertTrue(timeSeries.isEmpty());
    DateTime dateTime = DateTime.of(1993, 4, 5, 4, 5);
    assertThrows(Exception.class, () -> timeSeries.insert(dateTime, null));
    timeSeries.insert(dateTime, Tensors.vector(1, 2, 3));
    TimeSeries copy = timeSeries.copy();
    timeSeries.insert(dateTime.add(Quantity.of(3, "h")), Tensors.vector(2, 3, 0));
    timeSeries.insert(dateTime.add(Quantity.of(24, "h")), Tensors.vector(0, 0, 6));
    assertEquals(timeSeries.size(), 3);
    assertEquals(timeSeries.eval(dateTime.add(Quantity.of(1, "h"))), Tensors.fromString("{4/3, 7/3, 2}"));
    assertEquals(timeSeries.eval(dateTime.add(Quantity.of(0, "h"))), Tensors.vector(1, 2, 3));
    assertEquals(timeSeries.eval(dateTime.add(Quantity.of(3, "h"))), Tensors.vector(2, 3, 0));
    assertEquals(timeSeries.eval(dateTime.add(Quantity.of(24, "h"))), Tensors.vector(0, 0, 6));
    assertThrows(Exception.class, () -> timeSeries.eval(dateTime.add(Quantity.of(-1, "h"))));
    assertThrows(Exception.class, () -> timeSeries.eval(dateTime.add(Quantity.of(25, "h"))));
    assertEquals(copy.size(), 1);
    assertTrue(timeSeries.toString().startsWith("TimeSeries["));
    TimeSeries copy_unmodif = Serialization.copy(timeSeries.unmodifiable());
    assertThrows(Exception.class, () -> copy_unmodif.insert(dateTime, Tensors.vector(0, 0, 6)));
    Tensor integral = TimeSeriesIntegrate.of(timeSeries, timeSeries.support());
    assertEquals(integral, Tensors.fromString("{91800[s], 140400[s], 243000[s]}"));
    TimeSeries integrate = TimeSeriesIntegrate.of(timeSeries);
    assertEquals(integrate.eval(timeSeries.support().max()), integral);
  }

  @Test
  void testHoldLo() throws ClassNotFoundException, IOException {
    TimeSeries timeSeries = TimeSeries.empty(ResamplingMethods.HOLD_LO);
    assertEquals(timeSeries.size(), 0);
    assertTrue(timeSeries.isEmpty());
    DateTime dateTime = DateTime.of(1993, 4, 5, 4, 5);
    assertThrows(Exception.class, () -> timeSeries.insert(dateTime, null));
    timeSeries.insert(dateTime, Tensors.vector(1, 2, 3));
    TimeSeries copy = timeSeries.copy();
    timeSeries.insert(dateTime.add(Quantity.of(3, "h")), Tensors.vector(2, 3, 0));
    timeSeries.insert(dateTime.add(Quantity.of(4, "h")), Tensors.vector(2, 3, 0));
    timeSeries.insert(dateTime.add(Quantity.of(24, "h")), Tensors.vector(0, 0, 6));
    Serialization.copy(timeSeries);
    assertEquals(timeSeries.size(), 4);
    assertFalse(timeSeries.isEmpty());
    Clip clip = timeSeries.support();
    assertEquals(clip.width(), UnitSystem.SI().apply(Quantity.of(24, "h")));
    assertEquals(timeSeries.eval(dateTime.add(Quantity.of(0, "h"))), Tensors.vector(1, 2, 3));
    assertEquals(timeSeries.eval(dateTime.add(Quantity.of(1, "h"))), Tensors.vector(1, 2, 3));
    assertThrows(Exception.class, () -> timeSeries.eval(dateTime.add(Quantity.of(-1, "h"))));
    assertThrows(Exception.class, () -> timeSeries.eval(dateTime.add(Quantity.of(25, "h"))));
    assertEquals(copy.size(), 1);
    Tensor integral = TimeSeriesIntegrate.of(timeSeries, timeSeries.support());
    assertEquals(integral, Tensors.fromString("{162000[s], 248400[s], 32400[s]}"));
    TimeSeries integrate = TimeSeriesIntegrate.of(timeSeries);
    assertEquals(integrate.eval(timeSeries.support().max()), integral);
  }

  @Test
  void testHoldLoSparse() throws ClassNotFoundException, IOException {
    TimeSeries timeSeries = TimeSeries.empty(ResamplingMethods.HOLD_LO_SPARSE);
    assertEquals(timeSeries.size(), 0);
    assertTrue(timeSeries.isEmpty());
    DateTime dateTime = DateTime.of(1993, 4, 5, 4, 5);
    assertThrows(Exception.class, () -> timeSeries.insert(dateTime, null));
    timeSeries.insert(dateTime, Tensors.vector(1, 2, 3));
    TimeSeries copy = timeSeries.copy();
    timeSeries.insert(dateTime.add(Quantity.of(3, "h")), Tensors.vector(2, 3, 0));
    timeSeries.insert(dateTime.add(Quantity.of(4, "h")), Tensors.vector(2, 3, 0));
    timeSeries.insert(dateTime.add(Quantity.of(24, "h")), Tensors.vector(0, 0, 6));
    Serialization.copy(timeSeries);
    assertEquals(timeSeries.size(), 3);
    assertFalse(timeSeries.isEmpty());
    Clip clip = timeSeries.support();
    assertEquals(clip.width(), UnitSystem.SI().apply(Quantity.of(24, "h")));
    assertEquals(timeSeries.eval(dateTime.add(Quantity.of(0, "h"))), Tensors.vector(1, 2, 3));
    assertEquals(timeSeries.eval(dateTime.add(Quantity.of(1, "h"))), Tensors.vector(1, 2, 3));
    assertThrows(Exception.class, () -> timeSeries.eval(dateTime.add(Quantity.of(-1, "h"))));
    assertThrows(Exception.class, () -> timeSeries.eval(dateTime.add(Quantity.of(25, "h"))));
    assertEquals(copy.size(), 1);
    Tensor integral = TimeSeriesIntegrate.of(timeSeries, timeSeries.support());
    assertEquals(integral, Tensors.fromString("{162000[s], 248400[s], 32400[s]}"));
    TimeSeries integrate = TimeSeriesIntegrate.of(timeSeries);
    assertEquals(integrate.eval(timeSeries.support().max()), integral);
  }

  @Test
  void testHoldHi() throws ClassNotFoundException, IOException {
    TimeSeries timeSeries = TimeSeries.empty(ResamplingMethods.HOLD_HI);
    assertEquals(timeSeries.size(), 0);
    assertTrue(timeSeries.isEmpty());
    DateTime dateTime = DateTime.of(1993, 4, 5, 4, 5);
    assertThrows(Exception.class, () -> timeSeries.insert(dateTime, null));
    timeSeries.insert(dateTime, Tensors.vector(1, 2, 3));
    TimeSeries copy = timeSeries.copy();
    timeSeries.insert(dateTime.add(Quantity.of(4, "h")), Tensors.vector(2, 3, 0));
    timeSeries.insert(dateTime.add(Quantity.of(3, "h")), Tensors.vector(2, 3, 0));
    timeSeries.insert(dateTime.add(Quantity.of(24, "h")), Tensors.vector(0, 0, 6));
    Serialization.copy(timeSeries);
    assertEquals(timeSeries.size(), 3);
    assertFalse(timeSeries.isEmpty());
    Clip clip = timeSeries.support();
    assertEquals(clip.width(), UnitSystem.SI().apply(Quantity.of(24, "h")));
    assertEquals(timeSeries.eval(dateTime.add(Quantity.of(0, "h"))), Tensors.vector(1, 2, 3));
    assertEquals(timeSeries.eval(dateTime.add(Quantity.of(1, "h"))), Tensors.vector(2, 3, 0));
    assertThrows(Exception.class, () -> timeSeries.eval(dateTime.add(Quantity.of(-1, "h"))));
    assertThrows(Exception.class, () -> timeSeries.eval(dateTime.add(Quantity.of(25, "h"))));
    assertEquals(copy.size(), 1);
    Tensor integral = TimeSeriesIntegrate.of(timeSeries, timeSeries.support());
    assertEquals(integral, Tensors.fromString("{28800[s], 43200[s], 432000[s]}"));
    TimeSeries integrate = TimeSeriesIntegrate.of(timeSeries);
    assertEquals(integrate.eval(timeSeries.support().max()), integral);
  }
}
