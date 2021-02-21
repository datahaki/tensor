// code by jph
package ch.ethz.idsc.tensor.mat;

import java.util.Arrays;

import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Scalars;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Dimensions;
import ch.ethz.idsc.tensor.alg.MatrixDotTranspose;
import ch.ethz.idsc.tensor.alg.Transpose;
import ch.ethz.idsc.tensor.lie.LeviCivitaTensor;
import ch.ethz.idsc.tensor.num.Pi;
import ch.ethz.idsc.tensor.pdf.Distribution;
import ch.ethz.idsc.tensor.pdf.NormalDistribution;
import ch.ethz.idsc.tensor.pdf.RandomVariate;
import ch.ethz.idsc.tensor.red.Diagonal;
import ch.ethz.idsc.tensor.red.VectorAngle;
import ch.ethz.idsc.tensor.sca.Chop;
import ch.ethz.idsc.tensor.sca.Imag;
import ch.ethz.idsc.tensor.usr.AssertFail;
import junit.framework.TestCase;

public class OrthogonalizeTest extends TestCase {
  private static void orthogonalMatrixQ_require(Tensor q) {
    Tensor id = q.dot(Transpose.of(q));
    Tensor diagonal = Diagonal.of(id);
    Tolerance.CHOP.requireClose(id, DiagonalMatrix.with(diagonal));
    for (Tensor d : diagonal)
      assertTrue(Tolerance.CHOP.isClose(d, RealScalar.ONE) || Tolerance.CHOP.isClose(d, RealScalar.ZERO));
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

  public void testZeroPad() {
    Tensor v1 = Tensors.vector(1, 2, 3);
    Tensor v0 = v1.map(Scalar::zero);
    Tensor matrix = Tensors.of(v1, v0, v0);
    Tensor q1 = Orthogonalize.of(matrix);
    Tolerance.CHOP.requireClose(Det.of(q1), RealScalar.ONE);
    Tensor q2 = Orthogonalize.usingSvd(matrix);
    Tolerance.CHOP.requireClose(Det.of(q2), RealScalar.ONE);
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
    Tensor q1 = Orthogonalize.of(matrix);
    Scalar angle1 = VectorAngle.of(q1.get(0), v0).get();
    Scalar angle2 = VectorAngle.of(q1.get(1), v1).get();
    Chop._07.requireAllZero(angle1);
    assertTrue(Scalars.isZero(angle2));
    Tensor q2 = Orthogonalize.usingSvd(matrix);
    Tensor q3 = Orthogonalize.usingPD(matrix);
    Tolerance.CHOP.requireClose(q1, q2);
    Tolerance.CHOP.requireClose(q2, q3);
  }

  public void testMatrix2X3bDeficient() {
    Tensor v0 = Tensors.fromString("{1, 0, 1}");
    Tensor v1 = Tensors.fromString("{0, 0, 0}");
    Tensor matrix = Tensors.of(v0, v1);
    Tensor q1 = Orthogonalize.of(matrix);
    Tensor q2 = Orthogonalize.usingSvd(matrix);
    Tolerance.CHOP.requireClose(q1, q2);
    AssertFail.of(() -> Orthogonalize.usingPD(matrix));
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
    Tensor q1 = Orthogonalize.of(matrix);
    Scalar a1 = VectorAngle.of(q1.get(0), v0).get();
    assertTrue(Scalars.isZero(a1));
    Tensor q2 = Orthogonalize.usingSvd(matrix);
    Tensor q3 = Orthogonalize.usingPD(matrix);
    Tolerance.CHOP.requireClose(q1, q2);
    Tolerance.CHOP.requireClose(q2, q3);
  }

  public void testComplex() {
    Tensor matrix = Tensors.fromString("{{1, 0, 1+2*I}, {-3*I, 1, 1}}");
    Tensor q1 = Orthogonalize.of(matrix);
    // System.out.println(q1);
    assertTrue(UnitaryMatrixQ.of(q1));
    // Tensor q2 = Orthogonalize.usingSvd(matrix);
    Tensor q3 = Orthogonalize.usingPD(matrix);
    assertTrue(UnitaryMatrixQ.of(q3));
    // System.out.println(q3);
    // Tolerance.CHOP.requireClose(q1, q2);
    // Tolerance.CHOP.requireClose(q1, q3);
  }

  public void test3x4() {
    Tensor a = Transpose.of(Tensors.fromString("{{1, 2, 3}, {1, 0, 0}, {0, 1, 0}, {0, 0, 1}}"));
    _check(a);
    Tensor m = Orthogonalize.of(a);
    assertEquals(Dimensions.of(m), Dimensions.of(a));
    Tensor m_mt = MatrixDotTranspose.of(m, m);
    Tolerance.CHOP.requireClose(m_mt, DiagonalMatrix.of(1, 1, 1));
  }

  public void test4x3() {
    Tensor a = Tensors.fromString("{{1, 2, 3}, {1, 0, 0}, {0, 1, 0}, {0, 0, 1}}");
    assertEquals(Dimensions.of(a), Arrays.asList(4, 3));
    Tensor m = Orthogonalize.of(a);
    assertEquals(Dimensions.of(m), Dimensions.of(a));
    Tensor m_mt = MatrixDotTranspose.of(m, m);
    Tensor mt_m = Transpose.of(m).dot(m);
    Tolerance.CHOP.requireClose(m_mt, DiagonalMatrix.of(1, 1, 1, 0));
    Tolerance.CHOP.requireClose(mt_m, DiagonalMatrix.of(1, 1, 1));
  }

  public void testInvariantPD() {
    Tensor matrix = RandomVariate.of(NormalDistribution.standard(), 3, 5);
    Tensor s1 = Orthogonalize.usingSvd(matrix);
    Tensor r1 = Orthogonalize.usingPD(matrix);
    Tolerance.CHOP.requireClose(r1, s1);
    OrthogonalMatrixQ.require(r1);
    Chop.NONE.requireAllZero(Imag.of(r1));
    Tensor s2 = Orthogonalize.usingSvd(s1);
    Tensor r2 = Orthogonalize.usingPD(r1);
    Tolerance.CHOP.requireClose(r1, r2);
    Tolerance.CHOP.requireClose(s1, s2);
  }

  public void testUsingSvd() {
    Tensor matrix = RandomVariate.of(NormalDistribution.standard(), 4, 3);
    AssertFail.of(() -> Orthogonalize.usingSvd(matrix));
  }

  public void testFailScalar() {
    AssertFail.of(() -> Orthogonalize.of(Pi.VALUE));
  }

  public void testFailVector() {
    AssertFail.of(() -> Orthogonalize.of(Tensors.vector(1, 2, 3, 4)));
  }

  public void testFailMatrix() {
    Tensor matrix = Transpose.of(Tensors.fromString("{{1, 0, 1}, {1, 1, 1}}"));
    Tensor result = Orthogonalize.of(matrix);
    Tensor expect = Tensors.fromString( // consistent with Mathematica
        "{{0.7071067811865475, 0.7071067811865477}, {-0.7071067811865477, 0.7071067811865475}, {0, 0}}");
    Tolerance.CHOP.requireClose(result, expect);
  }

  public void testFailAd() {
    AssertFail.of(() -> Orthogonalize.of(LeviCivitaTensor.of(3)));
  }
}
