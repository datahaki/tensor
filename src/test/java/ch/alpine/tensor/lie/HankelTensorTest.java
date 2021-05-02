// code by jph
package ch.alpine.tensor.lie;

import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.alg.Dimensions;
import ch.alpine.tensor.mat.SymmetricMatrixQ;
import ch.alpine.tensor.usr.AssertFail;
import junit.framework.TestCase;

public class HankelTensorTest extends TestCase {
  public void testRank2() {
    Tensor tensor = HankelTensor.of(Tensors.vector(1, 2, 3, 4, 5), 2);
    SymmetricMatrixQ.require(tensor);
  }

  public void testRank3a() {
    Tensor tensor = HankelTensor.of(Tensors.vector(1, 2, 3, 4), 3);
    tensor.stream().forEach(SymmetricMatrixQ::require);
  }

  public void testRank3b() {
    Tensor tensor = HankelTensor.of(Tensors.vector(0, 1, 2, 3, 4, 5, 6), 3);
    tensor.stream().forEach(SymmetricMatrixQ::require);
  }

  public void testRank4() {
    Tensor tensor = HankelTensor.of(Tensors.vector(1, 2, 3, 4, 5), 4);
    Dimensions dimensions = new Dimensions(tensor);
    assertTrue(dimensions.isArray());
    assertEquals(dimensions.list().size(), 4);
  }

  public void testFailVector() {
    AssertFail.of(() -> HankelTensor.of(RealScalar.ONE, 1));
    AssertFail.of(() -> HankelTensor.of(Tensors.fromString("{{1, 2}}"), 1));
  }

  public void testFailRank() {
    AssertFail.of(() -> HankelTensor.of(Tensors.vector(1, 2, 3, 4, 5, 6), 2));
  }
}
