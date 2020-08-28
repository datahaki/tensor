// code by jph
package ch.ethz.idsc.tensor.lie;

import java.util.Arrays;

import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Scalars;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Dimensions;
import ch.ethz.idsc.tensor.alg.Transpose;
import ch.ethz.idsc.tensor.mat.DiagonalMatrix;
import ch.ethz.idsc.tensor.mat.OrthogonalMatrixQ;
import ch.ethz.idsc.tensor.mat.Tolerance;
import ch.ethz.idsc.tensor.mat.UnitaryMatrixQ;
import ch.ethz.idsc.tensor.opt.Pi;
import ch.ethz.idsc.tensor.pdf.Distribution;
import ch.ethz.idsc.tensor.pdf.NormalDistribution;
import ch.ethz.idsc.tensor.pdf.RandomVariate;
import ch.ethz.idsc.tensor.red.Diagonal;
import ch.ethz.idsc.tensor.red.VectorAngle;
import ch.ethz.idsc.tensor.sca.Chop;
import junit.framework.TestCase;

public class OrthogonalizeTest extends TestCase {
  private static void orthogonalMatrixQ_require(Tensor q) {
    Tensor id = q.dot(Transpose.of(q));
    Tensor diagonal = Diagonal.of(id);
    Tolerance.CHOP.requireClose(id, DiagonalMatrix.with(diagonal));
    for (Tensor d : diagonal)
      assertTrue(Tolerance.CHOP.close(d, RealScalar.ONE) || Tolerance.CHOP.close(d, RealScalar.ZERO));
  }

  private static void _check(Tensor matrix) {
    Tensor q = Orthogonalize.of(matrix);
    assertEquals(Dimensions.of(matrix), Dimensions.of(q));
    orthogonalMatrixQ_require(q);
  }

  public void testMatrix1X3() {
    Tensor matrix = Tensors.fromString("{{1, 0, 1}}");
    assertFalse(OrthogonalMatrixQ.of(matrix));
    _check(matrix);
  }

  public void testMatrix2X3() {
    Tensor matrix = Tensors.fromString("{{1, 0, 1}, {1, 1, 1}}");
    assertFalse(OrthogonalMatrixQ.of(matrix));
    _check(matrix);
  }

  public void testMatrix2X3b() {
    Tensor v0 = Tensors.fromString("{1, 0, 1}");
    Tensor v1 = Tensors.fromString("{0, 1, 0}");
    Tensor matrix = Tensors.of(v0, v1);
    assertFalse(OrthogonalMatrixQ.of(matrix));
    _check(matrix);
    Tensor q = Orthogonalize.of(matrix);
    Scalar angle1 = VectorAngle.of(q.get(0), v0).get();
    Scalar angle2 = VectorAngle.of(q.get(1), v1).get();
    Chop._07.requireAllZero(angle1);
    assertTrue(Scalars.isZero(angle2));
  }

  public void testRandom() {
    Distribution distribution = NormalDistribution.standard();
    for (int rows = 1; rows < 10; ++rows)
      for (int cols = 1; cols < 10; ++cols)
        _check(RandomVariate.of(distribution, rows, cols));
  }

  public void test2x1() {
    Tensor matrix = Tensors.fromString("{{-1.63342010908827}, {0.07817413797055835}}");
    Tensor tensor = Orthogonalize.of(matrix);
    Tolerance.CHOP.requireClose(tensor, Tensors.fromString("{{-1}, {0}}"));
  }

  public void testSpan() {
    Tensor v0 = Tensors.vector(1, 1, 1);
    Tensor matrix = Tensors.of(v0);
    _check(matrix);
    Tensor q = Orthogonalize.of(matrix);
    Scalar a1 = VectorAngle.of(q.get(0), v0).get();
    assertTrue(Scalars.isZero(a1));
  }

  public void testComplex() {
    Tensor matrix = Tensors.fromString("{{1, 0, 1+2*I}, {-3*I, 1, 1}}");
    Tensor orth = Orthogonalize.of(matrix);
    assertTrue(UnitaryMatrixQ.of(orth));
  }

  public void test3x4() {
    Tensor a = Transpose.of(Tensors.fromString("{{1, 2, 3}, {1, 0, 0}, {0, 1, 0}, {0, 0, 1}}"));
    _check(a);
    Tensor m = Orthogonalize.of(a);
    assertEquals(Dimensions.of(m), Dimensions.of(a));
    Tensor m_mt = m.dot(Transpose.of(m));
    Chop._12.requireClose(m_mt, DiagonalMatrix.of(1, 1, 1));
  }

  public void test4x3() {
    Tensor a = Tensors.fromString("{{1, 2, 3}, {1, 0, 0}, {0, 1, 0}, {0, 0, 1}}");
    assertEquals(Dimensions.of(a), Arrays.asList(4, 3));
    Tensor m = Orthogonalize.of(a);
    assertEquals(Dimensions.of(m), Dimensions.of(a));
    Tensor m_mt = m.dot(Transpose.of(m));
    Tensor mt_m = Transpose.of(m).dot(m);
    Chop._12.requireClose(m_mt, DiagonalMatrix.of(1, 1, 1, 0));
    Chop._12.requireClose(mt_m, DiagonalMatrix.of(1, 1, 1));
  }

  public void testFailScalar() {
    try {
      Orthogonalize.of(Pi.VALUE);
      fail();
    } catch (Exception exception) {
      // ---
    }
  }

  public void testFailVector() {
    try {
      Orthogonalize.of(Tensors.vector(1, 2, 3, 4));
      fail();
    } catch (Exception exception) {
      // ---
    }
  }

  public void testFailMatrix() {
    try {
      Tensor matrix = Transpose.of(Tensors.fromString("{{1, 0, 1}, {1, 1, 1}}"));
      Orthogonalize.of(matrix);
      // fail();
    } catch (Exception exception) {
      // ---
    }
  }

  public void testFailAd() {
    try {
      Orthogonalize.of(LieAlgebras.so3());
      fail();
    } catch (Exception exception) {
      // ---
    }
  }
}
