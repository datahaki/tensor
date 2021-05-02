// code by jph
package ch.alpine.tensor.lie;

import java.util.Objects;

import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.mat.IdentityMatrix;
import ch.alpine.tensor.pdf.Distribution;
import ch.alpine.tensor.pdf.NormalDistribution;
import ch.alpine.tensor.pdf.RandomVariate;
import ch.alpine.tensor.sca.Chop;
import junit.framework.TestCase;

public class DenmanBeaversTest extends TestCase {
  public void testSimple() {
    Distribution distribution = NormalDistribution.standard();
    int fails = 0;
    for (int n = 2; n < 6; ++n) {
      Tensor _a = RandomVariate.of(distribution, n, n);
      Tensor a = MatrixPower.of(_a, 2);
      MatrixSqrt matrixSqrt = null;
      try {
        matrixSqrt = new DenmanBeavers(a);
      } catch (Exception exception) {
        ++fails;
      }
      if (Objects.nonNull(matrixSqrt)) {
        Chop._08.requireClose(a, MatrixPower.of(matrixSqrt.sqrt(), 2));
        Tensor e = matrixSqrt.sqrt().dot(matrixSqrt.sqrt_inverse());
        Chop._08.requireClose(e, IdentityMatrix.of(n));
        Chop._08.requireClose( //
            MatrixPower.of(matrixSqrt.sqrt(), -1), //
            matrixSqrt.sqrt_inverse());
      }
    }
    assertTrue(fails < 2);
  }
}
