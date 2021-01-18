// code by jph
package ch.ethz.idsc.tensor.lie;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Iterator;

import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.alg.Array;
import ch.ethz.idsc.tensor.alg.Transpose;
import ch.ethz.idsc.tensor.mat.IdentityMatrix;
import ch.ethz.idsc.tensor.pdf.Distribution;
import ch.ethz.idsc.tensor.pdf.NormalDistribution;
import ch.ethz.idsc.tensor.pdf.RandomVariate;
import ch.ethz.idsc.tensor.sca.Chop;
import ch.ethz.idsc.tensor.usr.AssertFail;
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
    Tensor matrix = IdentityMatrix.of(2).extract(0, 1);
    assertEquals(matrix.length(), 1);
    AssertFail.of(() -> MatrixLog.of(matrix));
  }

  public void test2x3Fail() {
    Tensor matrix = IdentityMatrix.of(3).extract(0, 2);
    assertEquals(matrix.length(), 2);
    AssertFail.of(() -> MatrixLog.of(matrix));
  }

  public void test3x4Fail() {
    Tensor matrix = IdentityMatrix.of(4).extract(0, 3);
    assertEquals(matrix.length(), 3);
    AssertFail.of(() -> MatrixLog.of(matrix));
  }

  public void test3x2Fail() {
    Tensor matrix = Transpose.of(IdentityMatrix.of(3).extract(0, 2));
    assertEquals(matrix.length(), 3);
    AssertFail.of(() -> MatrixLog.of(matrix));
  }
}
