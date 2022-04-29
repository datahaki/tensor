// code by jph
package ch.alpine.tensor.qty;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.Map.Entry;
import java.util.Set;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.RationalScalar;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Scalars;
import ch.alpine.tensor.chq.ExactScalarQ;
import ch.alpine.tensor.mat.Tolerance;

public class StaticHelperTest {
  @Test
  public void testSimple() {
    UnitParser.requireAtomic("m");
    assertThrows(IllegalArgumentException.class, () -> UnitParser.requireAtomic("m2"));
    assertThrows(IllegalArgumentException.class, () -> UnitParser.requireAtomic("m^2"));
  }

  // only used in tests
  /* package */ static Set<Unit> atoms(Unit unit) {
    return unit.map().entrySet().stream() //
        .map(StaticHelperTest::format) //
        .collect(Collectors.toSet());
  }

  // helper function
  private static Unit format(Entry<String, Scalar> entry) {
    return Unit.of(entry.getKey() + Unit.POWER_DELIMITER + entry.getValue());
  }

  @Test
  public void testAtoms() {
    Set<Unit> set = atoms(Unit.of("m^3*kg^-2*s^1"));
    set.contains(Unit.of("m^3"));
    set.contains(Unit.of("kg^-2"));
    set.contains(Unit.of("s"));
  }

  @Test
  public void testConversion0() {
    assertEquals(StaticHelper.conversion(UnitSystem.SI(), "kg", "kg"), RealScalar.ONE);
    assertEquals(StaticHelper.conversion(UnitSystem.SI(), "K", "K"), Quantity.of(1, ""));
  }

  @Test
  public void testConversion1() {
    Scalar scalar = StaticHelper.conversion(UnitSystem.SI(), "kg", "N");
    ExactScalarQ.require(scalar);
    assertEquals(scalar, Quantity.of(1, "N*m^-1*s^2"));
  }

  @Test
  public void testConversion2() {
    Scalar scalar = StaticHelper.conversion(UnitSystem.SI(), "s", "Hz");
    ExactScalarQ.require(scalar);
    assertEquals(scalar, Quantity.of(1, "Hz^-1"));
  }

  @Test
  public void testConversion3() {
    Scalar scalar = StaticHelper.conversion(UnitSystem.SI(), "s", "min");
    ExactScalarQ.require(scalar);
    assertEquals(scalar, Quantity.of(RationalScalar.of(1, 60), "min"));
  }

  @Test
  public void testConversion3b() {
    Scalar scalar = StaticHelper.conversion(UnitSystem.SI(), "m", "km");
    ExactScalarQ.require(scalar);
    assertEquals(scalar, Quantity.of(RationalScalar.of(1, 1000), "km"));
  }

  @Test
  public void testConversion4() {
    Scalar scalar = StaticHelper.conversion(UnitSystem.SI(), "s", "N");
    ExactScalarQ.require(scalar);
    assertEquals(scalar, Quantity.of(1, "N^-1/2*kg^1/2*m^1/2"));
    Scalar rev = UnitSystem.SI().apply(scalar);
    assertEquals(rev, Quantity.of(1, "s"));
  }

  @Test
  public void testM_W() {
    Scalar scalar = StaticHelper.conversion(UnitSystem.SI(), "m", "W"); // W = 1[m^2*kg*s^-3]
    ExactScalarQ.require(scalar);
    assertEquals(scalar, Scalars.fromString("1[W^1/2*kg^-1/2*s^3/2]"));
  }

  @Test
  public void testM_kW() {
    Scalar scalar = StaticHelper.conversion(UnitSystem.SI(), "m", "kW"); // W = 1[m^2*kg*s^-3]
    Tolerance.CHOP.requireClose(scalar, Scalars.fromString("0.03162277660168379[kW^1/2*kg^-1/2*s^3/2]"));
  }

  @Test
  public void testBase() {
    Set<String> set = StaticHelper.base(UnitSystem.SI().map().values());
    assertTrue(Arrays.asList("cd A B s mol K kg m".split(" ")).containsAll(set));
  }

  @Test
  public void testMultiplyNullFail() {
    assertThrows(NullPointerException.class, () -> StaticHelper.multiply(Quantity.of(1, "s"), null));
    assertThrows(NullPointerException.class, () -> StaticHelper.multiply(null, Unit.of("s")));
  }

  @Test
  public void testConversionFail0() {
    assertThrows(NullPointerException.class, () -> StaticHelper.conversion(UnitSystem.SI(), "rad", ""));
    assertThrows(NullPointerException.class, () -> StaticHelper.conversion(UnitSystem.SI(), "", "rad"));
  }

  @Test
  public void testConversionFail1() {
    assertThrows(NullPointerException.class, () -> StaticHelper.conversion(UnitSystem.SI(), "K", "N"));
    assertThrows(NullPointerException.class, () -> StaticHelper.conversion(UnitSystem.SI(), "kg*m", "N"));
    assertThrows(NullPointerException.class, () -> StaticHelper.conversion(UnitSystem.SI(), "kg", "N*kg"));
    assertThrows(NullPointerException.class, () -> StaticHelper.conversion(UnitSystem.SI(), "kg", "N*s^2"));
    assertThrows(NullPointerException.class, () -> StaticHelper.conversion(UnitSystem.SI(), "kg*s", "N"));
  }

  @Test
  public void testConversionFail2() {
    assertThrows(IllegalArgumentException.class, () -> StaticHelper.conversion(UnitSystem.SI(), "K", "CHF"));
    assertThrows(NullPointerException.class, () -> StaticHelper.conversion(UnitSystem.SI(), "CHF", "K"));
    assertThrows(IllegalArgumentException.class, () -> StaticHelper.conversion(UnitSystem.SI(), "m", "CHF"));
    assertThrows(NullPointerException.class, () -> StaticHelper.conversion(UnitSystem.SI(), "CHF", "m"));
  }

  @Test
  public void testConversionTrivial() {
    assertEquals(StaticHelper.conversion(UnitSystem.SI(), "kg*m", "kg*m"), RealScalar.ONE);
  }

  @Test
  public void testPackageVisibility() {
    assertFalse(Modifier.isPublic(StaticHelper.class.getModifiers()));
  }
}
