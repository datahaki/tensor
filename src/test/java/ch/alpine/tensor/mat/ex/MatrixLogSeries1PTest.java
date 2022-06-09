// code by jph
package ch.alpine.tensor.mat.ex;

import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.TensorRuntimeException;
import ch.alpine.tensor.alg.Array;
import ch.alpine.tensor.alg.ConstantArray;

class MatrixLogSeries1PTest {
  @Test
  public void testSeries1p() {
    MatrixLogSeries1P.FUNCTION.apply(Array.zeros(3, 3));
    MatrixLogSeries1P.FUNCTION.apply(ConstantArray.of(RealScalar.ZERO, 3, 3));
    assertThrows(TensorRuntimeException.class, () -> MatrixLogSeries1P.FUNCTION.apply(ConstantArray.of(RealScalar.ONE, 3, 3)));
    assertThrows(TensorRuntimeException.class, () -> MatrixLogSeries1P.FUNCTION.apply(ConstantArray.of(RealScalar.of(1.0), 3, 3)));
  }
}
