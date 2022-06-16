// code by jph
package ch.alpine.tensor.itp;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.RationalScalar;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.alg.Reverse;
import ch.alpine.tensor.lie.Quaternion;
import ch.alpine.tensor.red.Total;

class BernsteinBasisTest {
  @Test
  void testSimple() {
    Tensor actual = BernsteinBasis.of(5, RationalScalar.of(2, 3));
    Tensor expect = Tensors.fromString("{1/243, 10/243, 40/243, 80/243, 80/243, 32/243}");
    assertEquals(actual, expect);
    assertEquals(Total.of(actual), RealScalar.ONE);
  }

  @Test
  void testSimpleReverse() {
    Tensor actual = BernsteinBasis.of(5, RationalScalar.of(1, 3));
    Tensor expect = Reverse.of(Tensors.fromString("{1/243, 10/243, 40/243, 80/243, 80/243, 32/243}"));
    assertEquals(actual, expect);
    assertEquals(Total.of(actual), RealScalar.ONE);
  }

  @Test
  void testQuaternion() {
    Quaternion quaternion = Quaternion.of(2, 3, 4, 5);
    assertThrows(ClassCastException.class, () -> BernsteinBasis.of(5, quaternion));
  }

  @Test
  void testNegFail() {
    assertEquals(BernsteinBasis.of(0, RationalScalar.of(2, 3)), Tensors.vector(1));
    assertThrows(IllegalArgumentException.class, () -> BernsteinBasis.of(-1, RationalScalar.of(2, 3)));
  }
}
