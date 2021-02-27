// code by jph
package ch.ethz.idsc.tensor.nrm;

import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.api.TensorScalarFunction;
import ch.ethz.idsc.tensor.api.TensorUnaryOperator;
import ch.ethz.idsc.tensor.usr.AssertFail;
import junit.framework.TestCase;

public class VectorNormsTest extends TestCase {
  public static final TensorScalarFunction[] VALUES = { //
      Vector1Norm::of, //
      Vector2Norm::of, //
      VectorInfinityNorm::of, //
  };

  public void testEmptyFail() {
    for (TensorScalarFunction norm : VALUES) {
      AssertFail.of(() -> norm.apply(Tensors.empty()));
      AssertFail.of(() -> norm.apply(RealScalar.ONE));
      AssertFail.of(() -> norm.apply(Tensors.fromString("{{1, 2}, {3}}")));
    }
  }

  public void testOk1() {
    Tensor v = Tensors.vector(0, 0, 0, 0);
    for (TensorScalarFunction norm : VALUES)
      assertEquals(v, NormalizeUnlessZero.with(norm).apply(v));
  }

  public void testEmpty() {
    for (TensorScalarFunction norm : VALUES) {
      TensorUnaryOperator tensorUnaryOperator = NormalizeUnlessZero.with(norm);
      Tensor tensor = Tensors.empty();
      AssertFail.of(() -> tensorUnaryOperator.apply(tensor));
    }
  }
}
