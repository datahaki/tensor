// code by jph
package ch.alpine.tensor.pdf;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.Rational;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.Throw;
import ch.alpine.tensor.pdf.c.ExponentialDistribution;
import ch.alpine.tensor.qty.Quantity;
import ch.alpine.tensor.qty.QuantityTensor;
import ch.alpine.tensor.qty.Unit;
import ch.alpine.tensor.red.Total;

class BinCountsTest {
  @Test
  void testWidthTwo() {
    Tensor hist = BinCounts.of(Tensors.vector(6, 7, 1, 2, 3, 4, 2), RealScalar.of(2));
    assertEquals(hist, Tensors.fromString("{1, 3, 1, 2}"));
  }

  @Test
  void testWidthHalf() {
    Tensor values = Tensors.vector(6, 7, 1, 2, 3, 4, 2);
    Tensor hist = BinCounts.of(values, Rational.of(1, 2));
    assertEquals(hist, Tensors.fromString("{0, 0, 1, 0, 2, 0, 1, 0, 1, 0, 0, 0, 1, 0, 1}"));
    assertEquals(Total.ofVector(hist).number().intValue(), values.length());
  }

  @Test
  void testEmpty() {
    assertEquals(BinCounts.of(Tensors.vector(), RealScalar.ONE), Tensors.empty());
  }

  @Test
  void testDefault() {
    Distribution distribution = ExponentialDistribution.of(RealScalar.ONE);
    Tensor vector = RandomVariate.of(distribution, 10);
    assertEquals(BinCounts.of(vector), BinCounts.of(vector, RealScalar.ONE));
  }

  @Test
  void testQuantity() {
    Tensor vector = QuantityTensor.of(Tensors.vector(1, 2, 3, 4, 1, 2, 1, 0, 3, 2, 1, 2), Unit.of("s"));
    Tensor result = BinCounts.of(vector, Quantity.of(1, "s"));
    assertEquals(result, Tensors.vector(1, 4, 4, 2, 1));
  }

  @Test
  void testNegativeFail() {
    assertThrows(Throw.class, () -> BinCounts.of(Tensors.vector(-1e-10), RealScalar.ONE));
    assertThrows(Throw.class, () -> BinCounts.of(Tensors.vector(-1e-10, -10), RealScalar.ONE));
    assertThrows(Throw.class, () -> BinCounts.of(Tensors.vector(1, 2, 3, 4, 0, -3, 12, 32), RealScalar.ONE));
  }

  @Test
  void testDomainFail() {
    assertThrows(Throw.class, () -> BinCounts.of(Tensors.vector(-1e-10), RealScalar.of(1.0)));
  }

  @Test
  void testWidthFail() {
    assertThrows(Throw.class, () -> BinCounts.of(Tensors.vector(1, 2), RealScalar.of(0.0))); // zero
    assertThrows(Throw.class, () -> BinCounts.of(Tensors.vector(1, 2), RealScalar.of(-0.2))); // negative
  }
}
