// code by jph
package ch.alpine.tensor.sca;

import ch.alpine.tensor.ComplexScalar;
import ch.alpine.tensor.DoubleScalar;
import ch.alpine.tensor.ExactScalarQ;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.num.GaussScalar;
import ch.alpine.tensor.num.Pi;
import ch.alpine.tensor.qty.Quantity;
import ch.alpine.tensor.usr.AssertFail;
import junit.framework.TestCase;

public class ClipsTest extends TestCase {
  public void testSignZero1() {
    Clip clip = Clips.interval(-0.0, +0.0);
    assertTrue(clip instanceof ClipPoint);
    assertEquals(clip.min(), RealScalar.ZERO);
    assertEquals(clip.max(), RealScalar.ZERO);
  }

  public void testSignZero2() {
    Clip clip = Clips.interval(+0.0, -0.0);
    assertTrue(clip instanceof ClipPoint);
    assertEquals(clip.min(), RealScalar.ZERO);
    assertEquals(clip.max(), RealScalar.ZERO);
  }

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

  public void testAbsolute() {
    Clip clip = Clips.absolute(Quantity.of(10, "N"));
    clip.requireInside(Quantity.of(-10, "N"));
    clip.requireInside(Quantity.of(+10, "N"));
    assertTrue(clip.isOutside(Quantity.of(-10.1, "N")));
    assertTrue(clip.isOutside(Quantity.of(+10.1, "N")));
  }

  public void testAbsoluteNumber() {
    Clip clip = Clips.absolute(Math.PI);
    assertTrue(clip.isInside(RealScalar.of(-1)));
    assertTrue(clip.isInside(RealScalar.of(+1)));
    assertTrue(clip.isInside(RealScalar.of(-Math.PI)));
    assertTrue(clip.isInside(RealScalar.of(+Math.PI)));
    assertTrue(clip.isOutside(RealScalar.of(Math.nextUp(Math.PI))));
    assertTrue(clip.isOutside(RealScalar.of(Math.nextDown(-Math.PI))));
  }

  public void testAbsoluteOne() {
    Clip clip = Clips.absoluteOne();
    assertTrue(clip.isInside(RealScalar.of(-1)));
    assertTrue(clip.isInside(RealScalar.of(+1)));
    assertTrue(clip.isOutside(RealScalar.of(Math.nextUp(1.0))));
    assertTrue(clip.isOutside(RealScalar.of(Math.nextDown(-1.0))));
  }

  public void testAbsoluteZero() {
    Clip clip = Clips.absolute(Quantity.of(0, "N"));
    Scalar scalar = clip.rescale(Quantity.of(5, "N"));
    assertEquals(scalar, RealScalar.ZERO);
    ExactScalarQ.require(scalar);
  }

  public void testPositive() {
    Clip clip = Clips.positive(Quantity.of(10, "N"));
    clip.requireInside(Quantity.of(0, "N"));
    clip.requireInside(Quantity.of(+10, "N"));
    assertTrue(clip.isOutside(Quantity.of(Math.nextDown(0.0), "N")));
    assertTrue(clip.isOutside(Quantity.of(Math.nextUp(10.0), "N")));
  }

  public void testPositiveNumber() {
    Clip clip = Clips.positive(10);
    clip.requireInside(Quantity.of(0, ""));
    clip.requireInside(Quantity.of(+10, ""));
    assertTrue(clip.isOutside(Quantity.of(Math.nextDown(0.0), "")));
    assertTrue(clip.isOutside(Quantity.of(Math.nextUp(10.0), "")));
  }

  public void testIntersectionSimple() {
    Clip clip = Clips.intersection(Clips.interval(2, 6), Clips.interval(3, 10));
    assertEquals(clip.min(), RealScalar.of(3));
    assertEquals(clip.max(), RealScalar.of(6));
  }

  public void testIntersectionPoint() {
    Clip clip = Clips.intersection(Clips.interval(2, 6), Clips.interval(-3, 2));
    assertEquals(clip.min(), RealScalar.of(2));
    assertEquals(clip.max(), RealScalar.of(2));
  }

  public void testGaussScalar() {
    Clip clip = Clips.positive(GaussScalar.of(3, 13));
    clip.requireInside(GaussScalar.of(0, 13));
    clip.requireInside(GaussScalar.of(1, 13));
    clip.requireInside(GaussScalar.of(2, 13));
    clip.requireInside(GaussScalar.of(3, 13));
    assertFalse(clip.isInside(GaussScalar.of(4, 13)));
  }

  public void testIntersectionFail() {
    AssertFail.of(() -> Clips.intersection(Clips.interval(2, 3), Clips.interval(5, 10)));
  }

  public void testCoverSimple() {
    Clip clip = Clips.cover(Clips.interval(2, 6), Clips.interval(3, 10));
    assertEquals(clip.min(), RealScalar.of(2));
    assertEquals(clip.max(), RealScalar.of(10));
  }

  public void testCoverFail0() {
    Clip c1 = Clips.positive(Quantity.of(0, "m"));
    Clip c2 = Clips.positive(Quantity.of(0, "s"));
    AssertFail.of(() -> Clips.cover(c1, c2));
    AssertFail.of(() -> Clips.intersection(c1, c2));
  }

  public void testCoverFail1() {
    Clip c1 = Clips.positive(Quantity.of(1, "m"));
    Clip c2 = Clips.positive(Quantity.of(2, "s"));
    AssertFail.of(() -> Clips.cover(c1, c2));
    AssertFail.of(() -> Clips.intersection(c1, c2));
  }

  public void testCoverPoint() {
    Clip clip = Clips.cover(Clips.interval(2, 6), Clips.interval(-3, 2));
    assertEquals(clip.min(), RealScalar.of(-3));
    assertEquals(clip.max(), RealScalar.of(6));
  }

  public void testPositiveFail() {
    AssertFail.of(() -> Clips.positive(Quantity.of(-1, "kg")));
  }

  public void testNaNFail() {
    AssertFail.of(() -> Clips.interval(DoubleScalar.INDETERMINATE, DoubleScalar.INDETERMINATE));
    AssertFail.of(() -> Clips.interval(RealScalar.ZERO, DoubleScalar.INDETERMINATE));
    AssertFail.of(() -> Clips.interval(DoubleScalar.INDETERMINATE, RealScalar.ZERO));
  }

  public void testAbsoluteFail() {
    AssertFail.of(() -> Clips.absolute(Quantity.of(-1, "kg")));
  }

  public void testInsideFail() {
    AssertFail.of(() -> Clips.unit().isInside(Quantity.of(0.5, "m")));
  }

  public void testQuantityFail() {
    AssertFail.of(() -> Clips.unit().apply(Quantity.of(-5, "m")));
    AssertFail.of(() -> Clips.absoluteOne().apply(Quantity.of(-5, "m")));
  }

  public void testQuantityMixedZero() {
    AssertFail.of(() -> Clips.interval(Quantity.of(0, "m"), Quantity.of(0, "")));
  }

  public void testQuantityMixedUnitsFail() {
    AssertFail.of(() -> Clips.interval(Quantity.of(2, "m"), Quantity.of(3, "kg")));
  }

  public void testComplexFail() {
    AssertFail.of(() -> Clips.absolute(ComplexScalar.I));
  }
}
