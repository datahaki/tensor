// code by jph
package ch.ethz.idsc.tensor.mat;

import java.lang.reflect.Modifier;

import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.sca.Clips;
import ch.ethz.idsc.tensor.usr.AssertFail;
import junit.framework.TestCase;

public class StaticHelperTest extends TestCase {
  public void testRequireUnit() {
    Scalar scalar = RealScalar.of(1.0 + 1e-13);
    Scalar mapped = StaticHelper.requireUnit(scalar);
    Clips.unit().requireInside(mapped);
  }

  public void testRequireUnitFail() {
    Scalar scalar = RealScalar.of(1.0 + 1e-11);
    AssertFail.of(() -> StaticHelper.requireUnit(scalar));
  }

  public void testUnitizeChop() {
    assertEquals(StaticHelper.unitize_chop(RealScalar.of(1e-13)), RealScalar.ZERO);
    assertEquals(StaticHelper.unitize_chop(RealScalar.of(1e-11)), RealScalar.ONE);
  }

  public void testPackageVisibility() {
    assertFalse(Modifier.isPublic(StaticHelper.class.getModifiers()));
  }
}
