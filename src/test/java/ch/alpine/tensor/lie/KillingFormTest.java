// code by jph
package ch.alpine.tensor.lie;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.alg.Array;
import ch.alpine.tensor.alg.UnitVector;
import ch.alpine.tensor.mat.DiagonalMatrix;
import ch.alpine.tensor.mat.re.Det;

class KillingFormTest {
  @Test
  void testSe2() {
    Tensor ad = ExAd.SE2.ad();
    JacobiIdentity.INSTANCE.require(ad);
    assertEquals(ad.dot(UnitVector.of(3, 0)).dot(UnitVector.of(3, 1)), Array.zeros(3));
    assertEquals(ad.dot(UnitVector.of(3, 0)).dot(UnitVector.of(3, 2)), UnitVector.of(3, 1).negate());
    assertEquals(ad.dot(UnitVector.of(3, 1)).dot(UnitVector.of(3, 2)), UnitVector.of(3, 0));
    assertEquals(KillingForm.of(ad), DiagonalMatrix.of(0, 0, -2));
  }

  @Test
  void testSo3() {
    Tensor ad = ExAd.SO3.ad();
    Tensor kil = KillingForm.of(ad);
    assertEquals(kil, DiagonalMatrix.of(-2, -2, -2));
  }

  @Test
  void testSl2() {
    Tensor ad = ExAd.SL2.ad();
    Tensor kil = KillingForm.of(ad);
    assertEquals(kil, DiagonalMatrix.of(-2, 2, 2));
    // killing form is non-degenerate
    assertEquals(Det.of(kil), RealScalar.of(-8));
  }

  @Test
  void testHe3() {
    Tensor ad = ExAd.HE1.ad();
    Tensor kil = KillingForm.of(ad);
    assertEquals(Det.of(kil), RealScalar.ZERO);
  }

  @Test
  void testRank4Fail() {
    assertThrows(Exception.class, () -> KillingForm.of(Array.zeros(3, 3, 3, 3)));
  }
}
