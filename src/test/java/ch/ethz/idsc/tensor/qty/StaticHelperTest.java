// code by jph
package ch.ethz.idsc.tensor.qty;

import java.util.Map.Entry;
import java.util.Set;
import java.util.stream.Collectors;

import ch.ethz.idsc.tensor.ExactScalarQ;
import ch.ethz.idsc.tensor.RationalScalar;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Scalars;
import ch.ethz.idsc.tensor.mat.Tolerance;
import ch.ethz.idsc.tensor.usr.AssertFail;
import junit.framework.TestCase;

public class StaticHelperTest extends TestCase {
  public void testSimple() {
    UnitParser.requireAtomic("m");
    AssertFail.of(() -> UnitParser.requireAtomic("m2"));
    AssertFail.of(() -> UnitParser.requireAtomic("m^2"));
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

  public void testAtoms() {
    Set<Unit> set = atoms(Unit.of("m^3*kg^-2*s^1"));
    set.contains(Unit.of("m^3"));
    set.contains(Unit.of("kg^-2"));
    set.contains(Unit.of("s"));
  }

  public void testConversion0() {
    assertEquals(StaticHelper.conversion(UnitSystem.SI(), "kg", "kg"), RealScalar.ONE);
    assertEquals(StaticHelper.conversion(UnitSystem.SI(), "K", "K"), Quantity.of(1, ""));
  }

  public void testConversion1() {
    Scalar scalar = StaticHelper.conversion(UnitSystem.SI(), "kg", "N");
    ExactScalarQ.require(scalar);
    assertEquals(scalar, Quantity.of(1, "N*m^-1*s^2"));
  }

  public void testConversion2() {
    Scalar scalar = StaticHelper.conversion(UnitSystem.SI(), "s", "Hz");
    ExactScalarQ.require(scalar);
    assertEquals(scalar, Quantity.of(1, "Hz^-1"));
  }

  public void testConversion3() {
    Scalar scalar = StaticHelper.conversion(UnitSystem.SI(), "s", "min");
    ExactScalarQ.require(scalar);
    assertEquals(scalar, Quantity.of(RationalScalar.of(1, 60), "min"));
  }

  public void testConversion3b() {
    Scalar scalar = StaticHelper.conversion(UnitSystem.SI(), "m", "km");
    ExactScalarQ.require(scalar);
    assertEquals(scalar, Quantity.of(RationalScalar.of(1, 1000), "km"));
  }

  public void testConversion4() {
    Scalar scalar = StaticHelper.conversion(UnitSystem.SI(), "s", "N");
    ExactScalarQ.require(scalar);
    assertEquals(scalar, Quantity.of(1, "N^-1/2*kg^1/2*m^1/2"));
    Scalar rev = UnitSystem.SI().apply(scalar);
    assertEquals(rev, Quantity.of(1, "s"));
  }

  public void testM_W() {
    Scalar scalar = StaticHelper.conversion(UnitSystem.SI(), "m", "W"); // W = 1[m^2*kg*s^-3]
    ExactScalarQ.require(scalar);
    assertEquals(scalar, Scalars.fromString("1[W^1/2*kg^-1/2*s^3/2]"));
  }

  public void testM_kW() {
    Scalar scalar = StaticHelper.conversion(UnitSystem.SI(), "m", "kW"); // W = 1[m^2*kg*s^-3]
    Tolerance.CHOP.requireClose(scalar, Scalars.fromString("0.03162277660168379[kW^1/2*kg^-1/2*s^3/2]"));
  }

  public void testMultiplyNullFail() {
    AssertFail.of(() -> StaticHelper.multiply(Quantity.of(1, "s"), null));
    AssertFail.of(() -> StaticHelper.multiply(null, Unit.of("s")));
  }

  public void testConversionFail0() {
    AssertFail.of(() -> StaticHelper.conversion(UnitSystem.SI(), "rad", ""));
    AssertFail.of(() -> StaticHelper.conversion(UnitSystem.SI(), "", "rad"));
  }

  public void testConversionFail1() {
    AssertFail.of(() -> StaticHelper.conversion(UnitSystem.SI(), "K", "N"));
    AssertFail.of(() -> StaticHelper.conversion(UnitSystem.SI(), "kg*m", "N"));
    AssertFail.of(() -> StaticHelper.conversion(UnitSystem.SI(), "kg", "N*kg"));
    AssertFail.of(() -> StaticHelper.conversion(UnitSystem.SI(), "kg", "N*s^2"));
    AssertFail.of(() -> StaticHelper.conversion(UnitSystem.SI(), "kg*s", "N"));
  }

  public void testConversionFail2() {
    AssertFail.of(() -> StaticHelper.conversion(UnitSystem.SI(), "K", "CHF"));
    AssertFail.of(() -> StaticHelper.conversion(UnitSystem.SI(), "CHF", "K"));
    AssertFail.of(() -> StaticHelper.conversion(UnitSystem.SI(), "m", "CHF"));
    AssertFail.of(() -> StaticHelper.conversion(UnitSystem.SI(), "CHF", "m"));
  }

  public void testConversionTrivial() {
    assertEquals(StaticHelper.conversion(UnitSystem.SI(), "kg*m", "kg*m"), RealScalar.ONE);
  }
}
