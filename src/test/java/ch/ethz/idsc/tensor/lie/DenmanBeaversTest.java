// code by jph
package ch.ethz.idsc.tensor.lie;

import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.mat.IdentityMatrix;
import ch.ethz.idsc.tensor.pdf.Distribution;
import ch.ethz.idsc.tensor.pdf.NormalDistribution;
import ch.ethz.idsc.tensor.pdf.RandomVariate;
import ch.ethz.idsc.tensor.sca.Chop;
import junit.framework.TestCase;

public class DenmanBeaversTest extends TestCase {
  public void testSimple() {
    Distribution distribution = NormalDistribution.standard();
    for (int n = 2; n < 6; ++n) {
      Tensor _a = RandomVariate.of(distribution, n, n);
      Tensor a = MatrixPower.of(_a, 2);
      MatrixSqrt matrixSqrt = new DenmanBeavers(a);
      Chop._08.requireClose(a, MatrixPower.of(matrixSqrt.sqrt(), 2));
      Tensor e = matrixSqrt.sqrt().dot(matrixSqrt.sqrt_inverse());
      Chop._08.requireClose(e, IdentityMatrix.of(n));
      Chop._08.requireClose( //
          MatrixPower.of(matrixSqrt.sqrt(), -1), //
          matrixSqrt.sqrt_inverse());
    }
  }
}
