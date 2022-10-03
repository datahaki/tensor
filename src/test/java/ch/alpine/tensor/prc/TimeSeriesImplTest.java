// code by jph
package ch.alpine.tensor.prc;

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
    assertEquals(timeSeries.size(), 3);
    assertFalse(timeSeries.isEmpty());
    Clip clip = timeSeries.support();
    assertEquals(clip.width(), UnitSystem.SI().apply(Quantity.of(24, "h")));
    assertEquals(timeSeries.eval(dateTime.add(Quantity.of(0, "h"))), Tensors.vector(1, 2, 3));
    assertEquals(timeSeries.eval(dateTime.add(Quantity.of(1, "h"))), Tensors.vector(1, 2, 3));
    assertThrows(Exception.class, () -> timeSeries.eval(dateTime.add(Quantity.of(-1, "h"))));
    assertThrows(Exception.class, () -> timeSeries.eval(dateTime.add(Quantity.of(25, "h"))));
    assertEquals(copy.size(), 1);
    Tensor integral = TimeSeriesOp.integrate(timeSeries);
    assertEquals(integral, Tensors.fromString("{162000[s], 248400[s], 32400[s]}"));
  }

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
  }
}
