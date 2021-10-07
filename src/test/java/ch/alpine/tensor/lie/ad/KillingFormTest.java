// code by jph
package ch.alpine.tensor.lie.ad;

import ch.alpine.tensor.Scalars;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.alg.Array;
import ch.alpine.tensor.alg.UnitVector;
import ch.alpine.tensor.mat.DiagonalMatrix;
import ch.alpine.tensor.mat.re.Det;
import ch.alpine.tensor.usr.AssertFail;
import junit.framework.TestCase;

public class KillingFormTest extends TestCase {
  public void testSe2() {
    Tensor ad = LieAlgebras.se2().unmodifiable();
    JacobiIdentity.require(ad);
    assertEquals(JacobiIdentity.of(ad), Array.zeros(3, 3, 3, 3));
    assertEquals(ad.dot(UnitVector.of(3, 0)).dot(UnitVector.of(3, 1)), Array.zeros(3));
    assertEquals(ad.dot(UnitVector.of(3, 0)).dot(UnitVector.of(3, 2)), UnitVector.of(3, 1).negate());
    assertEquals(ad.dot(UnitVector.of(3, 1)).dot(UnitVector.of(3, 2)), UnitVector.of(3, 0));
    assertEquals(KillingForm.of(ad), DiagonalMatrix.of(0, 0, -2));
  }

  public void testSo3() {
    Tensor ad = LieAlgebras.so3();
    assertEquals(JacobiIdentity.of(ad), Array.zeros(3, 3, 3, 3));
    Tensor kil = KillingForm.of(ad);
    assertEquals(kil, DiagonalMatrix.of(-2, -2, -2));
  }

  public void testSl2() {
    Tensor ad = LieAlgebras.sl2();
    assertEquals(JacobiIdentity.of(ad), Array.zeros(3, 3, 3, 3));
    Tensor kil = KillingForm.of(ad);
    // killing form is non-degenerate
    assertTrue(Scalars.nonZero(Det.of(kil)));
  }

  public void testHe3() {
    Tensor ad = LieAlgebras.he1();
    assertEquals(JacobiIdentity.of(ad), Array.zeros(3, 3, 3, 3));
    Tensor kil = KillingForm.of(ad);
    assertTrue(Scalars.isZero(Det.of(kil)));
  }

  public void testRank4Fail() {
    AssertFail.of(() -> KillingForm.of(Array.zeros(3, 3, 3, 3)));
  }
}
