// code by jph
package ch.ethz.idsc.tensor.img;

import java.lang.reflect.Modifier;

import ch.ethz.idsc.tensor.DoubleScalar;
import ch.ethz.idsc.tensor.alg.Array;
import ch.ethz.idsc.tensor.usr.AssertFail;
import junit.framework.TestCase;

public class FallbackColorDataGradientTest extends TestCase {
  public void testNull() {
    assertEquals(FallbackColorDataGradient.INSTANCE.apply(DoubleScalar.INDETERMINATE), Array.zeros(4));
  }

  public void testDerive() {
    assertEquals(FallbackColorDataGradient.INSTANCE.deriveWithOpacity(DoubleScalar.INDETERMINATE), FallbackColorDataGradient.INSTANCE);
  }

  public void testFailNullApply() {
    AssertFail.of(() -> FallbackColorDataGradient.INSTANCE.apply(null));
  }

  public void testFailNullDerive() {
    AssertFail.of(() -> FallbackColorDataGradient.INSTANCE.deriveWithOpacity(null));
  }

  public void testPackageVisibility() {
    assertTrue(Modifier.isPublic(LinearColorDataGradient.class.getModifiers()));
    assertFalse(Modifier.isPublic(FallbackColorDataGradient.class.getModifiers()));
  }
}
