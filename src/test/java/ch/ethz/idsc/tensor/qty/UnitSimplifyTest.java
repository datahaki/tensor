// code by jph
package ch.ethz.idsc.tensor.qty;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import ch.ethz.idsc.tensor.api.ScalarUnaryOperator;
import ch.ethz.idsc.tensor.ext.Serialization;
import ch.ethz.idsc.tensor.io.ResourceData;
import ch.ethz.idsc.tensor.num.Pi;
import ch.ethz.idsc.tensor.usr.AssertFail;
import junit.framework.TestCase;

public class UnitSimplifyTest extends TestCase {
  public void testSimple() throws ClassNotFoundException, IOException {
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

  public void testProperties() {
    Set<String> set = ResourceData.properties("/unit/simplify1.properties").stringPropertyNames();
    UnitSimplify.from(UnitSystem.SI(), set);
    assertEquals(set.size(), 4);
  }

  public void testNonUnitFail() {
    Set<String> set = ResourceData.properties("/unit/simplify2.properties").stringPropertyNames();
    AssertFail.of(() -> UnitSimplify.from(UnitSystem.SI(), set));
  }

  public void testDuplicateFail() {
    Set<Unit> set = new HashSet<>();
    set.add(Unit.of("kW"));
    set.add(Unit.of("W"));
    AssertFail.of(() -> UnitSimplify.of(UnitSystem.SI(), set));
  }

  public void testNullFail() {
    AssertFail.of(() -> UnitSimplify.of(UnitSystem.SI(), null));
    AssertFail.of(() -> UnitSimplify.of(null, new HashSet<>()));
  }
}
