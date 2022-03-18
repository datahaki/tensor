// code by jph
package ch.alpine.tensor.mat.ex;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.DoubleScalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.alg.ConstantArray;
import ch.alpine.tensor.usr.AssertFail;

public class MatrixExpSeriesTest {
  @Test
  public void testNaNFail() {
    Tensor matrix = ConstantArray.of(DoubleScalar.INDETERMINATE, 3, 3);
    AssertFail.of(() -> MatrixExpSeries.FUNCTION.apply(matrix));
  }
}
