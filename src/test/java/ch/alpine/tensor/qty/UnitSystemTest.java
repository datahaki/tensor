// code by jph
package ch.alpine.tensor.qty;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.util.Properties;
import java.util.Set;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.ExactScalarQ;
import ch.alpine.tensor.RationalScalar;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Scalars;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.TensorRuntimeException;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.ext.Integers;
import ch.alpine.tensor.ext.Serialization;
import ch.alpine.tensor.io.ResourceData;
import ch.alpine.tensor.red.Total;

public class UnitSystemTest {
  @Test
  public void testSize() {
    Integers.requireLessEquals(463, UnitSystem.SI().map().size());
  }

  @Test
  public void testExact() {
    Scalar scalar = UnitSystem.SI().apply(Quantity.of(3, "Hz^-2*N*m^-1"));
    assertEquals(scalar, Quantity.of(3, "kg"));
    ExactScalarQ.require(scalar);
  }

  @Test
  public void testScalar() {
    Scalar scalar = RealScalar.ONE;
    Scalar result = UnitSystem.SI().apply(scalar);
    assertEquals(result, scalar);
    ExactScalarQ.require(result);
  }

  @Test
  public void testVoltage() {
    Scalar normal = UnitSystem.SI().apply(Quantity.of(1, "V"));
    assertEquals(normal, Quantity.of(1, "A^-1*kg*m^2*s^-3"));
    ExactScalarQ.require(normal);
  }

  @Test
  public void testMiles() {
    Scalar normal = UnitSystem.SI().apply(Quantity.of(125, "mi"));
    assertEquals(normal, Quantity.of(201168, "m"));
  }

  @Test
  public void testNullFail() {
    assertThrows(NullPointerException.class, () -> UnitSystem.SI().apply(null));
  }

  @Test
  public void testMore() {
    Tensor tensor = Tensors.of( //
        Quantity.of(3, "Hz^-2*N*m^-1"), //
        Quantity.of(3.6, "km*h^-1"), //
        Quantity.of(2, "cm^2"));
    Tensor result = tensor.map(UnitSystem.SI());
    assertEquals(result, Tensors.of( //
        Quantity.of(3, "kg"), //
        Quantity.of(1, "m*s^-1"), //
        Scalars.fromString("1/5000[m^2]")));
  }

  @Test
  public void testElectric() {
    UnitSystem unitSystem = UnitSystem.SI();
    Scalar r1 = unitSystem.apply(Quantity.of(3, "Ohm"));
    Scalar r2 = unitSystem.apply(Quantity.of(3, "V*A^-1"));
    assertEquals(r1, r2);
  }

  @Test
  public void testCustom() {
    Properties properties = new Properties();
    properties.setProperty("EUR", "1.25[CHF]");
    properties.setProperty("Apples", "2[CHF]");
    properties.setProperty("Chocolates", "3[CHF]");
    properties.setProperty("Oranges", "1[CHF]");
    UnitSystem prices = SimpleUnitSystem.from(properties);
    assertEquals(prices.apply(Quantity.of(3, "Apples")), Quantity.of(6, "CHF"));
    Tensor cart = Tensors.of(Quantity.of(2, "Apples"), Quantity.of(3, "Chocolates"), Quantity.of(3, "Oranges"));
    assertThrows(TensorRuntimeException.class, () -> Total.of(cart));
    Scalar total = Total.ofVector(cart.map(prices));
    assertEquals(total, Quantity.of(16, "CHF"));
    Scalar euro = UnitConvert.of(prices).to(Unit.of("EUR")).apply(total);
    assertEquals(euro, Quantity.of(12.8, "EUR"));
  }

  @Test
  public void testKnots() throws ClassNotFoundException, IOException {
    UnitSystem unitSystem = Serialization.copy(UnitSystem.SI());
    Scalar r1 = unitSystem.apply(Quantity.of(1, "kn"));
    Unit unit = QuantityUnit.of(r1);
    assertEquals(unit, Unit.of("m*s^-1"));
    ExactScalarQ.require(r1);
    Scalar r2 = UnitConvert.SI().to(Unit.of("km*h^-1")).apply(r1);
    ExactScalarQ.require(r2);
    Scalar r3 = Quantity.of(RationalScalar.of(463, 250), "km*h^-1");
    ExactScalarQ.require(r3);
    assertEquals(r2, r3);
  }

  @Test
  public void testSmallSi() throws ClassNotFoundException, IOException {
    Properties properties = ResourceData.properties("/unit/small.properties");
    assertFalse(properties.entrySet().isEmpty());
    UnitSystem unitSystem = Serialization.copy(SimpleUnitSystem.from(properties));
    Set<String> set = UnitSystems.base(unitSystem);
    assertEquals(set.size(), 7);
    assertTrue(set.contains("K"));
    assertTrue(set.contains("cd"));
    assertTrue(set.contains("A"));
    assertTrue(set.contains("m"));
    assertTrue(set.contains("s"));
    assertTrue(set.contains("kg"));
    assertTrue(set.contains("mol"));
    // unitSystem.apply(Quantity.of(1, "K"));
    KnownUnitQ knownUnitQ = KnownUnitQ.in(unitSystem);
    // Set<String> set2 = StaticHelper.buildSet(unitSystem);
    assertTrue(knownUnitQ.test(Unit.of("K")));
    assertTrue(knownUnitQ.test(Unit.of("N")));
    assertTrue(knownUnitQ.test(Unit.of("kg")));
    assertTrue(knownUnitQ.test(Unit.of("kg*N")));
  }

  @Test
  public void testFail1() {
    Properties properties = ResourceData.properties("/unit/fail1.properties");
    assertFalse(properties.entrySet().isEmpty());
    assertThrows(IllegalArgumentException.class, () -> SimpleUnitSystem.from(properties));
  }

  @Test
  public void testFail2() {
    Properties properties = ResourceData.properties("/unit/fail2.properties");
    assertFalse(properties.entrySet().isEmpty());
    assertThrows(IllegalArgumentException.class, () -> SimpleUnitSystem.from(properties));
  }

  @Test
  public void testFail3() {
    Properties properties = ResourceData.properties("/unit/fail3.properties");
    assertFalse(properties.entrySet().isEmpty());
    assertThrows(IllegalArgumentException.class, () -> SimpleUnitSystem.from(properties));
  }

  @Test
  public void testFail4() {
    Properties properties = ResourceData.properties("/unit/fail4.properties");
    assertFalse(properties.entrySet().isEmpty());
    assertThrows(IllegalArgumentException.class, () -> SimpleUnitSystem.from(properties));
  }

  @Test
  public void testFail5() {
    Properties properties = ResourceData.properties("/unit/fail5.properties");
    assertFalse(properties.entrySet().isEmpty());
    assertThrows(IllegalArgumentException.class, () -> SimpleUnitSystem.from(properties));
  }
}
