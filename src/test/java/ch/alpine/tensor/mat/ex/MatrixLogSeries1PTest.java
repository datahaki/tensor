// code by jph
package ch.alpine.tensor.mat.ex;

import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.alg.Array;
import ch.alpine.tensor.alg.ConstantArray;
import ch.alpine.tensor.usr.AssertFail;
import junit.framework.TestCase;

public class MatrixLogSeries1PTest extends TestCase {
  public void testSeries1p() {
    MatrixLogSeries1P.FUNCTION.apply(Array.zeros(3, 3));
    MatrixLogSeries1P.FUNCTION.apply(ConstantArray.of(RealScalar.ZERO, 3, 3));
    AssertFail.of(() -> MatrixLogSeries1P.FUNCTION.apply(ConstantArray.of(RealScalar.ONE, 3, 3)));
    AssertFail.of(() -> MatrixLogSeries1P.FUNCTION.apply(ConstantArray.of(RealScalar.of(1.0), 3, 3)));
  }
}
