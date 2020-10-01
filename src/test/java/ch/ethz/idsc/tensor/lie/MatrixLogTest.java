// code by jph
package ch.ethz.idsc.tensor.lie;

import ch.ethz.idsc.tensor.DoubleScalar;
import ch.ethz.idsc.tensor.RationalScalar;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.TensorRuntimeException;
import ch.ethz.idsc.tensor.alg.ConstantArray;
import ch.ethz.idsc.tensor.alg.Transpose;
import ch.ethz.idsc.tensor.mat.Eigensystem;
import ch.ethz.idsc.tensor.mat.IdentityMatrix;
import ch.ethz.idsc.tensor.pdf.Distribution;
import ch.ethz.idsc.tensor.pdf.NormalDistribution;
import ch.ethz.idsc.tensor.pdf.RandomVariate;
import ch.ethz.idsc.tensor.sca.Chop;
import ch.ethz.idsc.tensor.sca.Power;
import junit.framework.TestCase;

public class MatrixLogTest extends TestCase {
  // TODO
  // public void testSeries() {
  // for (int n = 2; n < 10; ++n) {
  // Distribution distribution = NormalDistribution.of(0, 0.2);
  // Tensor matrix = IdentityMatrix.of(n).add(RandomVariate.of(distribution, n, n));
  // Tensor log = MatrixLog.series(matrix);
  // Tensor exp = MatrixExp.of(log);
  // Chop._08.requireClose(matrix, exp);
  // }
  // }
  public void testSymmetric() {
    for (int n = 2; n < 10; ++n) {
      Distribution distribution = NormalDistribution.of(0, 0.4 / n);
      Tensor matrix = Symmetrize.of(IdentityMatrix.of(n).add(RandomVariate.of(distribution, n, n)));
      Tensor log = ofSymmetric(matrix);
      Tensor loq = MatrixLog.ofSymmetric(matrix);
      Chop._08.requireClose(log, loq);
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

  public void testNaNFail() {
    Tensor matrix = ConstantArray.of(DoubleScalar.INDETERMINATE, 3, 3);
    try {
      series(matrix);
      fail();
    } catch (Exception exception) {
      // ---
    }
  }

  private static final int MAX_ITERATIONS = 500;

  /** @param matrix symmetric
   * @return */
  /* package */ static Tensor ofSymmetric(Tensor matrix) {
    Eigensystem eigensystem = Eigensystem.ofSymmetric(matrix);
    Scalar exponent = RationalScalar.of(1, 2); // TODO adapt exponent to matrix
    Tensor avec = eigensystem.vectors();
    Tensor m = Transpose.of(avec).dot(eigensystem.values().map(Power.function(exponent)).pmul(avec));
    return series(m).multiply(exponent.reciprocal());
  }

  /** @param matrix square
   * @return
   * @throws Exception if given matrix is non-square */
  /* package */ static Tensor series(Tensor matrix) {
    Tensor x = matrix.subtract(IdentityMatrix.of(matrix.length()));
    Tensor nxt = x;
    Tensor sum = nxt;
    for (int k = 2; k < MAX_ITERATIONS; ++k) {
      nxt = nxt.dot(x);
      Tensor prv = sum;
      sum = sum.add(nxt.divide(RealScalar.of(k % 2 == 0 ? -k : k)));
      if (Chop.NONE.isClose(sum, prv))
        return sum;
    }
    throw TensorRuntimeException.of(matrix); // insufficient convergence
  }
}
