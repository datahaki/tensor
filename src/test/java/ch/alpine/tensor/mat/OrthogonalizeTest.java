// code by jph
package ch.alpine.tensor.mat;

import java.util.Arrays;

import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Scalars;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.alg.Dimensions;
import ch.alpine.tensor.alg.Transpose;
import ch.alpine.tensor.lie.LeviCivitaTensor;
import ch.alpine.tensor.lie.TensorWedge;
import ch.alpine.tensor.lie.r2.AngleVector;
import ch.alpine.tensor.mat.ex.MatrixExp;
import ch.alpine.tensor.mat.re.Det;
import ch.alpine.tensor.num.Pi;
import ch.alpine.tensor.pdf.Distribution;
import ch.alpine.tensor.pdf.RandomVariate;
import ch.alpine.tensor.pdf.c.NormalDistribution;
import ch.alpine.tensor.pdf.c.UniformDistribution;
import ch.alpine.tensor.red.Diagonal;
import ch.alpine.tensor.red.VectorAngle;
import ch.alpine.tensor.sca.ArcTan;
import ch.alpine.tensor.sca.Chop;
import ch.alpine.tensor.sca.Conjugate;
import ch.alpine.tensor.sca.Imag;
import ch.alpine.tensor.sca.Sign;
import ch.alpine.tensor.usr.AssertFail;
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
    for (int rows = 1; rows < 7; ++rows)
      for (int cols = 1; cols < 7; ++cols)
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
    Tensor mmt = MatrixDotTranspose.of(matrix, Conjugate.of(matrix));
    HermitianMatrixQ.require(mmt);
    // System.out.println(Pretty.of(mmt));
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

  private static Scalar ArcTan2D_of(Tensor vector) {
    return ArcTan.of(vector.Get(0), vector.Get(1));
  }

  public void testAngles2D() {
    Scalar i0 = RealScalar.of(1.2);
    Scalar i1 = i0.add(RealScalar.of(0.2));
    Tensor d0 = AngleVector.of(i0);
    Tensor d1 = AngleVector.of(i1);
    Tensor vectors = Tensors.of(d0, d1);
    Sign.requirePositive(Det.of(vectors));
    Tolerance.CHOP.requireClose(ArcTan2D_of(d0), i0);
    Tolerance.CHOP.requireClose(ArcTan2D_of(d1), i1);
    // ---
    {
      Tensor tensor = Orthogonalize.usingSvd(vectors);
      Tolerance.CHOP.requireClose(Det.of(tensor), RealScalar.ONE);
      Scalar a0 = ArcTan2D_of(tensor.get(0));
      Scalar a1 = ArcTan2D_of(tensor.get(1));
      Tolerance.CHOP.requireClose(i0.subtract(a0), a1.subtract(i1));
    }
    {
      Tensor tensor = Orthogonalize.usingPD(vectors);
      Tolerance.CHOP.requireClose(Det.of(tensor), RealScalar.ONE);
      Scalar a0 = ArcTan2D_of(tensor.get(0));
      Scalar a1 = ArcTan2D_of(tensor.get(1));
      Tolerance.CHOP.requireClose(i0.subtract(a0), a1.subtract(i1));
    }
  }

  public void testAngles3D() {
    Scalar i0 = RealScalar.of(-1.5);
    Scalar i1 = i0.add(RealScalar.of(0.2));
    Tensor d0 = AngleVector.of(i0).append(RealScalar.ZERO);
    Tensor d1 = AngleVector.of(i1).append(RealScalar.ZERO);
    Tensor vectors = Tensors.of(d0, d1);
    Tolerance.CHOP.requireClose(ArcTan2D_of(d0), i0);
    Tolerance.CHOP.requireClose(ArcTan2D_of(d1), i1);
    // ---
    {
      Tensor tensor = Orthogonalize.usingSvd(vectors);
      Scalar a0 = ArcTan2D_of(tensor.get(0));
      Scalar a1 = ArcTan2D_of(tensor.get(1));
      Tolerance.CHOP.requireClose(i0.subtract(a0), a1.subtract(i1));
    }
    {
      Tensor tensor = Orthogonalize.usingPD(vectors);
      Scalar a0 = ArcTan2D_of(tensor.get(0));
      Scalar a1 = ArcTan2D_of(tensor.get(1));
      Tolerance.CHOP.requireClose(i0.subtract(a0), a1.subtract(i1));
    }
  }

  public void testMatrixExp() {
    for (int d = 2; d < 5; ++d) {
      Tensor matrix = MatrixExp.of(TensorWedge.of(RandomVariate.of(UniformDistribution.unit(), d, d)));
      OrthogonalMatrixQ.require(matrix);
      Tolerance.CHOP.requireClose(matrix, Orthogonalize.of(matrix));
      Tolerance.CHOP.requireClose(matrix, Orthogonalize.usingSvd(matrix));
      Tolerance.CHOP.requireClose(matrix, Orthogonalize.usingPD(matrix));
      Tolerance.CHOP.requireClose(matrix, Orthogonalize.unprotected(matrix));
    }
  }

  public void testDiag() {
    Tensor matrix = DiagonalMatrix.of(2, -2);
    Tensor rdetp1 = DiagonalMatrix.of(1, +1);
    Tensor rdetn1 = DiagonalMatrix.of(1, -1);
    assertEquals(rdetn1, Orthogonalize.of(matrix));
    assertEquals(rdetp1, Orthogonalize.usingSvd(matrix));
    assertEquals(rdetn1, Orthogonalize.usingPD(matrix));
    assertEquals(rdetp1, Orthogonalize.unprotected(matrix));
  }

  public void testTransposeUsingSvd() {
    Tensor matrix = RandomVariate.of(NormalDistribution.standard(), 4, 3);
    Tensor result = Orthogonalize.unprotected(matrix);
    OrthogonalMatrixQ.require(Transpose.of(result));
    assertEquals(Dimensions.of(result), Dimensions.of(matrix));
  }

  public void testUsingSvdFail() {
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
