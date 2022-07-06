// code by jph
package ch.alpine.tensor.lie.r2;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.Throw;
import ch.alpine.tensor.qty.Quantity;
import ch.alpine.tensor.sca.Chop;

class SignedCurvature2DTest {
  @Test
  void testCounterClockwise() {
    Tensor a = Tensors.vector(1, 0);
    Tensor b = Tensors.vector(0, 1);
    Tensor c = Tensors.vector(-1, 0);
    Chop._12.requireClose(SignedCurvature2D.of(a, b, c).get(), RealScalar.ONE);
    Chop._12.requireClose(SignedCurvature2D.of(c, b, a).get(), RealScalar.ONE.negate());
  }

  @Test
  void testStraight() {
    Tensor a = Tensors.vector(1, 1);
    Tensor b = Tensors.vector(2, 2);
    Tensor c = Tensors.vector(5, 5);
    assertEquals(SignedCurvature2D.of(a, b, c).get(), RealScalar.ZERO);
  }

  @Test
  void testSingular1() {
    Tensor a = Tensors.vector(1, 1);
    Tensor b = Tensors.vector(1, 1);
    Tensor c = Tensors.vector(2, 2);
    assertFalse(SignedCurvature2D.of(a, b, c).isPresent());
  }

  @Test
  void testSingular2() {
    Tensor a = Tensors.vector(1, 1);
    Tensor b = Tensors.vector(2, 2);
    Tensor c = Tensors.vector(1, 1);
    assertFalse(SignedCurvature2D.of(a, b, c).isPresent());
  }

  @Test
  void testSingular3() {
    Tensor a = Tensors.vector(1, 1);
    Tensor b = Tensors.vector(1, 1);
    Tensor c = Tensors.vector(1, 1);
    assertFalse(SignedCurvature2D.of(a, b, c).isPresent());
  }

  @Test
  void testQuantity() {
    Tensor a = Tensors.fromString("{1[m], 0[m]}");
    Tensor b = Tensors.fromString("{0[m], 1[m]}");
    Tensor c = Tensors.fromString("{-1[m], 0[m]}");
    Chop._10.requireClose(SignedCurvature2D.of(a, b, c).get(), Quantity.of(+1, "m^-1"));
    Chop._10.requireClose(SignedCurvature2D.of(c, b, a).get(), Quantity.of(-1, "m^-1"));
  }

  @Test
  void testFail() {
    Tensor a = Tensors.vector(1, 1, 0);
    Tensor b = Tensors.vector(1, 2, 1);
    Tensor c = Tensors.vector(1, 3, 2);
    assertThrows(Throw.class, () -> SignedCurvature2D.of(a, b, c));
  }
}
