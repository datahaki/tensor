// code by jph
package ch.alpine.tensor.sca;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.lang.reflect.Modifier;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.Throw;
import ch.alpine.tensor.qty.Quantity;

class ClipPointTest {
  @Test
  void testZeroWidth() {
    Clip clip = Clips.interval(2, 2);
    assertEquals(clip.apply(RealScalar.of(3)), RealScalar.of(2));
    assertTrue(clip.isInside(RealScalar.of(2)));
    assertFalse(clip.isInside(RealScalar.of(-2)));
    assertEquals(clip.requireInside(RealScalar.of(2)), RealScalar.of(2));
  }

  @Test
  void testRescaleZeroWidth() {
    Clip clip = Clips.interval(2, 2);
    assertEquals(clip.rescale(RealScalar.of(-1)), RealScalar.ZERO);
    assertEquals(clip.rescale(RealScalar.ZERO), RealScalar.ZERO);
    assertEquals(clip.rescale(RealScalar.of(2)), RealScalar.ZERO);
    assertEquals(clip.rescale(RealScalar.of(4)), RealScalar.ZERO);
    assertEquals(clip.min(), RealScalar.of(2));
    assertEquals(clip.max(), RealScalar.of(2));
  }

  @Test
  void testQuantity() {
    Scalar value = Quantity.of(3, "s");
    Clip clip = Clips.interval(value, value);
    assertEquals(clip.apply(Quantity.of(2, "s")), value);
    assertEquals(clip.width(), Quantity.of(0, "s"));
    assertNotEquals(clip.width(), RealScalar.ZERO);
    assertEquals(clip.requireInside(value), value);
  }

  @Test
  void testVector() {
    Scalar value = Quantity.of(2, "m*s^-1");
    Clip clip = Clips.interval(value, value);
    Tensor vector = Tensors.fromString("{1[m*s^-1], 5[m*s^-1], 2[m*s^-1]}");
    Tensor result = vector.maps(clip);
    assertEquals(result, Tensors.fromString("{2[m*s^-1], 2[m*s^-1], 2[m*s^-1]}"));
  }

  @Test
  void testRescale() {
    Scalar value = Quantity.of(2, "m*s^-1");
    Clip clip = Clips.interval(value, value);
    assertEquals(clip.rescale(Quantity.of(4, "m*s^-1")), RealScalar.ZERO);
    assertThrows(Throw.class, () -> clip.requireInside(Quantity.of(3, "m*s^-1")));
  }

  @Test
  void testRescaleFail() {
    Scalar value = Quantity.of(2, "m*s^-1");
    Clip clip = Clips.interval(value, value);
    assertEquals(clip.requireInside(value), value);
    assertThrows(Throw.class, () -> clip.rescale(Quantity.of(2, "kg")));
  }

  @Test
  void testVisibility() {
    assertFalse(Modifier.isPublic(ClipPoint.class.getModifiers()));
  }
}
