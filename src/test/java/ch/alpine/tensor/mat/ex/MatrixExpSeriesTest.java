// code by jph
package ch.alpine.tensor.mat.ex;

import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.DoubleScalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.TensorRuntimeException;
import ch.alpine.tensor.alg.ConstantArray;

public class MatrixExpSeriesTest {
  @Test
  public void testNaNFail() {
    Tensor matrix = ConstantArray.of(DoubleScalar.INDETERMINATE, 3, 3);
    assertThrows(TensorRuntimeException.class, () -> MatrixExpSeries.FUNCTION.apply(matrix));
  }
}
