// code by jph
package ch.alpine.tensor.qty;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Comparator;
import java.util.Properties;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.Throw;
import ch.alpine.tensor.alg.Sort;
import test.wrap.SerializableQ;

class QuantityComparatorTest {
  @Test
  void testSimple() {
    Comparator<Scalar> comparator = QuantityComparator.SI();
    SerializableQ.require(comparator);
    Tensor sorted = Sort.ofVector(Tensors.fromString("{4[h], 300[s], 2[min], 180[s]}"), comparator);
    assertEquals(sorted, Tensors.fromString("{2[min], 180[s], 300[s], 4[h]}"));
  }

  @Test
  void testUnitless() {
    Comparator<Scalar> comparator = QuantityComparator.SI();
    Tensor sorted = Sort.ofVector(Tensors.fromString("{4[rad], 300[deg], 2, 180[rad], -1[rad]}"), comparator);
    assertEquals(sorted, Tensors.fromString("{-1[rad], 2, 4[rad], 300[deg], 180[rad]}"));
  }

  @Test
  void testUnknown() {
    Comparator<Scalar> comparator = QuantityComparator.SI();
    Tensor sorted = Sort.ofVector(Tensors.fromString("{4[fun], 300[fun], 2[fun], 180[fun]}"), comparator);
    assertEquals(sorted, Tensors.fromString("{2[fun], 4[fun], 180[fun], 300[fun]}"));
  }

  @Test
  void testEmpty() {
    UnitSystem unitSystem = SimpleUnitSystem.from(new Properties());
    SerializableQ.require(unitSystem);
    Comparator<Scalar> comparator = QuantityComparator.of(unitSystem);
    SerializableQ.require(comparator);
    Tensor sorted = Sort.ofVector(Tensors.fromString("{4[fun], 300[fun], 2[fun], 180[fun]}"), comparator);
    assertEquals(sorted, Tensors.fromString("{2[fun], 4[fun], 180[fun], 300[fun]}"));
  }

  @Test
  void testIncompatibleFail() {
    Comparator<Scalar> comparator = QuantityComparator.SI();
    Tensor vector = Tensors.fromString("{4[h], 300[s], 2[km], 180[s]}");
    assertThrows(Throw.class, () -> Sort.ofVector(vector, comparator));
  }

  @Test
  void testInequality() {
    QuantityComparator quantityComparator = QuantityComparator.SI();
    assertFalse(quantityComparator.lessThan(Quantity.of(5, "days"), Quantity.of(10, "h")));
    assertFalse(quantityComparator.lessEquals(Quantity.of(5, "days"), Quantity.of(10, "h")));
    assertTrue(quantityComparator.lessThan(Quantity.of(200, "min"), Quantity.of(10, "h")));
    assertTrue(quantityComparator.lessEquals(Quantity.of(200, "min"), Quantity.of(10, "h")));
  }

  @Test
  void testNullFail() {
    assertThrows(NullPointerException.class, () -> QuantityComparator.of(null));
  }
}
