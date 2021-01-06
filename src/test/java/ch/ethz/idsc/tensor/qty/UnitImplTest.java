// code by jph
package ch.ethz.idsc.tensor.qty;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.usr.AssertFail;
import junit.framework.TestCase;

public class UnitImplTest extends TestCase {
  public void testMap() {
    Unit unit = Unit.of("kg^2*m^-1");
    assertEquals(unit.map().get("kg"), RealScalar.of(2));
    assertEquals(unit.map().get("m"), RealScalar.of(-1));
    assertFalse(unit.map().containsKey("A"));
  }

  public void testMap2() {
    Unit unit = Unit.of("kg^2*m^-1*s");
    assertEquals(unit.map().get("kg"), RealScalar.of(2));
    assertEquals(unit.map().get("m"), RealScalar.of(-1));
    assertEquals(unit.map().get("s"), RealScalar.ONE);
    assertFalse(unit.map().containsKey("A"));
  }

  public void testMultiplyFail() {
    Unit unit = Unit.of("kg^2*m^-1");
    AssertFail.of(() -> unit.multiply(Quantity.of(3, "s")));
  }

  public void testUnmodifiableMap() {
    Unit unit = Unit.of("kg^2*m^-1");
    AssertFail.of(() -> unit.map().clear());
  }

  public void testMergeCollision() {
    Map<String, Scalar> m1 = new HashMap<>();
    m1.put("m", RealScalar.ONE);
    Map<String, Scalar> m2 = new HashMap<>();
    m2.put("m", RealScalar.of(2));
    Stream.concat(m1.entrySet().stream(), m2.entrySet().stream()).collect(UnitImpl.NEGATION);
  }
}
