// code by jph
package ch.ethz.idsc.tensor.nrm;

import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.UnitVector;
import ch.ethz.idsc.tensor.mat.HilbertMatrix;
import ch.ethz.idsc.tensor.num.Pi;
import ch.ethz.idsc.tensor.usr.AssertFail;
import junit.framework.TestCase;

public class NormalizeTotalTest extends TestCase {
  public void testSimple() {
    Tensor tensor = NormalizeTotal.FUNCTION.apply(Tensors.vector(2, -3, 4, 5));
    assertEquals(tensor, Tensors.fromString("{1/4, -3/8, 1/2, 5/8}"));
  }

  public void testUnitVector() {
    Tensor tensor = NormalizeTotal.FUNCTION.apply(Tensors.vector(2, 0.0, 4, 5).map(Scalar::reciprocal));
    assertEquals(tensor, UnitVector.of(4, 1));
  }

  public void testEmpty() {
    AssertFail.of(() -> NormalizeTotal.FUNCTION.apply(Tensors.empty()));
  }

  public void testZeroFail() {
    AssertFail.of(() -> NormalizeTotal.FUNCTION.apply(Tensors.vector(2, -2, 1, -1)));
  }

  public void testZeroNumericFail() {
    AssertFail.of(() -> NormalizeTotal.FUNCTION.apply(Tensors.vectorDouble(2, -2, 1, -1)));
  }

  public void testFailScalar() {
    AssertFail.of(() -> NormalizeTotal.FUNCTION.apply(Pi.TWO));
  }

  public void testFailMatrix() {
    AssertFail.of(() -> NormalizeTotal.FUNCTION.apply(HilbertMatrix.of(3)));
  }
}
