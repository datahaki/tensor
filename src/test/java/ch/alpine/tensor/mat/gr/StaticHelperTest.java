// code by jph
package ch.alpine.tensor.mat.gr;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.lang.reflect.Modifier;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Throw;
import ch.alpine.tensor.mat.Tolerance;
import ch.alpine.tensor.sca.Clips;
import ch.alpine.tensor.sca.InvertUnlessZero;

class StaticHelperTest {
  private static final Scalar _0 = RealScalar.of(0.0);
  private static final Scalar _1 = RealScalar.of(1.0);

  /** @param scalar
   * @return numerical 0 or 1
   * @see InvertUnlessZero */
  private static Scalar unitize_chop(Scalar scalar) {
    return Tolerance.CHOP.isZero(scalar) ? _0 : _1;
  }

  @Test
  void testRequireUnit() {
    Scalar scalar = RealScalar.of(1.0 + 1e-13);
    Scalar mapped = InfluenceMatrixSvd.requireUnit(scalar);
    Clips.unit().requireInside(mapped);
  }

  @Test
  void testRequireUnitFail() {
    Scalar scalar = RealScalar.of(1.0 + 1e-5);
    assertThrows(Throw.class, () -> InfluenceMatrixSvd.requireUnit(scalar));
  }

  @Test
  void testUnitizeChop() {
    assertEquals(StaticHelperTest.unitize_chop(RealScalar.of(1e-13)), RealScalar.ZERO);
    assertEquals(StaticHelperTest.unitize_chop(RealScalar.of(1e-11)), RealScalar.ONE);
    assertEquals(StaticHelperTest.unitize_chop(RealScalar.of(123)), RealScalar.ONE);
  }

  @Test
  void testPackageVisibility() {
    assertFalse(Modifier.isPublic(StaticHelper.class.getModifiers()));
  }
}
