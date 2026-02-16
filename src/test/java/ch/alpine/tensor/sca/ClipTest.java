// code by jph
package ch.alpine.tensor.sca;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.DoubleScalar;
import ch.alpine.tensor.Rational;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.Throw;
import ch.alpine.tensor.num.Pi;
import ch.alpine.tensor.pdf.RandomVariate;
import ch.alpine.tensor.pdf.c.NormalDistribution;
import ch.alpine.tensor.qty.DateTime;
import ch.alpine.tensor.qty.Quantity;

class ClipTest {
  @Test
  void testApply() {
    Scalar min = RealScalar.of(-3);
    Scalar max = RealScalar.of(10);
    Clip clip = Clips.interval(min, max);
    assertEquals(clip.apply(RealScalar.of(-10)), min);
    assertEquals(clip.apply(RealScalar.of(-4)), min);
    assertEquals(clip.apply(RealScalar.of(0)), RealScalar.ZERO);
    assertEquals(clip.apply(RealScalar.of(13)), max);
  }

  @Test
  void testVector() {
    Scalar min = RealScalar.of(-3);
    Scalar max = RealScalar.of(10);
    Clip clip = Clips.interval(min, max);
    Tensor vector = Tensors.vector(-30, 30, 5);
    assertEquals(vector.maps(clip), Tensors.vector(-3, 10, 5));
  }

  @Test
  void testUnit() {
    assertEquals(Clips.unit().apply(RealScalar.of(-0.1)), RealScalar.ZERO);
    assertEquals(Clips.unit().apply(RealScalar.of(0.1)), RealScalar.of(0.1));
    assertEquals(Clips.unit().apply(RealScalar.of(1.1)), RealScalar.ONE);
  }

  @Test
  void testRescaleDateTime() {
    Clip clip = Clips.interval(DateTime.of(2020, 1, 2, 3, 4), DateTime.of(2023, 6, 2, 13, 24));
    Scalar rescale = clip.rescale(DateTime.of(2021, 7, 8, 2, 7));
    assertEquals(rescale, Rational.of(796263, 1796300));
  }

  @Test
  void testFail() {
    Clips.interval(5, 5);
    assertThrows(Throw.class, () -> Clips.interval(2, -3));
  }

  @Test
  void testQuantity() {
    Scalar min = Quantity.of(-3, "m");
    Scalar max = Quantity.of(2, "m");
    Clip clip = Clips.interval(min, max);
    assertEquals(clip.apply(Quantity.of(-5, "m")), min);
    assertEquals(clip.apply(Quantity.of(5, "m")), max);
    Scalar value = Quantity.of(-1, "m");
    assertEquals(clip.apply(value), value);
  }

  @Test
  void testQuantityInside() {
    Scalar min = Quantity.of(-3, "m");
    Scalar max = Quantity.of(2, "m");
    Clip clip = Clips.interval(min, max);
    assertTrue(clip.isInside(Quantity.of(1, "m")));
    assertTrue(clip.isInside(Quantity.of(2, "m")));
    assertFalse(clip.isInside(Quantity.of(3, "m")));
    assertThrows(Throw.class, () -> clip.isInside(Quantity.of(0, "V")));
    assertThrows(Throw.class, () -> clip.isInside(Quantity.of(3, "V")));
  }

  @Test
  void testRescaleQuantity() {
    Scalar min = Quantity.of(-3, "m");
    Scalar max = Quantity.of(2, "m");
    Clip clip = Clips.interval(min, max);
    assertEquals(clip.rescale(Quantity.of(-3, "m")), RealScalar.ZERO);
    assertEquals(clip.rescale(Quantity.of(-1, "m")), Rational.of(2, 5));
    assertEquals(clip.rescale(Quantity.of(2, "m")), RealScalar.ONE);
    assertEquals(clip.rescale(Quantity.of(10, "m")), RealScalar.ONE);
    assertEquals(clip.min(), min);
    assertEquals(clip.max(), max);
    assertEquals(clip.width(), Quantity.of(5, "m"));
  }

  @Test
  void testRescale() {
    Scalar min = RealScalar.of(5);
    Scalar max = RealScalar.of(25);
    Clip clip = Clips.interval(min, max);
    assertEquals(clip.rescale(RealScalar.of(20)), RealScalar.of(3 / 4.0));
    assertEquals(clip.min(), RealScalar.of(5));
    assertEquals(clip.max(), RealScalar.of(25));
    assertEquals(clip.width(), RealScalar.of(20));
  }

  @Test
  void testClipMinMax() {
    Clip clip = Clips.interval(3, 5);
    assertEquals(clip.min(), RealScalar.of(3));
    assertEquals(clip.max(), RealScalar.of(5));
    assertEquals(clip.width(), RealScalar.of(2));
  }

  @Test
  void testClipOutside() {
    Clip clip = Clips.interval(3, 5);
    assertEquals(clip.requireInside(RealScalar.of(3.9)), RealScalar.of(3.9));
    assertThrows(Throw.class, () -> clip.requireInside(RealScalar.of(2.9)));
  }

  @Test
  void testClipInfty() {
    Clip clip = Clips.interval(DoubleScalar.NEGATIVE_INFINITY, DoubleScalar.POSITIVE_INFINITY);
    Tensor tensor = RandomVariate.of(NormalDistribution.standard(), 2, 3, 4);
    assertEquals(tensor.maps(clip), tensor);
  }

  @Test
  void testClipInftyQuantity() {
    Clip clip = Clips.interval(Quantity.of(Double.NEGATIVE_INFINITY, "V"), Quantity.of(Double.POSITIVE_INFINITY, "V"));
    Tensor tensor = RandomVariate.of(NormalDistribution.standard(), 2, 3, 4).maps(s -> Quantity.of(s, "V"));
    assertEquals(tensor.maps(clip), tensor);
  }

  @Test
  void testQuantityOutside() {
    Scalar min = Quantity.of(-3, "m");
    Scalar max = Quantity.of(2, "m");
    Clip clip = Clips.interval(min, max);
    assertFalse(clip.isOutside(Quantity.of(1, "m")));
    assertFalse(clip.isOutside(Quantity.of(2, "m")));
    assertTrue(clip.isOutside(Quantity.of(3, "m")));
    assertThrows(Throw.class, () -> clip.isOutside(Quantity.of(3, "V")));
  }

  @Test
  void testEps() {
    Clip clip = Clips.interval(0, Double.MIN_VALUE);
    assertEquals(clip.rescale(RealScalar.of(Double.MIN_VALUE)), RealScalar.ONE);
    assertEquals(clip.rescale(Pi.VALUE), RealScalar.ONE);
    assertEquals(clip.rescale(RealScalar.ZERO), RealScalar.ZERO);
    assertEquals(clip.rescale(Pi.VALUE.negate()), RealScalar.ZERO);
  }
}
