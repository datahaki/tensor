// code by jph
package ch.ethz.idsc.tensor.num;

import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Array;
import ch.ethz.idsc.tensor.mat.HilbertMatrix;
import ch.ethz.idsc.tensor.usr.AssertFail;
import junit.framework.TestCase;

public class RootsFailTest extends TestCase {
  public void testScalarFail() {
    AssertFail.of(() -> Roots.of(RealScalar.ONE));
  }

  public void testEmptyFail() {
    AssertFail.of(() -> Roots.of(Tensors.empty()));
  }

  public void testOnes() {
    Tensor coeffs = Tensors.vector(0);
    AssertFail.of(() -> Roots.of(coeffs));
  }

  public void testConstantZeroFail() {
    AssertFail.of(() -> Roots.of(Tensors.vector(0)));
  }

  public void testZerosFail() {
    for (int n = 0; n < 10; ++n) {
      int fn = n;
      AssertFail.of(() -> Roots.of(Array.zeros(fn)));
    }
  }

  public void testMatrixFail() {
    AssertFail.of(() -> Roots.of(HilbertMatrix.of(2, 3)));
  }

  public void testNotImplemented() {
    AssertFail.of(() -> Roots.of(Tensors.vector(1, 2, 3, 4, 5, 6)));
  }
}
