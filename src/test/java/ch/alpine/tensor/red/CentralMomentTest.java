// code by jph
package ch.alpine.tensor.red;

import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Scalars;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.mat.HilbertMatrix;
import ch.alpine.tensor.mat.Tolerance;
import ch.alpine.tensor.qty.Quantity;
import ch.alpine.tensor.usr.AssertFail;
import junit.framework.TestCase;

public class CentralMomentTest extends TestCase {
  public void testVarious() {
    Tensor tensor = Tensors.vector(10, 2, 3, 4, 1);
    assertEquals(CentralMoment.of(tensor, 0), RealScalar.of(1));
    assertEquals(CentralMoment.of(tensor, 1), RealScalar.of(0));
    assertEquals(CentralMoment.of(tensor, 2), RealScalar.of(10));
    assertEquals(CentralMoment.of(tensor, 3), RealScalar.of(36));
    assertEquals(CentralMoment.of(tensor, 4), Scalars.fromString("1394/5"));
  }

  public void testComplex() {
    Tensor tensor = Tensors.vector(10, 2, 3, 4, 1);
    Scalar result = CentralMoment.of(tensor, 1.3);
    Scalar gndtru = Scalars.fromString("1.1567572194352718 - 1.2351191805935866* I");
    Tolerance.CHOP.requireClose(result, gndtru);
  }

  public void testQuantity() {
    Tensor vector = Tensors.of(Quantity.of(2, "kg"), Quantity.of(3, "kg"));
    Scalar result = CentralMoment.of(vector, 2);
    assertEquals(result, Scalars.fromString("1/4[kg^2]"));
  }

  public void testEmptyFail() {
    AssertFail.of(() -> CentralMoment.of(Tensors.empty(), 2));
  }

  public void testMatrixFail() {
    AssertFail.of(() -> CentralMoment.of(HilbertMatrix.of(2, 3), RealScalar.of(2)));
  }
}
