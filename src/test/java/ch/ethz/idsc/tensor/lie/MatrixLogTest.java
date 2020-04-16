// code by jph
package ch.ethz.idsc.tensor.lie;

import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.mat.IdentityMatrix;
import ch.ethz.idsc.tensor.pdf.Distribution;
import ch.ethz.idsc.tensor.pdf.NormalDistribution;
import ch.ethz.idsc.tensor.pdf.RandomVariate;
import ch.ethz.idsc.tensor.sca.Chop;
import junit.framework.TestCase;

public class MatrixLogTest extends TestCase {
  public void testSeries() {
    for (int n = 2; n < 10; ++n) {
      Distribution distribution = NormalDistribution.of(0, 0.2);
      Tensor matrix = IdentityMatrix.of(n).add(RandomVariate.of(distribution, n, n));
      Tensor log = MatrixLog.series(matrix);
      Tensor exp = MatrixExp.of(log);
      Chop._08.requireClose(matrix, exp);
    }
  }

  public void testSymmetric() {
    for (int n = 2; n < 10; ++n) {
      Distribution distribution = NormalDistribution.of(0, 0.2);
      Tensor matrix = Symmetrize.of(IdentityMatrix.of(n).add(RandomVariate.of(distribution, n, n)));
      Tensor log = MatrixLog.ofSymmetric(matrix);
      Tensor exp = MatrixExp.of(log);
      Chop._08.requireClose(matrix, exp);
    }
  }

  public void testFail() {
    Distribution distribution = NormalDistribution.of(0, 2);
    Tensor matrix = RandomVariate.of(distribution, 4, 5);
    try {
      MatrixLog.of(matrix);
      fail();
    } catch (Exception exception) {
      // ---
    }
  }
}
