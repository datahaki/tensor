// code by jph
package ch.ethz.idsc.tensor.nrm;

import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Array;
import ch.ethz.idsc.tensor.api.TensorScalarFunction;
import ch.ethz.idsc.tensor.lie.LeviCivitaTensor;
import ch.ethz.idsc.tensor.usr.AssertFail;
import junit.framework.TestCase;

public class MatrixNormsTest extends TestCase {
  public static final TensorScalarFunction[] VALUES = { //
      Matrix1Norm::of, //
      Matrix2Norm::of, //
      MatrixInfinityNorm::of, //
  };

  public void testZero() {
    for (TensorScalarFunction norm : VALUES) {
      assertEquals(norm.apply(Array.zeros(1, 1)), RealScalar.ZERO);
      assertEquals(norm.apply(Array.zeros(5, 3)), RealScalar.ZERO);
    }
  }

  public void testFails() {
    for (TensorScalarFunction norm : VALUES) {
      AssertFail.of(() -> norm.apply(RealScalar.ONE));
      AssertFail.of(() -> norm.apply(Tensors.empty()));
      AssertFail.of(() -> norm.apply(Tensors.vector(1, 2, 3)));
      AssertFail.of(() -> norm.apply(LeviCivitaTensor.of(3)));
      AssertFail.of(() -> norm.apply(Tensors.fromString("{{1, 2}, {3}}")));
    }
  }
}
