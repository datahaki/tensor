// code by jph
package ch.alpine.tensor.qty;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.api.ScalarUnaryOperator;
import ch.alpine.tensor.ext.ResourceData;
import ch.alpine.tensor.ext.Serialization;
import ch.alpine.tensor.num.Pi;

class UnitSimplifyTest {
  @Test
  void testSimple() throws ClassNotFoundException, IOException {
    Set<Unit> set = new HashSet<>();
    set.add(Unit.of("kW"));
    set.add(Unit.of("kW*h^-1"));
    set.add(Unit.of("N"));
    set.add(Unit.of("J")); // J
    ScalarUnaryOperator scalarUnaryOperator = Serialization.copy(UnitSimplify.of(UnitSystem.SI(), set));
    assertEquals(scalarUnaryOperator.apply(Quantity.of(3000, "W")), Quantity.of(3, "kW"));
    assertEquals(scalarUnaryOperator.apply(Quantity.of(5, "N*m")), Quantity.of(5, "J"));
    assertEquals(scalarUnaryOperator.apply(Quantity.of(10, "s")), Quantity.of(10, "s"));
    assertEquals(scalarUnaryOperator.apply(Pi.VALUE), Pi.VALUE);
    assertEquals(scalarUnaryOperator.apply(Quantity.of(20, "unknown")), Quantity.of(20, "unknown"));
  }

  @Test
  void testPartsPer() {
    assertEquals(QuantityMagnitude.SI().in("ppt").apply(Quantity.of(1, "ppb")), RealScalar.of(1000));
    assertEquals(QuantityMagnitude.SI().in("ppb").apply(Quantity.of(1, "ppm")), RealScalar.of(1000));
    assertEquals(QuantityMagnitude.SI().in("ppm").apply(Quantity.of(1, "%")), RealScalar.of(10000));
    assertEquals(QuantityMagnitude.SI().in("").apply(Quantity.of(100, "%")), RealScalar.of(1));
  }

  @Test
  void testProperties() {
    Set<String> set = ResourceData.properties("/ch/alpine/tensor/qty/simplify1.properties").stringPropertyNames();
    UnitSimplify.from(UnitSystem.SI(), set);
    assertEquals(set.size(), 4);
  }

  @Test
  void testNonUnitFail() {
    Set<String> set = ResourceData.properties("/ch/alpine/tensor/qty/simplify2.properties").stringPropertyNames();
    assertThrows(IllegalArgumentException.class, () -> UnitSimplify.from(UnitSystem.SI(), set));
  }

  @Test
  void testDuplicateFail() {
    Set<Unit> set = new HashSet<>();
    set.add(Unit.of("kW"));
    set.add(Unit.of("W"));
    assertThrows(IllegalArgumentException.class, () -> UnitSimplify.of(UnitSystem.SI(), set));
  }

  @Test
  void testNullFail() {
    assertThrows(NullPointerException.class, () -> UnitSimplify.of(UnitSystem.SI(), null));
    assertThrows(NullPointerException.class, () -> UnitSimplify.of(null, new HashSet<>()));
  }
}
