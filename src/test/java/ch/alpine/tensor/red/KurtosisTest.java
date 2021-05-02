// code by jph
package ch.alpine.tensor.red;

import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Scalars;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.mat.HilbertMatrix;
import ch.alpine.tensor.usr.AssertFail;
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
