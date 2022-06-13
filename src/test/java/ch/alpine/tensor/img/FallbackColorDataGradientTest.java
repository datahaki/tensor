// code by jph
package ch.alpine.tensor.img;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.lang.reflect.Modifier;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.DoubleScalar;
import ch.alpine.tensor.alg.Array;

class FallbackColorDataGradientTest {
  @Test
  void testNull() {
    assertEquals(FallbackColorDataGradient.INSTANCE.apply(DoubleScalar.INDETERMINATE), Array.zeros(4));
  }

  @Test
  void testDerive() {
    assertEquals(FallbackColorDataGradient.INSTANCE.deriveWithOpacity(DoubleScalar.INDETERMINATE), FallbackColorDataGradient.INSTANCE);
  }

  @Test
  void testFailNullApply() {
    assertThrows(NullPointerException.class, () -> FallbackColorDataGradient.INSTANCE.apply(null));
  }

  @Test
  void testFailNullDerive() {
    assertThrows(NullPointerException.class, () -> FallbackColorDataGradient.INSTANCE.deriveWithOpacity(null));
  }

  @Test
  void testPackageVisibility() {
    assertTrue(Modifier.isPublic(LinearColorDataGradient.class.getModifiers()));
    assertFalse(Modifier.isPublic(FallbackColorDataGradient.class.getModifiers()));
  }
}
