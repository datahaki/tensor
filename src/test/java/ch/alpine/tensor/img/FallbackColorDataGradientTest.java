// code by jph
package ch.alpine.tensor.img;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.lang.reflect.Modifier;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.DoubleScalar;
import ch.alpine.tensor.alg.Array;
import ch.alpine.tensor.usr.AssertFail;

public class FallbackColorDataGradientTest {
  @Test
  public void testNull() {
    assertEquals(FallbackColorDataGradient.INSTANCE.apply(DoubleScalar.INDETERMINATE), Array.zeros(4));
  }

  @Test
  public void testDerive() {
    assertEquals(FallbackColorDataGradient.INSTANCE.deriveWithOpacity(DoubleScalar.INDETERMINATE), FallbackColorDataGradient.INSTANCE);
  }

  @Test
  public void testFailNullApply() {
    AssertFail.of(() -> FallbackColorDataGradient.INSTANCE.apply(null));
  }

  @Test
  public void testFailNullDerive() {
    AssertFail.of(() -> FallbackColorDataGradient.INSTANCE.deriveWithOpacity(null));
  }

  @Test
  public void testPackageVisibility() {
    assertTrue(Modifier.isPublic(LinearColorDataGradient.class.getModifiers()));
    assertFalse(Modifier.isPublic(FallbackColorDataGradient.class.getModifiers()));
  }
}
