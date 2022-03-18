// code by jph
package ch.alpine.tensor.sca;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.ComplexScalar;
import ch.alpine.tensor.DoubleScalar;
import ch.alpine.tensor.ExactScalarQ;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.TensorRuntimeException;
import ch.alpine.tensor.num.GaussScalar;
import ch.alpine.tensor.num.Pi;
import ch.alpine.tensor.qty.Quantity;

public class ClipsTest {
  @Test
  public void testSignZero1() {
    Clip clip = Clips.interval(-0.0, +0.0);
    assertTrue(clip instanceof ClipPoint);
    assertEquals(clip.min(), RealScalar.ZERO);
    assertEquals(clip.max(), RealScalar.ZERO);
  }

  @Test
  public void testSignZero2() {
    Clip clip = Clips.interval(+0.0, -0.0);
    assertTrue(clip instanceof ClipPoint);
    assertEquals(clip.min(), RealScalar.ZERO);
    assertEquals(clip.max(), RealScalar.ZERO);
  }

  @Test
  public void testInftyHalf() {
    Clip clip = Clips.interval(-0.0, Double.POSITIVE_INFINITY);
    assertTrue(clip instanceof ClipInterval);
    assertTrue(clip.isInside(Pi.VALUE));
    assertEquals(clip.min(), RealScalar.ZERO);
    assertEquals(clip.max(), DoubleScalar.POSITIVE_INFINITY);
    assertEquals(clip.width(), DoubleScalar.POSITIVE_INFINITY);
    clip.requireInside(DoubleScalar.POSITIVE_INFINITY);
    assertFalse(clip.isInside(DoubleScalar.NEGATIVE_INFINITY));
  }

  @Test
  public void testRealNumbers() {
    Clip clip = Clips.interval(Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY);
    assertTrue(clip instanceof ClipInterval);
    assertTrue(clip.isInside(Pi.VALUE));
    assertEquals(clip.min(), DoubleScalar.NEGATIVE_INFINITY);
    assertEquals(clip.max(), DoubleScalar.POSITIVE_INFINITY);
    assertEquals(clip.width(), DoubleScalar.POSITIVE_INFINITY);
    clip.requireInside(DoubleScalar.NEGATIVE_INFINITY);
    clip.requireInside(DoubleScalar.POSITIVE_INFINITY);
  }

  @Test
  public void testAbsolute() {
    Clip clip = Clips.absolute(Quantity.of(10, "N"));
    clip.requireInside(Quantity.of(-10, "N"));
    clip.requireInside(Quantity.of(+10, "N"));
    assertTrue(clip.isOutside(Quantity.of(-10.1, "N")));
    assertTrue(clip.isOutside(Quantity.of(+10.1, "N")));
  }

  @Test
  public void testAbsoluteNumber() {
    Clip clip = Clips.absolute(Math.PI);
    assertTrue(clip.isInside(RealScalar.of(-1)));
    assertTrue(clip.isInside(RealScalar.of(+1)));
    assertTrue(clip.isInside(RealScalar.of(-Math.PI)));
    assertTrue(clip.isInside(RealScalar.of(+Math.PI)));
    assertTrue(clip.isOutside(RealScalar.of(Math.nextUp(Math.PI))));
    assertTrue(clip.isOutside(RealScalar.of(Math.nextDown(-Math.PI))));
  }

  @Test
  public void testAbsoluteOne() {
    Clip clip = Clips.absoluteOne();
    assertTrue(clip.isInside(RealScalar.of(-1)));
    assertTrue(clip.isInside(RealScalar.of(+1)));
    assertTrue(clip.isOutside(RealScalar.of(Math.nextUp(1.0))));
    assertTrue(clip.isOutside(RealScalar.of(Math.nextDown(-1.0))));
  }

  @Test
  public void testAbsoluteZero() {
    Clip clip = Clips.absolute(Quantity.of(0, "N"));
    Scalar scalar = clip.rescale(Quantity.of(5, "N"));
    assertEquals(scalar, RealScalar.ZERO);
    ExactScalarQ.require(scalar);
  }

  @Test
  public void testPositive() {
    Clip clip = Clips.positive(Quantity.of(10, "N"));
    clip.requireInside(Quantity.of(0, "N"));
    clip.requireInside(Quantity.of(+10, "N"));
    assertTrue(clip.isOutside(Quantity.of(Math.nextDown(0.0), "N")));
    assertTrue(clip.isOutside(Quantity.of(Math.nextUp(10.0), "N")));
  }

