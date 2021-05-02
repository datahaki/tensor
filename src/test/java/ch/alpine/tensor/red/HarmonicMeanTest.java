// code by jph
package ch.alpine.tensor.red;

import ch.alpine.tensor.RationalScalar;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.mat.HilbertMatrix;
import ch.alpine.tensor.usr.AssertFail;
import junit.framework.TestCase;

public class HarmonicMeanTest extends TestCase {
  public void testGeo1() {
    Tensor a = HarmonicMean.ofVector(Tensors.vector(8, 27, 525));
    assertEquals(a, RationalScalar.of(113400, 6197));
    Tensor b = HarmonicMean.ofVector(Tensors.vector(8, -27, 3));
    assertEquals(b, RationalScalar.of(648, 91));
  }

  public void testEmpty() {
    AssertFail.of(() -> HarmonicMean.ofVector(Tensors.empty()));
  }

  public void testZero() {
    AssertFail.of(() -> HarmonicMean.ofVector(Tensors.vector(3, 0, 2)));
  }

  public void testScalarFail() {
    AssertFail.of(() -> HarmonicMean.ofVector(RealScalar.ONE));
  }

  public void testMatrixFail() {
    AssertFail.of(() -> HarmonicMean.ofVector(HilbertMatrix.of(4)));
  }
}
