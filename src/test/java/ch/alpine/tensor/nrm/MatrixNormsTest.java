// code by jph
package ch.alpine.tensor.nrm;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.Throw;
import ch.alpine.tensor.alg.Array;
import ch.alpine.tensor.api.TensorScalarFunction;
import ch.alpine.tensor.lie.LeviCivitaTensor;

class MatrixNormsTest {
  public static final TensorScalarFunction[] VALUES = { //
      Matrix1Norm::of, //
      Matrix2Norm::of, //
      MatrixInfinityNorm::of, //
  };

  @Test
  void testZero() {
    for (TensorScalarFunction norm : VALUES) {
      assertEquals(norm.apply(Array.zeros(1, 1)), RealScalar.ZERO);
      assertEquals(norm.apply(Array.zeros(5, 3)), RealScalar.ZERO);
    }
  }

  @Test
  void testFails() {
    for (TensorScalarFunction norm : VALUES) {
      assertThrows(Throw.class, () -> norm.apply(RealScalar.ONE));
      assertThrows(Exception.class, () -> norm.apply(Tensors.empty()));
      assertThrows(Throw.class, () -> norm.apply(Tensors.vector(1, 2, 3)));
      assertThrows(Exception.class, () -> norm.apply(LeviCivitaTensor.of(3)));
      assertThrows(Exception.class, () -> norm.apply(Tensors.fromString("{{1, 2}, {3}}")));
    }
  }
}
