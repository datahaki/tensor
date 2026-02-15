// code by jph
package ch.alpine.tensor.lie;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.alg.ConstantArray;
import ch.alpine.tensor.alg.Dot;
import ch.alpine.tensor.mat.IdentityMatrix;

class JacobiIdentityTest {
  @Test
  void testHeisenberg() {
    Tensor ad = ExAd.HE1.ad(); // new HeAlgebra(1).ad();
    Tensor eye = IdentityMatrix.of(3);
    assertEquals(Dot.of(ad, eye.get(0), eye.get(2)), eye.get(1));
    assertEquals(Dot.of(ad, eye.get(2), eye.get(0)), eye.get(1).negate());
  }

  @Test
  void testSo3() {
    Tensor so3 = ExAd.SO3.ad();
    Tensor eye = IdentityMatrix.of(3);
    assertEquals(Dot.of(so3, eye.get(0), eye.get(1)), eye.get(2));
    assertEquals(Dot.of(so3, eye.get(1), eye.get(0)), eye.get(2).negate());
  }

  @Test
  void testSl2() {
    Tensor ad = ExAd.SL2.ad().copy(); // Sl2Algebra.INSTANCE.ad();
    ad.set(Scalar::zero, Tensor.ALL, 1, 2);
    assertThrows(Exception.class, () -> JacobiIdentity.INSTANCE.require(ad));
  }

  @ParameterizedTest
  @EnumSource
  void testSe2(ExAd exAd) {
    Tensor ad = exAd.ad(); // Se2Algebra.INSTANCE.ad();
    int n = ad.length();
    assertEquals(JacobiIdentity.INSTANCE.defect(ad), ConstantArray.of(RealScalar.ZERO, n, n, n, n));
  }
}
