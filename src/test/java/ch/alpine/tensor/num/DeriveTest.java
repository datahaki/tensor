// code by jph
package ch.alpine.tensor.num;

import ch.alpine.tensor.ExactTensorQ;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.mat.HilbertMatrix;
import ch.alpine.tensor.usr.AssertFail;
import junit.framework.TestCase;

public class DeriveTest extends TestCase {
  public void testSimple() {
    Tensor coeffs = Tensors.vector(-3, 4, -5, 8, 1);
    Tensor result = Derive.of(coeffs);
    ExactTensorQ.require(result);
    assertEquals(result, Tensors.vector(4, -5 * 2, 8 * 3, 1 * 4));
  }

  public void testEmpty() {
    assertEquals(Derive.of(Tensors.vector()), Tensors.vector());
    assertEquals(Derive.of(Tensors.vector(3)), Tensors.empty());
  }

  public void testScalarFail() {
    AssertFail.of(() -> Derive.of(RealScalar.ONE));
  }

  public void testMatrixFail() {
    AssertFail.of(() -> Derive.of(HilbertMatrix.of(4, 5)));
  }

  public void testUnstructuredFail() {
    AssertFail.of(() -> Derive.of(Tensors.fromString("{2, {1}}")));
  }
}
