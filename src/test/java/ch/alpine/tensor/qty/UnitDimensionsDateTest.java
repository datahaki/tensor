// code by jph
package ch.alpine.tensor.qty;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;

class UnitDimensionsDateTest {
  @Test
  void testAddSeconds() {
    DateTime dateTime = DateTime.now();
    Scalar scalar = Quantity.of(2, "s");
    Scalar res1 = UnitDimensionsDate.INSTANCE.plus(scalar, dateTime);
    Scalar res2 = dateTime.add(scalar);
    assertEquals(res1, res2);
    assertThrows(Exception.class, () -> UnitDimensionsDate.INSTANCE.plus(RealScalar.TWO, dateTime));
  }
}
