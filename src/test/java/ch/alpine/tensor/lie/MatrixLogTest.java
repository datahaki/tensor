// code by jph
package ch.alpine.tensor.lie;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Iterator;

import ch.alpine.tensor.DoubleScalar;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.alg.Array;
import ch.alpine.tensor.alg.ConstantArray;
import ch.alpine.tensor.alg.Transpose;
import ch.alpine.tensor.mat.IdentityMatrix;
import ch.alpine.tensor.pdf.Distribution;
import ch.alpine.tensor.pdf.NormalDistribution;
import ch.alpine.tensor.pdf.RandomVariate;
import ch.alpine.tensor.sca.Chop;
import ch.alpine.tensor.usr.AssertFail;
import junit.framework.TestCase;

public class MatrixLogTest extends TestCase {
  public void testIdentityMatrix() {
    for (int n = 1; n < 6; ++n) {
      Tensor matrix = IdentityMatrix.of(n);
      assertEquals(MatrixLog.of(matrix), Array.zeros(n, n));
      assertEquals(MatrixLog.ofSymmetric(matrix), Array.zeros(n, n));
    }
  }

  public void testSymmetric() {
    for (int n = 1; n < 8; ++n) {
      Distribution distribution = NormalDistribution.of(0, 0.1 / n);
      Tensor matrix = Symmetrize.of(IdentityMatrix.of(n).add(RandomVariate.of(distribution, n, n)));
      Tensor loq = MatrixLog.ofSymmetric(matrix);
      Tensor los = MatrixLog.of(matrix);
      Chop._08.requireClose(loq, los);
      Tensor exp = MatrixExp.of(loq);
      Tensor exs = MatrixExp.ofSymmetric(loq);
      Chop._08.requireClose(matrix, exp);
      Chop._08.requireClose(matrix, exs);
    }
  }

  public void testExp() {
    for (int n = 2; n < 7; ++n) {
      Tensor x = RandomVariate.of(NormalDistribution.standard(), n, n);
      Tensor exp = MatrixExp.of(x);
      Tensor log = MatrixLog.of(exp);
      Tensor cmp = MatrixExp.of(log);
      Chop._04.requireClose(exp, cmp);
    }
  }

  public void testDeque() {
    Deque<Integer> deque = new ArrayDeque<>();
    deque.add(3);
    deque.add(5);
    Iterator<Integer> iterator = deque.iterator();
    assertEquals(iterator.next(), (Integer) 3);
    assertEquals(iterator.next(), (Integer) 5);
  }

  public void testFail() {
    Distribution distribution = NormalDistribution.of(0, 2);
    Tensor matrix = RandomVariate.of(distribution, 4, 5);
    AssertFail.of(() -> MatrixLog.of(matrix));
  }

  public void test1x2Fail() {
    for (int d = 1; d < 4; ++d) {
      Tensor matrix = IdentityMatrix.of(d + 1).extract(0, d);
      assertEquals(matrix.length(), d);
      AssertFail.of(() -> MatrixLog.of(matrix));
    }
  }

  public void test3x2Fail() {
    Tensor matrix = Transpose.of(IdentityMatrix.of(3).extract(0, 2));
    assertEquals(matrix.length(), 3);
    AssertFail.of(() -> MatrixLog.of(matrix));
  }

  public void testSeries1p() {
    MatrixLog.series1p(Array.zeros(3, 3));
    MatrixLog.series1p(ConstantArray.of(RealScalar.ZERO, 3, 3));
    AssertFail.of(() -> MatrixLog.series1p(ConstantArray.of(RealScalar.ONE, 3, 3)));
    AssertFail.of(() -> MatrixLog.series1p(ConstantArray.of(RealScalar.of(1.0), 3, 3)));
  }

  public void test_of() {
    AssertFail.of(() -> MatrixLog.of(ConstantArray.of(DoubleScalar.of(1e20), 3, 3)));
    AssertFail.of(() -> MatrixLog.of(ConstantArray.of(DoubleScalar.of(1e100), 3, 3)));
    AssertFail.of(() -> MatrixLog.of(ConstantArray.of(DoubleScalar.of(1e200), 3, 3)));
    AssertFail.of(() -> MatrixLog.of(ConstantArray.of(DoubleScalar.POSITIVE_INFINITY, 3, 3)));
    AssertFail.of(() -> MatrixLog.of(ConstantArray.of(DoubleScalar.INDETERMINATE, 3, 3)));
  }
}
