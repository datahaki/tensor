// code by jph
package ch.alpine.tensor.mat.ex;

import java.lang.reflect.Modifier;
import java.util.Random;

import ch.alpine.tensor.ComplexScalar;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.alg.Array;
import ch.alpine.tensor.alg.Transpose;
import ch.alpine.tensor.mat.DiagonalMatrix;
import ch.alpine.tensor.mat.IdentityMatrix;
import ch.alpine.tensor.mat.Tolerance;
import ch.alpine.tensor.pdf.Distribution;
import ch.alpine.tensor.pdf.RandomVariate;
import ch.alpine.tensor.pdf.c.NormalDistribution;
import ch.alpine.tensor.pdf.c.UniformDistribution;
import ch.alpine.tensor.red.Entrywise;
import ch.alpine.tensor.red.Trace;
import ch.alpine.tensor.sca.Chop;
import ch.alpine.tensor.usr.AssertFail;
import junit.framework.TestCase;

public class MatrixLog2Test extends TestCase {
  private static void _checkExpLog(Tensor matrix) {
    Tensor exp = MatrixExp.of(matrix);
    Tensor log = MatrixLog.of(exp);
    Tensor elg = MatrixExp.of(log);
    Chop._08.requireClose(elg, exp); // tests fail for 1e-12
    // Chop._10.close(log, matrix); // not generally true!
  }

  private static void _checkLogExp(Tensor matrix) {
    Tensor log = MatrixLog.of(matrix);
    Tensor exp = MatrixExp.of(log);
    Tolerance.CHOP.requireClose(exp, matrix);
  }

  public void testIdentity() {
    Tensor mlog = MatrixLog.of(IdentityMatrix.of(2));
    assertEquals(mlog, Array.zeros(2, 2));
  }

  public void testDiagonal() {
    Tensor mlog = MatrixLog.of(DiagonalMatrix.of(2, 3));
    assertEquals(mlog, DiagonalMatrix.of(Math.log(2), Math.log(3)));
  }

  public void testFull() {
    Tensor matrix = Tensors.fromString("{{4, 2}, {-1, 1}}");
    Tensor mlog = MatrixLog.of(matrix);
    Tensor mathematica = Tensors.fromString( //
        "{{1.5040773967762740734, 0.81093021621632876396}, {-0.40546510810816438198, 0.28768207245178092744}}");
    Tolerance.CHOP.requireClose(mlog, mathematica);
    Tolerance.CHOP.requireClose(matrix, MatrixExp.of(mlog));
  }

  public void testUpper() {
    Tensor matrix = Tensors.fromString("{{4, 2}, {0, 1}}");
    Tensor mlog = MatrixLog.of(matrix);
    Tensor mathematica = Tensors.fromString( //
        "{{1.3862943611198906188, 0.92419624074659374589}, {0, 0}}");
    Tolerance.CHOP.requireClose(mlog, mathematica);
    Tolerance.CHOP.requireClose(matrix, MatrixExp.of(mlog));
  }

  public void testLower() {
    Tensor matrix = Tensors.fromString("{{4, 0}, {2, 1}}");
    Tensor mlog = MatrixLog.of(matrix);
    Tensor mathematica = Transpose.of(Tensors.fromString( //
        "{{1.3862943611198906188, 0.92419624074659374589}, {0, 0}}"));
    Tolerance.CHOP.requireClose(mlog, mathematica);
    Tolerance.CHOP.requireClose(matrix, MatrixExp.of(mlog));
  }

  public void testTraceZero() {
    Random random = new Random(3);
    Distribution distribution = NormalDistribution.of(0, 2);
    for (int index = 0; index < 10; ++index) {
      Tensor alg = RandomVariate.of(distribution, random, 2, 2);
      alg.set(alg.Get(0, 0).negate(), 1, 1);
      assertEquals(Trace.of(alg), RealScalar.ZERO);
      _checkExpLog(alg);
      _checkLogExp(alg);
    }
  }

  public void testComplex() {
    Distribution distribution = NormalDistribution.standard();
    for (int index = 0; index < 10; ++index) {
      Tensor alg = Entrywise.with(ComplexScalar::of).apply( //
          RandomVariate.of(distribution, 2, 2), //
          RandomVariate.of(distribution, 2, 2));
      _checkExpLog(alg);
      _checkLogExp(alg);
    }
  }

  public void testComplexTraceZero() {
    Distribution distribution = NormalDistribution.standard();
    for (int index = 0; index < 10; ++index) {
      Tensor alg = Entrywise.with(ComplexScalar::of).apply( //
          RandomVariate.of(distribution, 2, 2), //
          RandomVariate.of(distribution, 2, 2));
      alg.set(alg.Get(0, 0).negate(), 1, 1);
      assertEquals(Trace.of(alg), RealScalar.ZERO);
      _checkExpLog(alg);
      _checkLogExp(alg);
    }
  }

  public void test2x2() {
    for (int count = 0; count < 10; ++count) {
      Tensor x = RandomVariate.of(UniformDistribution.of(-1, 1), 2, 2);
      Tensor exp = MatrixExp.of(x);
      Tensor log = MatrixLog._of(exp);
      Tensor cmp = MatrixLog2.of(exp);
      Chop._04.requireClose(log, cmp);
    }
  }

  public void testFail() {
    Distribution distribution = NormalDistribution.of(0, 2);
    Tensor matrix = RandomVariate.of(distribution, 2, 3);
    AssertFail.of(() -> MatrixLog.of(matrix));
  }

  public void testPackageVisibility() {
    assertFalse(Modifier.isPublic(MatrixLog2.class.getModifiers()));
  }
}
