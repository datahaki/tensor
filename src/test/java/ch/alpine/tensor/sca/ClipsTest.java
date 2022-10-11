// code by jph
package ch.alpine.tensor.sca;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.NavigableSet;
import java.util.TreeMap;
import java.util.TreeSet;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.ComplexScalar;
import ch.alpine.tensor.DoubleScalar;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Throw;
import ch.alpine.tensor.chq.ExactScalarQ;
import ch.alpine.tensor.num.GaussScalar;
import ch.alpine.tensor.num.Pi;
import ch.alpine.tensor.qty.DateTime;
import ch.alpine.tensor.qty.Quantity;

class ClipsTest {
  @Test
  void testSignZero1() {
    Clip clip = Clips.interval(-0.0, +0.0);
    assertInstanceOf(ClipPoint.class, clip);
    assertEquals(clip.min(), RealScalar.ZERO);
    assertEquals(clip.max(), RealScalar.ZERO);
  }

  @Test
  void testSignZero2() {
    Clip clip = Clips.interval(+0.0, -0.0);
    assertInstanceOf(ClipPoint.class, clip);
    assertEquals(clip.min(), RealScalar.ZERO);
    assertEquals(clip.max(), RealScalar.ZERO);
  }

  @Test
  void testInftyHalf() {
    Clip clip = Clips.interval(-0.0, Double.POSITIVE_INFINITY);
    assertInstanceOf(ClipInterval.class, clip);
    assertTrue(clip.isInside(Pi.VALUE));
    assertEquals(clip.min(), RealScalar.ZERO);
    assertEquals(clip.max(), DoubleScalar.POSITIVE_INFINITY);
    assertEquals(clip.width(), DoubleScalar.POSITIVE_INFINITY);
    clip.requireInside(DoubleScalar.POSITIVE_INFINITY);
    assertFalse(clip.isInside(DoubleScalar.NEGATIVE_INFINITY));
  }

  @Test
  void testRealNumbers() {
    Clip clip = Clips.interval(Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY);
    assertInstanceOf(ClipInterval.class, clip);
    assertTrue(clip.isInside(Pi.VALUE));
    assertEquals(clip.min(), DoubleScalar.NEGATIVE_INFINITY);
    assertEquals(clip.max(), DoubleScalar.POSITIVE_INFINITY);
    assertEquals(clip.width(), DoubleScalar.POSITIVE_INFINITY);
    clip.requireInside(DoubleScalar.NEGATIVE_INFINITY);
    clip.requireInside(DoubleScalar.POSITIVE_INFINITY);
  }

  @Test
  void testAbsolute() {
    Clip clip = Clips.absolute(Quantity.of(10, "N"));
    clip.requireInside(Quantity.of(-10, "N"));
    clip.requireInside(Quantity.of(+10, "N"));
    assertTrue(clip.isOutside(Quantity.of(-10.1, "N")));
    assertTrue(clip.isOutside(Quantity.of(+10.1, "N")));
  }

  @Test
  void testAbsoluteNumber() {
    Clip clip = Clips.absolute(Math.PI);
    assertTrue(clip.isInside(RealScalar.of(-1)));
    assertTrue(clip.isInside(RealScalar.of(+1)));
    assertTrue(clip.isInside(RealScalar.of(-Math.PI)));
    assertTrue(clip.isInside(RealScalar.of(+Math.PI)));
    assertTrue(clip.isOutside(RealScalar.of(Math.nextUp(Math.PI))));
    assertTrue(clip.isOutside(RealScalar.of(Math.nextDown(-Math.PI))));
  }

  @Test
  void testAbsoluteOne() {
    Clip clip = Clips.absoluteOne();
    assertTrue(clip.isInside(RealScalar.of(-1)));
    assertTrue(clip.isInside(RealScalar.of(+1)));
    assertTrue(clip.isOutside(RealScalar.of(Math.nextUp(1.0))));
    assertTrue(clip.isOutside(RealScalar.of(Math.nextDown(-1.0))));
  }

  @Test
  void testAbsoluteZero() {
    Clip clip = Clips.absolute(Quantity.of(0, "N"));
    Scalar scalar = clip.rescale(Quantity.of(5, "N"));
    assertEquals(scalar, RealScalar.ZERO);
    ExactScalarQ.require(scalar);
  }

  @Test
  void testPositive() {
    Clip clip = Clips.positive(Quantity.of(10, "N"));
    clip.requireInside(Quantity.of(0, "N"));
    clip.requireInside(Quantity.of(+10, "N"));
    assertTrue(clip.isOutside(Quantity.of(Math.nextDown(0.0), "N")));
    assertTrue(clip.isOutside(Quantity.of(Math.nextUp(10.0), "N")));
  }

  @Test
  void testPositiveNumber() {
    Clip clip = Clips.positive(10);
    clip.requireInside(Quantity.of(0, ""));
    clip.requireInside(Quantity.of(+10, ""));
    assertTrue(clip.isOutside(Quantity.of(Math.nextDown(0.0), "")));
    assertTrue(clip.isOutside(Quantity.of(Math.nextUp(10.0), "")));
  }

  @Test
  void testIntersectionSimple() {
    Clip clip = Clips.intersection(Clips.interval(2, 6), Clips.interval(3, 10));
    assertEquals(clip.min(), RealScalar.of(3));
    assertEquals(clip.max(), RealScalar.of(6));
  }

  @Test
  void testIntersectionPoint() {
    Clip clip = Clips.intersection(Clips.interval(2, 6), Clips.interval(-3, 2));
    assertEquals(clip.min(), RealScalar.of(2));
    assertEquals(clip.max(), RealScalar.of(2));
  }

