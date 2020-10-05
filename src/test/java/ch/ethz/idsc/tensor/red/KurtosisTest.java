// code by jph
package ch.ethz.idsc.tensor.red;

import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Scalars;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.mat.HilbertMatrix;
import ch.ethz.idsc.tensor.usr.AssertFail;
import junit.framework.TestCase;

public class KurtosisTest extends TestCase {
  public void testMathematica() {
    Tensor tensor = Tensors.vector(10, 2, 3, 4, 1);
    Scalar result = Kurtosis.of(tensor);
    assertEquals(result, Scalars.fromString("697/250")); // confirmed in mathematica
  }

  public void testFailScalar() {
    AssertFail.of(() -> Kurtosis.of(RealScalar.ONE));
  }

  public void testFailMatrix() {
    AssertFail.of(() -> Kurtosis.of(HilbertMatrix.of(3)));
  }
}
