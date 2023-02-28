// code by jph
package ch.alpine.tensor.mat.ex;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Random;
import java.util.stream.Stream;

import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.RepetitionInfo;
import org.junit.jupiter.api.Test;

import ch.alpine.tensor.ComplexScalar;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Scalars;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.Throw;
import ch.alpine.tensor.alg.Array;
import ch.alpine.tensor.alg.ConstantArray;
import ch.alpine.tensor.chq.ExactScalarQ;
import ch.alpine.tensor.chq.ExactTensorQ;
import ch.alpine.tensor.io.MathematicaFormat;
import ch.alpine.tensor.lie.Symmetrize;
import ch.alpine.tensor.lie.TensorWedge;
import ch.alpine.tensor.mat.HermitianMatrixQ;
import ch.alpine.tensor.mat.IdentityMatrix;
import ch.alpine.tensor.mat.MatrixDotTranspose;
import ch.alpine.tensor.mat.OrthogonalMatrixQ;
import ch.alpine.tensor.mat.Tolerance;
import ch.alpine.tensor.mat.re.Inverse;
import ch.alpine.tensor.pdf.Distribution;
import ch.alpine.tensor.pdf.RandomVariate;
import ch.alpine.tensor.pdf.c.NormalDistribution;
import ch.alpine.tensor.pdf.c.TriangularDistribution;
import ch.alpine.tensor.red.Entrywise;
import ch.alpine.tensor.red.Trace;
import ch.alpine.tensor.sca.Chop;

class MatrixExpTest {
  @Test
  void testZeros() {
    Tensor zeros = Array.zeros(7, 7);
    Tensor eye = MatrixExp.of(zeros);
    assertEquals(eye, IdentityMatrix.of(7));
    ExactTensorQ.require(eye);
  }

  @Test
  void testExp() {
    Random random = new Random();
    double val = random.nextGaussian() * 0.1;
    double va2 = random.nextGaussian() * 0.1;
    double va3 = random.nextGaussian() * 0.1;
    double[][] mat = new double[][] { { 0, val, va2 }, { -val, 0, va3 }, { -va2, -va3, 0 } };
    Tensor bu = Tensors.matrixDouble(mat);
    Tensor ort = MatrixExp.of(bu);
    Tolerance.CHOP.requireClose(MatrixDotTranspose.of(ort, ort), IdentityMatrix.of(ort.length()));
    Tensor log = MatrixLog.of(ort);
    Tolerance.CHOP.requireClose(bu, log);
  }

  @Test
  void testExp1() {
    Tensor mat = Tensors.fromString("{{1}}");
    Tensor exp = MatrixExp.of(mat);
    Scalar exp1 = exp.Get(0, 0);
    assertFalse(ExactScalarQ.of(exp1));
    Chop._13.requireClose(exp1, RealScalar.of(Math.exp(1)));
    Tensor log = MatrixLog.of(exp);
    Chop._13.requireClose(log, mat);
  }

  @Test
  void testExp2() {
    int n = 10;
    Distribution distribution = NormalDistribution.standard();
    Tensor A = RandomVariate.of(distribution, n, n);
    Tensor S = TensorWedge.of(A);
    Tensor o = MatrixExp.of(S);
    OrthogonalMatrixQ.require(o);
  }

  @Test
  void testGoldenThompsonInequality() {
    Tensor a = Tensors.fromString("{{2, I}, {-I, 2}}");
    Tensor b = Tensors.fromString("{{2, 1-I}, {1+I, 2}}");
    assertTrue(HermitianMatrixQ.of(a));
    assertTrue(HermitianMatrixQ.of(b));
    Tensor tra = Trace.of(MatrixExp.of(a.add(b)));
    Tensor trb = Trace.of(MatrixExp.of(a).dot(MatrixExp.of(b)));
    Chop._08.requireClose(tra, RealScalar.of(168.49869602)); // mathematica
    Chop._08.requireClose(trb, RealScalar.of(191.43054831)); // mathematica
  }

  @Test
  void testExact() {
    Tensor mat = Tensors.matrixInt(new int[][] { { 0, 2, 3 }, { 0, 0, -1 }, { 0, 0, 0 } });
    Tensor result = MatrixExp.of(mat);
    ExactTensorQ.require(result);
    Tensor actual = Tensors.matrixInt(new int[][] { { 1, 2, 2 }, { 0, 1, -1 }, { 0, 0, 1 } });
    assertEquals(result, actual);
    assertEquals(result.toString(), actual.toString());
  }