  @Test
  void testGaussScalar() {
    Clip clip = Clips.positive(GaussScalar.of(3, 13));
    clip.requireInside(GaussScalar.of(0, 13));
    clip.requireInside(GaussScalar.of(1, 13));
    clip.requireInside(GaussScalar.of(2, 13));
    clip.requireInside(GaussScalar.of(3, 13));
    assertFalse(clip.isInside(GaussScalar.of(4, 13)));
  }

  @Test
  void testIntersectionFail() {
    assertThrows(Throw.class, () -> Clips.intersection(Clips.interval(2, 3), Clips.interval(5, 10)));
  }

  @Test
  void testCentered() {
    Clip clip = Clips.centered(10, 1);
    assertEquals(clip.min(), RealScalar.of(9));
    assertEquals(clip.max(), RealScalar.of(11));
  }

  @Test
  void testCenteredDT() {
    Clip clip = Clips.centered(DateTime.now(), Quantity.of(3, "h"));
    assertEquals(clip.width(), Quantity.of(21600, "s"));
    assertInstanceOf(DateTime.class, clip.min());
    assertInstanceOf(DateTime.class, clip.max());
  }

  @Test
  void testKeycover() {
    TreeMap<DateTime, Object> treeMap = new TreeMap<>();
    treeMap.put(DateTime.of(2022, 3, 4, 5, 6), null);
    treeMap.put(DateTime.of(2021, 6, 4, 0, 0), null);
    {
      Clip clip = Clips.keycover(treeMap);
      assertEquals(clip.min(), DateTime.of(2021, 6, 4, 0, 0));
      assertEquals(clip.max(), DateTime.of(2022, 3, 4, 5, 6));
    }
    {
      Clip clip = Clips.setcover(treeMap.navigableKeySet());
      assertEquals(clip.min(), DateTime.of(2021, 6, 4, 0, 0));
      assertEquals(clip.max(), DateTime.of(2022, 3, 4, 5, 6));
    }
  }

  @Test
  void testCoverSimple() {
    Clip clip = Clips.cover(Clips.interval(2, 6), Clips.interval(3, 10));
    assertEquals(clip.min(), RealScalar.of(2));
    assertEquals(clip.max(), RealScalar.of(10));
  }

  @Test
  void testCoverFail0() {
    Clip c1 = Clips.positive(Quantity.of(0, "m"));
    Clip c2 = Clips.positive(Quantity.of(0, "s"));
    assertThrows(Throw.class, () -> Clips.cover(c1, c2));
    assertThrows(Throw.class, () -> Clips.intersection(c1, c2));
  }

  @Test
  void testCoverFail1() {
    Clip c1 = Clips.positive(Quantity.of(1, "m"));
    Clip c2 = Clips.positive(Quantity.of(2, "s"));
    assertThrows(Throw.class, () -> Clips.cover(c1, c2));
    assertThrows(Throw.class, () -> Clips.intersection(c1, c2));
  }

  @Test
  void testCoverPoint() {
    Clip clip = Clips.cover(Clips.interval(2, 6), Clips.interval(-3, 2));
    assertEquals(clip.min(), RealScalar.of(-3));
    assertEquals(clip.max(), RealScalar.of(6));
  }

  @Test
  void testClipsCover() {
    Clip clip = Clips.cover(Clips.interval(0, 1), Clips.interval(5, 6));
    assertEquals(clip, Clips.interval(0, 6));
  }

  @Test
  void testClipsCoverDate() {
    NavigableSet<DateTime> navigableSet = new TreeSet<>();
    assertThrows(Exception.class, () -> Clips.setcover(navigableSet));
    navigableSet.add(DateTime.now());
    assertEquals(Clips.setcover(navigableSet).width(), Quantity.of(0, "s"));
  }

  @Test
  void testPositiveFail() {
    assertThrows(Throw.class, () -> Clips.positive(Quantity.of(-1, "kg")));
  }

  @Test
  void testNaNFail() {
    assertThrows(Throw.class, () -> Clips.interval(DoubleScalar.INDETERMINATE, DoubleScalar.INDETERMINATE));
    assertThrows(Throw.class, () -> Clips.interval(RealScalar.ZERO, DoubleScalar.INDETERMINATE));
    assertThrows(Throw.class, () -> Clips.interval(DoubleScalar.INDETERMINATE, RealScalar.ZERO));
  }

  @Test
  void testAbsoluteFail() {
    assertThrows(Throw.class, () -> Clips.absolute(Quantity.of(-1, "kg")));
  }

  @Test
  void testInsideFail() {
    assertThrows(Throw.class, () -> Clips.unit().isInside(Quantity.of(0.5, "m")));
  }

  @Test
  void testQuantityFail() {
    assertThrows(Throw.class, () -> Clips.unit().apply(Quantity.of(-5, "m")));
    assertThrows(Throw.class, () -> Clips.absoluteOne().apply(Quantity.of(-5, "m")));
  }

  @Test
  void testQuantityMixedZero() {
    assertThrows(Throw.class, () -> Clips.interval(Quantity.of(0, "m"), Quantity.of(0, "")));
  }

  @Test
  void testQuantityMixedUnitsFail() {
    assertThrows(Throw.class, () -> Clips.interval(Quantity.of(2, "m"), Quantity.of(3, "kg")));
  }

  @Test
  void testComplexFail() {
    assertThrows(ClassCastException.class, () -> Clips.absolute(ComplexScalar.I));
  }

  @Test
  void testCoverEmptyFail() {
    assertThrows(Exception.class, () -> Clips.keycover(new TreeMap<>()));
    assertThrows(Exception.class, () -> Clips.setcover(new TreeSet<>()));
  }
}
