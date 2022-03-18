// code by jph
package ch.alpine.tensor.sca;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.qty.Quantity;
import ch.alpine.tensor.usr.AssertFail;

public class ClipPointTest {
  @Test
  public void testZeroWidth() {
    Clip clip = Clips.interval(2, 2);
    assertEquals(clip.apply(RealScalar.of(3)), RealScalar.of(2));
    assertTrue(clip.isInside(RealScalar.of(2)));
    assertFalse(clip.isInside(RealScalar.of(-2)));
    assertEquals(clip.requireInside(RealScalar.of(2)), RealScalar.of(2));
  }

  @Test
  public void testRescaleZeroWidth() {
    Clip clip = Clips.interval(2, 2);
    assertEquals(clip.rescale(RealScalar.of(-1)), RealScalar.ZERO);
    assertEquals(clip.rescale(RealScalar.ZERO), RealScalar.ZERO);
    assertEquals(clip.rescale(RealScalar.of(2)), RealScalar.ZERO);
    assertEquals(clip.rescale(RealScalar.of(4)), RealScalar.ZERO);
    assertEquals(clip.min(), RealScalar.of(2));
    assertEquals(clip.max(), RealScalar.of(2));
  }

  @Test
  public void testQuantity() {
    Scalar value = Quantity.of(3, "s");
    Clip clip = Clips.interval(value, value);
    assertEquals(clip.apply(Quantity.of(2, "s")), value);
    assertEquals(clip.width(), Quantity.of(0, "s"));
    assertFalse(clip.width().equals(RealScalar.ZERO));
    assertEquals(clip.requireInside(value), value);
  }

  @Test
  public void testVector() {
    Scalar value = Quantity.of(2, "m*s^-1");
    Clip clip = Clips.interval(value, value);
    Tensor vector = Tensors.fromString("{1[m*s^-1], 5[m*s^-1], 2[m*s^-1]}");
    Tensor result = clip.of(vector);
    assertEquals(result, Tensors.fromString("{2[m*s^-1], 2[m*s^-1], 2[m*s^-1]}"));
  }

  @Test
  public void testRescale() {
    Scalar value = Quantity.of(2, "m*s^-1");
    Clip clip = Clips.interval(value, value);
    assertEquals(clip.rescale(Quantity.of(4, "m*s^-1")), RealScalar.ZERO);
    AssertFail.of(() -> clip.requireInside(Quantity.of(3, "m*s^-1")));
  }

  @Test
  public void testRescaleFail() {
    Scalar value = Quantity.of(2, "m*s^-1");
    Clip clip = Clips.interval(value, value);
    assertEquals(clip.requireInside(value), value);
    AssertFail.of(() -> clip.rescale(Quantity.of(2, "kg")));
  }

  @Test
  public void testVisibility() {
    assertEquals(ClipPoint.class.getModifiers(), 0);
  }
}
