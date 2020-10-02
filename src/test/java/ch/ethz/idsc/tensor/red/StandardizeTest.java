// code by jph
package ch.ethz.idsc.tensor.red;

import ch.ethz.idsc.tensor.ExactTensorQ;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.mat.HilbertMatrix;
import ch.ethz.idsc.tensor.sca.Chop;
import ch.ethz.idsc.tensor.usr.AssertFail;
import junit.framework.TestCase;

public class StandardizeTest extends TestCase {
  public void testNumeric() {
    Tensor tensor = Standardize.ofVector(Tensors.vector(6.5, 3.8, 6.6, 5.7, 6.0, 6.4, 5.3));
    Chop._12.requireAllZero(Mean.of(tensor));
    Chop._12.requireClose(Variance.ofVector(tensor), RealScalar.ONE);
    Chop._12.requireClose(StandardDeviation.ofVector(tensor), RealScalar.ONE);
  }

  public void testExact1() {
    Tensor tensor = Standardize.ofVector(Tensors.vector(1, 2, 3));
    ExactTensorQ.require(tensor);
    assertEquals(tensor, Tensors.vector(-1, 0, 1));
  }

  public void testExact2() {
    Tensor tensor = Standardize.ofVector(Tensors.vector(1, 3, 5));
    ExactTensorQ.require(tensor);
    assertEquals(tensor, Tensors.vector(-1, 0, 1));
  }

  public void testLengthShort() {
    AssertFail.of(() -> Standardize.ofVector(Tensors.empty()));
    AssertFail.of(() -> Standardize.ofVector(Tensors.vector(2)));
  }

  public void testScalarFail() {
    AssertFail.of(() -> Standardize.ofVector(RealScalar.of(84.312)));
  }

  public void testMatrixFail() {
    AssertFail.of(() -> Standardize.ofVector(HilbertMatrix.of(5)));
  }
}
