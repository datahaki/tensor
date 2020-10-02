// code by jph
package ch.ethz.idsc.tensor.lie;

import java.lang.reflect.Modifier;

import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.usr.AssertFail;
import junit.framework.TestCase;

public class QRDecompositionImplTest extends TestCase {
  public void testEmpty() {
    AssertFail.of(() -> QRDecomposition.of(Tensors.empty()));
  }

  public void testFail() {
    AssertFail.of(() -> QRDecomposition.of(Tensors.fromString("{{1, 2}, {3, 4, 5}}")));
    AssertFail.of(() -> QRDecomposition.of(LeviCivitaTensor.of(3)));
  }

  public void testPackageVisibility() {
    assertTrue(Modifier.isPublic(QRDecomposition.class.getModifiers()));
    assertFalse(Modifier.isPublic(QRDecompositionImpl.class.getModifiers()));
  }
}
