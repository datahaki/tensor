// code by jph
package ch.ethz.idsc.tensor.lie;

import java.util.Random;
import java.util.stream.Stream;

import ch.ethz.idsc.tensor.ComplexScalar;
import ch.ethz.idsc.tensor.DoubleScalar;
import ch.ethz.idsc.tensor.ExactScalarQ;
import ch.ethz.idsc.tensor.ExactTensorQ;
import ch.ethz.idsc.tensor.RationalScalar;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Scalars;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Array;
import ch.ethz.idsc.tensor.alg.ConstantArray;
import ch.ethz.idsc.tensor.alg.Transpose;
import ch.ethz.idsc.tensor.io.MathematicaFormat;
import ch.ethz.idsc.tensor.mat.HermitianMatrixQ;
import ch.ethz.idsc.tensor.mat.IdentityMatrix;
import ch.ethz.idsc.tensor.mat.Inverse;
import ch.ethz.idsc.tensor.pdf.Distribution;
import ch.ethz.idsc.tensor.pdf.NormalDistribution;
import ch.ethz.idsc.tensor.pdf.RandomVariate;
import ch.ethz.idsc.tensor.qty.Quantity;
import ch.ethz.idsc.tensor.red.Trace;
import ch.ethz.idsc.tensor.sca.Chop;
import ch.ethz.idsc.tensor.usr.AssertFail;
import junit.framework.TestCase;

public class MatrixExpTest extends TestCase {
  private static final Random RANDOM = new Random();

  public void testZeros() {
    Tensor zeros = Array.zeros(7, 7);
    Tensor eye = MatrixExp.of(zeros);
    assertEquals(eye, IdentityMatrix.of(7));
    ExactTensorQ.require(eye);
  }

  public void testExp() {
    double val = RANDOM.nextGaussian();
    double va2 = RANDOM.nextGaussian();
    double va3 = RANDOM.nextGaussian();
    double[][] mat = new double[][] { { 0, val, va2 }, { -val, 0, va3 }, { -va2, -va3, 0 } };
    Tensor bu = Tensors.matrixDouble(mat);
    Tensor o = MatrixExp.of(bu);
    Chop._12.requireAllZero(o.dot(Transpose.of(o)).subtract(IdentityMatrix.of(o.length())));
  }

  public void testExp1() {
    Scalar exp1 = MatrixExp.of(Tensors.fromString("{{1}}")).Get(0, 0);
    assertFalse(ExactScalarQ.of(exp1));
    Chop._13.requireClose(exp1, RealScalar.of(Math.exp(1)));
  }

  public void testExp2() {
    int n = 10;
    Tensor A = Tensors.matrix((i, j) -> DoubleScalar.of(RANDOM.nextGaussian()), n, n);
    Tensor S = TensorWedge.of(A);
    Tensor o = MatrixExp.of(S);
    Chop._10.requireAllZero(o.dot(Transpose.of(o)).subtract(IdentityMatrix.of(o.length())));
  }

  public void testGoldenThompsonInequality() {
    Tensor a = Tensors.fromString("{{2, I}, {-I, 2}}");
    Tensor b = Tensors.fromString("{{2, 1-I}, {1+I, 2}}");
    assertTrue(HermitianMatrixQ.of(a));
    assertTrue(HermitianMatrixQ.of(b));
    Scalar tra = Trace.of(MatrixExp.of(a.add(b)));
    Scalar trb = Trace.of(MatrixExp.of(a).dot(MatrixExp.of(b)));
    Chop._05.requireClose(tra, RealScalar.of(168.49869602)); // mathematica
    Chop._05.requireClose(trb, RealScalar.of(191.43054831)); // mathematica
  }

  public void testExact() {
    Tensor mat = Tensors.matrixInt(new int[][] { { 0, 2, 3 }, { 0, 0, -1 }, { 0, 0, 0 } });
    Tensor result = MatrixExp.of(mat);
    ExactTensorQ.require(result);
    Tensor actual = Tensors.matrixInt(new int[][] { { 1, 2, 2 }, { 0, 1, -1 }, { 0, 0, 1 } });
    assertEquals(result, actual);
    assertEquals(result.toString(), actual.toString());
  }

