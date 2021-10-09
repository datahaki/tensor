// code by jph
package ch.alpine.tensor.mat.gr;

import java.lang.reflect.Modifier;

import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.mat.Tolerance;
import ch.alpine.tensor.sca.Clips;
import ch.alpine.tensor.sca.InvertUnlessZero;
import ch.alpine.tensor.usr.AssertFail;
import junit.framework.TestCase;

public class StaticHelperTest extends TestCase {
  private static final Scalar _0 = RealScalar.of(0.0);
  private static final Scalar _1 = RealScalar.of(1.0);

  /** @param scalar
   * @return numerical 0 or 1
   * @see InvertUnlessZero */
  private static Scalar unitize_chop(Scalar scalar) {
    return Tolerance.CHOP.isZero(scalar) ? _0 : _1;
  }

  public void testRequireUnit() {
    Scalar scalar = RealScalar.of(1.0 + 1e-13);
    Scalar mapped = StaticHelper.requireUnit(scalar);
    Clips.unit().requireInside(mapped);
  }

  public void testRequireUnitFail() {
    Scalar scalar = RealScalar.of(1.0 + 1e-5);
    AssertFail.of(() -> StaticHelper.requireUnit(scalar));
  }

  public void testUnitizeChop() {
    assertEquals(StaticHelperTest.unitize_chop(RealScalar.of(1e-13)), RealScalar.ZERO);
    assertEquals(StaticHelperTest.unitize_chop(RealScalar.of(1e-11)), RealScalar.ONE);
    assertEquals(StaticHelperTest.unitize_chop(RealScalar.of(123)), RealScalar.ONE);
  }

  public void testPackageVisibility() {
    assertFalse(Modifier.isPublic(StaticHelper.class.getModifiers()));
  }
}
