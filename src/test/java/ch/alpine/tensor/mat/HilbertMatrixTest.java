// code by jph
package ch.alpine.tensor.mat;

import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.alg.Transpose;
import ch.alpine.tensor.usr.AssertFail;
import junit.framework.TestCase;

public class HilbertMatrixTest extends TestCase {
  public void testMatrix() {
    Tensor m = HilbertMatrix.of(3, 4);
    Tensor d = Transpose.of(m);
    assertEquals(d, HilbertMatrix.of(4, 3));
  }

  public void testInverse() {
    Tensor m = HilbertMatrix.of(4, 4);
    Tensor mi = Inverse.of(m);
    Tensor ci = Tensors.fromString("{{16, -120, 240, -140}, {-120, 1200, -2700, 1680}, {240, -2700, 6480, -4200}, {-140, 1680, -4200, 2800}}");
    assertEquals(mi, ci);
  }

  public void testFail() {
    AssertFail.of(() -> HilbertMatrix.of(0, 4));
    AssertFail.of(() -> HilbertMatrix.of(4, 0));
  }
}
