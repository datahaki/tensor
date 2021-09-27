// code by jph
package ch.alpine.tensor.lie;

import ch.alpine.tensor.DoubleScalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.alg.ConstantArray;
import ch.alpine.tensor.usr.AssertFail;
import junit.framework.TestCase;

public class MatrixExpSeriesTest extends TestCase {
  public void testNaNFail() {
    Tensor matrix = ConstantArray.of(DoubleScalar.INDETERMINATE, 3, 3);
    AssertFail.of(() -> MatrixExpSeries.FUNCTION.apply(matrix));
  }
}
