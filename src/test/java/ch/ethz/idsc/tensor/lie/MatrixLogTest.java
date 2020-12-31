// code by jph
package ch.ethz.idsc.tensor.lie;

import ch.ethz.idsc.tensor.DoubleScalar;
import ch.ethz.idsc.tensor.RationalScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.alg.ConstantArray;
import ch.ethz.idsc.tensor.alg.Transpose;
import ch.ethz.idsc.tensor.mat.Eigensystem;
import ch.ethz.idsc.tensor.mat.IdentityMatrix;
import ch.ethz.idsc.tensor.pdf.Distribution;
import ch.ethz.idsc.tensor.pdf.NormalDistribution;
import ch.ethz.idsc.tensor.pdf.RandomVariate;
import ch.ethz.idsc.tensor.sca.Chop;
import ch.ethz.idsc.tensor.sca.Power;
import ch.ethz.idsc.tensor.usr.AssertFail;
import junit.framework.TestCase;

public class MatrixLogTest extends TestCase {
  public void testSymmetric() {
    for (int n = 2; n < 8; ++n) {
      Distribution distribution = NormalDistribution.of(0, 0.4 / n);
      Tensor matrix = Symmetrize.of(IdentityMatrix.of(n).add(RandomVariate.of(distribution, n, n)));
      Tensor log = ofSymmetric(matrix);
      Tensor loq = MatrixLog.ofSymmetric(matrix);
      Tensor los = MatrixLog.of(matrix);
      Chop._08.requireClose(log, loq);
      Chop._08.requireClose(log, los);
      Tensor exp = MatrixExp.of(log);
      Tensor exs = MatrixExp.ofSymmetric(log);
      Chop._08.requireClose(matrix, exp);
      Chop._08.requireClose(matrix, exs);
    }
  }

  public void testExp() {
    for (int n = 2; n < 4; ++n) {
      Tensor x = RandomVariate.of(NormalDistribution.standard(), n, n);
      Tensor exp = MatrixExp.of(x);
      Tensor log = MatrixLog.of(exp);
      Tensor cmp = MatrixExp.of(log);
      Chop._04.requireClose(exp, cmp);
    }
  }

  public void testFail() {
    Distribution distribution = NormalDistribution.of(0, 2);
    Tensor matrix = RandomVariate.of(distribution, 4, 5);
    AssertFail.of(() -> MatrixLog.of(matrix));
  }

  public void testNaNFail() {
    Tensor matrix = ConstantArray.of(DoubleScalar.INDETERMINATE, 3, 3);
    AssertFail.of(() -> MatrixLog.series(matrix));
  }

  /** @param matrix symmetric
   * @return */
  /* package */ static Tensor ofSymmetric(Tensor matrix) {
    Eigensystem eigensystem = Eigensystem.ofSymmetric(matrix);
    Scalar exponent = RationalScalar.of(1, 2); // LONGTERM adapt exponent to matrix
    Tensor avec = eigensystem.vectors();
    Tensor m = Transpose.of(avec).dot(eigensystem.values().map(Power.function(exponent)).pmul(avec));
    return MatrixLog.series(m).multiply(exponent.reciprocal());
  }
}