  @Test
  public void testPositiveNumber() {
    Clip clip = Clips.positive(10);
    clip.requireInside(Quantity.of(0, ""));
    clip.requireInside(Quantity.of(+10, ""));
    assertTrue(clip.isOutside(Quantity.of(Math.nextDown(0.0), "")));
    assertTrue(clip.isOutside(Quantity.of(Math.nextUp(10.0), "")));
  }

  @Test
  public void testIntersectionSimple() {
    Clip clip = Clips.intersection(Clips.interval(2, 6), Clips.interval(3, 10));
    assertEquals(clip.min(), RealScalar.of(3));
    assertEquals(clip.max(), RealScalar.of(6));
  }

  @Test
  public void testIntersectionPoint() {
    Clip clip = Clips.intersection(Clips.interval(2, 6), Clips.interval(-3, 2));
    assertEquals(clip.min(), RealScalar.of(2));
    assertEquals(clip.max(), RealScalar.of(2));
  }

  @Test
  public void testGaussScalar() {
    Clip clip = Clips.positive(GaussScalar.of(3, 13));
    clip.requireInside(GaussScalar.of(0, 13));
    clip.requireInside(GaussScalar.of(1, 13));
    clip.requireInside(GaussScalar.of(2, 13));
    clip.requireInside(GaussScalar.of(3, 13));
    assertFalse(clip.isInside(GaussScalar.of(4, 13)));
  }

  @Test
  public void testIntersectionFail() {
    assertThrows(TensorRuntimeException.class, () -> Clips.intersection(Clips.interval(2, 3), Clips.interval(5, 10)));
  }

  @Test
  public void testCoverSimple() {
    Clip clip = Clips.cover(Clips.interval(2, 6), Clips.interval(3, 10));
    assertEquals(clip.min(), RealScalar.of(2));
    assertEquals(clip.max(), RealScalar.of(10));
  }

  @Test
  public void testCoverFail0() {
    Clip c1 = Clips.positive(Quantity.of(0, "m"));
    Clip c2 = Clips.positive(Quantity.of(0, "s"));
    assertThrows(TensorRuntimeException.class, () -> Clips.cover(c1, c2));
    assertThrows(TensorRuntimeException.class, () -> Clips.intersection(c1, c2));
  }

  @Test
  public void testCoverFail1() {
    Clip c1 = Clips.positive(Quantity.of(1, "m"));
    Clip c2 = Clips.positive(Quantity.of(2, "s"));
    assertThrows(TensorRuntimeException.class, () -> Clips.cover(c1, c2));
    assertThrows(TensorRuntimeException.class, () -> Clips.intersection(c1, c2));
  }

  @Test
  public void testCoverPoint() {
    Clip clip = Clips.cover(Clips.interval(2, 6), Clips.interval(-3, 2));
    assertEquals(clip.min(), RealScalar.of(-3));
    assertEquals(clip.max(), RealScalar.of(6));
  }

  @Test
  public void testPositiveFail() {
    assertThrows(TensorRuntimeException.class, () -> Clips.positive(Quantity.of(-1, "kg")));
  }

  @Test
  public void testNaNFail() {
    assertThrows(TensorRuntimeException.class, () -> Clips.interval(DoubleScalar.INDETERMINATE, DoubleScalar.INDETERMINATE));
    assertThrows(TensorRuntimeException.class, () -> Clips.interval(RealScalar.ZERO, DoubleScalar.INDETERMINATE));
    assertThrows(TensorRuntimeException.class, () -> Clips.interval(DoubleScalar.INDETERMINATE, RealScalar.ZERO));
  }

  @Test
  public void testAbsoluteFail() {
    assertThrows(TensorRuntimeException.class, () -> Clips.absolute(Quantity.of(-1, "kg")));
  }

  @Test
  public void testInsideFail() {
    assertThrows(TensorRuntimeException.class, () -> Clips.unit().isInside(Quantity.of(0.5, "m")));
  }

  @Test
  public void testQuantityFail() {
    assertThrows(TensorRuntimeException.class, () -> Clips.unit().apply(Quantity.of(-5, "m")));
    assertThrows(TensorRuntimeException.class, () -> Clips.absoluteOne().apply(Quantity.of(-5, "m")));
  }

  @Test
  public void testQuantityMixedZero() {
    assertThrows(TensorRuntimeException.class, () -> Clips.interval(Quantity.of(0, "m"), Quantity.of(0, "")));
  }

  @Test
  public void testQuantityMixedUnitsFail() {
    assertThrows(TensorRuntimeException.class, () -> Clips.interval(Quantity.of(2, "m"), Quantity.of(3, "kg")));
  }

  @Test
  public void testComplexFail() {
    assertThrows(ClassCastException.class, () -> Clips.absolute(ComplexScalar.I));
  }
}
