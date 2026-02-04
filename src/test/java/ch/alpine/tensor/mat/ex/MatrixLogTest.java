// code by jph
package ch.alpine.tensor.mat.ex;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Iterator;

import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.RepetitionInfo;
import org.junit.jupiter.api.Test;

import ch.alpine.tensor.DoubleScalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Throw;
import ch.alpine.tensor.alg.Array;
import ch.alpine.tensor.alg.ConstantArray;
import ch.alpine.tensor.alg.Transpose;
import ch.alpine.tensor.lie.Symmetrize;
import ch.alpine.tensor.mat.IdentityMatrix;
import ch.alpine.tensor.pdf.Distribution;
import ch.alpine.tensor.pdf.RandomVariate;
import ch.alpine.tensor.pdf.c.NormalDistribution;
import ch.alpine.tensor.sca.Chop;

class MatrixLogTest {
  @Test
  void testIdentityMatrix() {
    for (int n = 1; n < 6; ++n) {
      Tensor matrix = IdentityMatrix.of(n);
      assertEquals(MatrixLog.of(matrix), Array.zeros(n, n));
      assertEquals(MatrixLog.ofSymmetric(matrix), Array.zeros(n, n));
    }
  }

  @Test
  void testSymmetric() {
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

  @RepeatedTest(6)
  void testExp(RepetitionInfo repetitionInfo) {
    int n = repetitionInfo.getCurrentRepetition();
    Tensor x = RandomVariate.of(NormalDistribution.standard(), n, n);
    Tensor exp = MatrixExp.of(x);
    Tensor log = MatrixLog.of(exp);
    Tensor cmp = MatrixExp.of(log);
    Chop._04.requireClose(exp, cmp);
  }

  @Test
  void testFail2() {
    int n = 5;
    Tensor x = RandomVariate.of(NormalDistribution.standard(), n, n);
    Tensor exp = MatrixExp.of(x);
    Tensor log = MatrixLog.of(exp);
    Tensor cmp = MatrixExp.of(log);
    Chop._04.requireClose(exp, cmp);
    MatrixLog.MatrixLog_MAX_EXPONENT.set(1);
    assertThrows(Exception.class, () -> MatrixLog.of(exp));
    MatrixLog.MatrixLog_MAX_EXPONENT.remove();
  }

  @Test
  void testDeque() {
    Deque<Integer> deque = new ArrayDeque<>();
    deque.add(3);
    deque.add(5);
    Iterator<Integer> iterator = deque.iterator();
    assertEquals(iterator.next(), (Integer) 3);
    assertEquals(iterator.next(), (Integer) 5);
  }

  @Test
  void testFail() {
    Distribution distribution = NormalDistribution.of(0, 2);
    Tensor matrix = RandomVariate.of(distribution, 4, 5);
    assertThrows(IllegalArgumentException.class, () -> MatrixLog.of(matrix));
  }

  @Test
  void test1x2Fail() {
    for (int d = 1; d < 3; ++d) {
      Tensor matrix = IdentityMatrix.of(d + 1).extract(0, d);
      assertEquals(matrix.length(), d);
      assertThrows(Throw.class, () -> MatrixLog.of(matrix));
    }
    for (int d = 3; d < 4; ++d) {
      Tensor matrix = IdentityMatrix.of(d + 1).extract(0, d);
      assertEquals(matrix.length(), d);
      assertThrows(IllegalArgumentException.class, () -> MatrixLog.of(matrix));
    }
  }

  @Test
  void test3x2Fail() {
    Tensor matrix = Transpose.of(IdentityMatrix.of(3).extract(0, 2));
    assertEquals(matrix.length(), 3);
    assertThrows(IllegalArgumentException.class, () -> MatrixLog.of(matrix));
  }

  @Test
  void test_of() {
    assertThrows(Throw.class, () -> MatrixLog.of(ConstantArray.of(DoubleScalar.of(1e20), 3, 3)));
    assertThrows(Throw.class, () -> MatrixLog.of(ConstantArray.of(DoubleScalar.of(1e100), 3, 3)));
    assertThrows(Throw.class, () -> MatrixLog.of(ConstantArray.of(DoubleScalar.of(1e200), 3, 3)));
    assertThrows(Throw.class, () -> MatrixLog.of(ConstantArray.of(DoubleScalar.POSITIVE_INFINITY, 3, 3)));
    assertThrows(Throw.class, () -> MatrixLog.of(ConstantArray.of(DoubleScalar.INDETERMINATE, 3, 3)));
  }
}
