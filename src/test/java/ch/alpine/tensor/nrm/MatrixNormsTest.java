// code by jph
package ch.alpine.tensor.nrm;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.alg.Array;
import ch.alpine.tensor.api.TensorScalarFunction;
import ch.alpine.tensor.lie.LeviCivitaTensor;
import ch.alpine.tensor.usr.AssertFail;

public class MatrixNormsTest {
  public static final TensorScalarFunction[] VALUES = { //
      Matrix1Norm::of, //
      Matrix2Norm::of, //
      MatrixInfinityNorm::of, //
  };

  @Test
  public void testZero() {
    for (TensorScalarFunction norm : VALUES) {
      assertEquals(norm.apply(Array.zeros(1, 1)), RealScalar.ZERO);
      assertEquals(norm.apply(Array.zeros(5, 3)), RealScalar.ZERO);
    }
  }

  @Test
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
