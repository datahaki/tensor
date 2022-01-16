// code by jph
package ch.alpine.tensor.mat;

import ch.alpine.tensor.RationalScalar;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.alg.Array;
import ch.alpine.tensor.alg.ConstantArray;
import ch.alpine.tensor.sca.Factorial;
import ch.alpine.tensor.sca.Power;
import ch.alpine.tensor.usr.AssertFail;
import junit.framework.TestCase;

public class PermanentTest extends TestCase {
  public void testSimple() {
    Scalar scalar = Permanent.of(Tensors.fromString("{{1, 2, 3}, {4, 5, 6}, {7, 8, 9}}"));
    assertEquals(scalar, RealScalar.of(450)); // confirmed with mathematica
  }

  public void testHilbert() {
    Scalar scalar = Permanent.of(HilbertMatrix.of(5));
    assertEquals(scalar, RationalScalar.of(32104903, 470400000)); // confirmed with mathematica
  }

  public void testMin() {
    for (int n = 1; n <= 4; ++n) {
      Tensor matrix = ConstantArray.of(RationalScalar.of(1, n), n, n);
      Scalar scalar = Permanent.of(matrix);
      assertEquals(scalar, Factorial.of(n).divide(Power.of(n, n)));
    }
  }

  public void testFailAd() {
    AssertFail.of(() -> Permanent.of(Tensors.empty()));
    AssertFail.of(() -> Permanent.of(Tensors.vector(1, 2, 3)));
    AssertFail.of(() -> Permanent.of(Array.zeros(3, 3, 3)));
    AssertFail.of(() -> Permanent.of(HilbertMatrix.of(2, 3)));
    AssertFail.of(() -> Permanent.of(HilbertMatrix.of(3, 2)));
  }
}
