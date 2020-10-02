// code by jph
package ch.ethz.idsc.tensor.img;

import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.usr.AssertFail;
import junit.framework.TestCase;

public class TensorExtractTest extends TestCase {
  public void testEmpty() {
    assertEquals(TensorExtract.of(Tensors.empty(), 0, t -> t), Tensors.empty());
  }

  public void testRadiusFail() {
    AssertFail.of(() -> TensorExtract.of(Tensors.empty(), -1, t -> t));
  }

  public void testFunctionNullFail() {
    AssertFail.of(() -> TensorExtract.of(Tensors.empty(), 2, null));
  }
}