  @Test
  void testChallenge() {
    // Cleve Moler and Charles van Loan p.8
    Tensor mat = Tensors.matrixInt(new int[][] { { -49, 24 }, { -64, 31 } });
    Tensor A = Tensors.matrixInt(new int[][] { { 1, 3 }, { 2, 4 } });
    Tensor diag = Inverse.of(A).dot(mat).dot(A);
    assertTrue(Scalars.isZero(diag.Get(0, 1)));
    assertTrue(Scalars.isZero(diag.Get(1, 0)));
    Tensor result = MatrixExp.of(mat);
    Tensor diaexp = MatrixExp.of(diag);
    Tensor altexp = A.dot(diaexp).dot(Inverse.of(A));
    Tolerance.CHOP.requireClose(altexp, result);
  }
  // public void testQuantity1() {
  // // Mathematica can't do this :-)
  // Scalar qs1 = Quantity.of(3, "m");
  // Tensor mat = Tensors.of( //
  // Tensors.of(RealScalar.ZERO, qs1), //
  // Tensors.vector(0, 0));
  // Tensor sol = MatrixExp.of(mat);
  // Chop.NONE.requireClose(sol, mat.add(IdentityMatrix.of(2)));
  // }
  //
  // public void testQuantity2() {
  // Scalar qs1 = Quantity.of(2, "m");
  // Scalar qs2 = Quantity.of(3, "s");
  // Scalar qs3 = Quantity.of(4, "m");
  // Scalar qs4 = Quantity.of(5, "s");
  // Tensor mat = Tensors.of( //
  // Tensors.of(RealScalar.ZERO, qs1, qs3.multiply(qs4)), //
  // Tensors.of(RealScalar.ZERO, RealScalar.ZERO, qs2), //
  // Tensors.of(RealScalar.ZERO, RealScalar.ZERO, RealScalar.ZERO) //
  // );
  // Tensor actual = IdentityMatrix.of(3).add(mat).add(mat.dot(mat).multiply(RationalScalar.of(1, 2)));
  // // assertEquals(MatrixExp.of(mat), actual);
  // Chop.NONE.requireClose(MatrixExp.of(mat), actual);
  // }

  @Test
  void testLarge() {
    // without scaling, the loop of the series requires ~300 steps
    // with scaling, the loop requires only ~20 steps
    Tensor tensor = MatrixExp.of(Tensors.fromString("{{100, 100}, {100, 100}}"));
    Tensor result = MathematicaFormat.parse(Stream.of("{{3.6129868840627414*^86, 3.6129868840627414*^86}, {3.6129868840627414*^86, 3.6129868840627414*^86}}"));
    Chop.below(1e75).requireClose(tensor, result);
  }

  @RepeatedTest(5)
  void testNoScale() {
    Distribution distribution = NormalDistribution.of(0, 6);
    Tensor matrix = RandomVariate.of(distribution, 2, 2);
    Tensor exp1 = MatrixExp.of(matrix);
    Tensor exp2 = MatrixExpSeries.FUNCTION.apply(matrix);
    Chop._01.requireClose(exp1, exp2);
  }

  @RepeatedTest(5)
  void testNoScaleComplex() {
    Distribution distribution = NormalDistribution.of(0, 5);
    Tensor matrix = Entrywise.with(ComplexScalar::of).apply( //
        RandomVariate.of(distribution, 2, 2), //
        RandomVariate.of(distribution, 2, 2));
    Tensor exp1 = MatrixExp.of(matrix);
    Tensor exp2 = MatrixExpSeries.FUNCTION.apply(matrix);
    Chop._01.requireClose(exp1, exp2);
  }

  @Test
  void testComplex1() {
    Tensor matrix = ConstantArray.of(Scalars.fromString("-10-1*I"), 2, 2);
    Tensor tensor1 = MatrixExp.of(matrix); // 19
    Tensor tensor2 = MatrixExpSeries.FUNCTION.apply(matrix); // 94
    Chop._06.requireClose(tensor1, tensor2);
  }

  @Test
  void testComplex2() {
    Tensor matrix = ConstantArray.of(Scalars.fromString("-10.0-1.0*I"), 3, 3);
    Tensor tensor1 = MatrixExp.of(matrix); // 19
    Tensor tensor2 = MatrixExpSeries.FUNCTION.apply(matrix); // 119
    Chop._04.requireClose(tensor1, tensor2);
  }

  @RepeatedTest(5)
  void testHermitian(RepetitionInfo repetitionInfo) {
    Distribution distribution = TriangularDistribution.with(0, 1);
    int n = repetitionInfo.getCurrentRepetition();
    Tensor real = Symmetrize.of(RandomVariate.of(distribution, n, n));
    Tensor imag = TensorWedge.of(RandomVariate.of(distribution, n, n));
    Tensor matrix = Entrywise.with(ComplexScalar::of).apply(real, imag);
    HermitianMatrixQ.require(matrix);
    Tensor exp1 = MatrixExp.of(matrix);
    Tensor exp2 = MatrixExp.ofHermitian(matrix);
    Tolerance.CHOP.requireClose(exp1, exp2);
    Tensor log = MatrixLog.ofHermitian(exp2);
    Tolerance.CHOP.requireClose(exp1, MatrixExp.of(log));
  }

  @Test
  void testSparse() {
    int n = 3;
    assertEquals(MatrixExp.of(IdentityMatrix.sparse(n)), MatrixExp.of(IdentityMatrix.of(n)));
  }

  @Test
  void testFail() {
    assertThrows(IllegalArgumentException.class, () -> MatrixExp.of(Array.zeros(4, 3)));
    assertThrows(IllegalArgumentException.class, () -> MatrixExp.of(Array.zeros(3, 4)));
  }

  @Test
  void testScalarFail() {
    assertThrows(Throw.class, () -> MatrixExp.of(RealScalar.ONE));
  }

  @Test
  void testEmptyFail() {
    assertThrows(Throw.class, () -> MatrixExp.of(Tensors.empty()));
  }
}