  public void testChallenge() {
    // Cleve Moler and Charles van Loan p.8
    Tensor mat = Tensors.matrixInt(new int[][] { { -49, 24 }, { -64, 31 } });
    Tensor A = Tensors.matrixInt(new int[][] { { 1, 3 }, { 2, 4 } });
    Tensor diag = Inverse.of(A).dot(mat).dot(A);
    assertTrue(Scalars.isZero(diag.Get(0, 1)));
    assertTrue(Scalars.isZero(diag.Get(1, 0)));
    Tensor result = MatrixExp.of(mat);
    Tensor diaexp = MatrixExp.of(diag);
    Tensor altexp = A.dot(diaexp).dot(Inverse.of(A));
    Chop._08.requireClose(altexp, result);
  }

  public void testQuantity1() {
    // Mathematica can't do this :-)
    Scalar qs1 = Quantity.of(3, "m");
    Tensor mat = Tensors.of( //
        Tensors.of(RealScalar.ZERO, qs1), //
        Tensors.vector(0, 0));
    Tensor sol = MatrixExp.of(mat);
    Chop.NONE.requireClose(sol, mat.add(IdentityMatrix.of(2)));
  }

  public void testQuantity2() {
    Scalar qs1 = Quantity.of(2, "m");
    Scalar qs2 = Quantity.of(3, "s");
    Scalar qs3 = Quantity.of(4, "m");
    Scalar qs4 = Quantity.of(5, "s");
    Tensor mat = Tensors.of( //
        Tensors.of(RealScalar.ZERO, qs1, qs3.multiply(qs4)), //
        Tensors.of(RealScalar.ZERO, RealScalar.ZERO, qs2), //
        Tensors.of(RealScalar.ZERO, RealScalar.ZERO, RealScalar.ZERO) //
    );
    Tensor actual = IdentityMatrix.of(3).add(mat).add(mat.dot(mat).multiply(RationalScalar.of(1, 2)));
    // assertEquals(MatrixExp.of(mat), actual);
    Chop.NONE.requireClose(MatrixExp.of(mat), actual);
  }

  public void testLarge() {
    // without scaling, the loop of the series requires ~300 steps
    // with scaling, the loop requires only ~20 steps
    Tensor tensor = MatrixExp.of(Tensors.fromString("{{100, 100}, {100, 100}}"));
    Tensor result = MathematicaFormat.parse(Stream.of("{{3.6129868840627414*^86, 3.6129868840627414*^86}, {3.6129868840627414*^86, 3.6129868840627414*^86}}"));
    Chop.below(1e75).requireClose(tensor, result);
  }

  public void testNoScale() {
    Distribution distribution = NormalDistribution.of(0, 6);
    for (int count = 0; count < 10; ++count) {
      Tensor matrix = RandomVariate.of(distribution, 2, 2);
      Tensor exp1 = MatrixExp.of(matrix);
      Tensor exp2 = MatrixExp.series(matrix);
      Chop._01.requireClose(exp1, exp2);
    }
  }

  public void testNoScaleComplex() {
    Distribution distribution = NormalDistribution.of(0, 5);
    for (int count = 0; count < 10; ++count) {
      Tensor mr = RandomVariate.of(distribution, 2, 2);
      Tensor mi = RandomVariate.of(distribution, 2, 2);
      Tensor matrix = mi.multiply(ComplexScalar.I).add(mr);
      Tensor exp1 = MatrixExp.of(matrix);
      Tensor exp2 = MatrixExp.series(matrix);
      Chop._01.requireClose(exp1, exp2);
    }
  }

  public void testComplex1() {
    Tensor matrix = ConstantArray.of(Scalars.fromString("-10-1*I"), 3, 3);
    Tensor tensor1 = MatrixExp.of(matrix);
    Tensor tensor2 = MatrixExp.series(matrix);
    Chop._03.requireClose(tensor1, tensor2);
  }

  public void testComplex2() {
    Tensor matrix = ConstantArray.of(Scalars.fromString("-10.0-1.0*I"), 3, 3);
    Tensor tensor1 = MatrixExp.of(matrix);
    Tensor tensor2 = MatrixExp.series(matrix);
    Chop._03.requireClose(tensor1, tensor2);
  }

  public void testNaNFail() {
    Tensor matrix = ConstantArray.of(DoubleScalar.INDETERMINATE, 3, 3);
    AssertFail.of(() -> MatrixExp.series(matrix));
  }

  public void testFail() {
    AssertFail.of(() -> MatrixExp.of(Array.zeros(4, 3)));
    AssertFail.of(() -> MatrixExp.of(Array.zeros(3, 4)));
  }

  public void testScalarFail() {
    AssertFail.of(() -> MatrixExp.of(RealScalar.ONE));
  }

  public void testEmptyFail() {
    AssertFail.of(() -> MatrixExp.of(Tensors.empty()));
  }
}
