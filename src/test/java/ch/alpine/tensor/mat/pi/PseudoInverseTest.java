// code by jph
package ch.alpine.tensor.mat.pi;

import java.util.Arrays;
import java.util.Random;

import ch.alpine.tensor.ComplexScalar;
import ch.alpine.tensor.DecimalScalar;
import ch.alpine.tensor.ExactTensorQ;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.alg.Array;
import ch.alpine.tensor.alg.Dimensions;
import ch.alpine.tensor.alg.Dot;
import ch.alpine.tensor.alg.NestList;
import ch.alpine.tensor.alg.Transpose;
import ch.alpine.tensor.ext.Timing;
import ch.alpine.tensor.mat.HilbertMatrix;
import ch.alpine.tensor.mat.IdentityMatrix;
import ch.alpine.tensor.mat.LeftNullSpace;
import ch.alpine.tensor.mat.OrthogonalMatrixQ;
import ch.alpine.tensor.mat.Tolerance;
import ch.alpine.tensor.mat.re.Inverse;
import ch.alpine.tensor.mat.re.MatrixRank;
import ch.alpine.tensor.pdf.DiscreteUniformDistribution;
import ch.alpine.tensor.pdf.Distribution;
import ch.alpine.tensor.pdf.NormalDistribution;
import ch.alpine.tensor.pdf.RandomVariate;
import ch.alpine.tensor.pdf.UniformDistribution;
import ch.alpine.tensor.qty.Quantity;
import ch.alpine.tensor.red.Entrywise;
import ch.alpine.tensor.sca.Chop;
import ch.alpine.tensor.sca.N;
import ch.alpine.tensor.usr.AssertFail;
import junit.framework.TestCase;

public class PseudoInverseTest extends TestCase {
  public void testHilbertSquare() {
    for (int n = 1; n < 8; ++n) {
      Tensor matrix = HilbertMatrix.of(n);
      Tensor result = PseudoInverse.usingSvd(matrix);
      Tensor identy = IdentityMatrix.of(n);
      Chop._06.requireClose(result.dot(matrix), identy);
      Chop._06.requireClose(matrix.dot(result), identy);
    }
  }

  public void testMixedUnitsVantHoff() {
    // inspired by code by gjoel
    for (int deg = 0; deg <= 2; ++deg) {
      int degree = deg;
      Tensor x = Tensors.fromString("{100[K], 110.0[K], 130[K], 133[K]}").map(Scalar::reciprocal);
      assertEquals(x.Get(0).one(), RealScalar.ONE);
      Tensor matrix = x.map(s -> NestList.of(s::multiply, s.one(), degree));
      PseudoInverse.of(matrix);
    }
  }

  public void testMathematica() {
    Tensor expect = Tensors.fromString("{{252/73, -(198/73), -(240/73)}, {-(360/73), 408/73, 468/73}}");
    assertEquals(PseudoInverse.of(HilbertMatrix.of(3, 2)), expect);
    assertEquals(PseudoInverse.of(HilbertMatrix.of(2, 3)), Transpose.of(expect));
  }

  public void testHilbertRect() {
    for (int m = 2; m < 6; ++m) {
      int n = m + 2;
      Tensor matrix = HilbertMatrix.of(n, m);
      assertEquals(MatrixRank.of(matrix), m);
      ExactTensorQ.require(matrix);
      Tensor sol1 = PseudoInverse.of(matrix);
      ExactTensorQ.require(sol1);
      Tensor sol2 = PseudoInverse.usingQR(matrix);
      Chop._03.requireClose(sol1, sol2);
      Tensor sol3 = PseudoInverse.usingSvd(matrix);
      Chop._03.requireClose(sol1, sol3);
    }
  }

  public void testHilbert46() {
    for (int n = 1; n < 6; ++n) {
      Tensor matrix = HilbertMatrix.of(n, 6);
      Tensor result = PseudoInverse.usingSvd(matrix);
      assertEquals(Dimensions.of(result), Arrays.asList(6, n));
      Chop._10.requireClose(matrix.dot(result), IdentityMatrix.of(n));
    }
  }

  public void testMathematica1() {
    Tensor matrix = Tensors.fromString("{{1, 2, 3}, {4, 5, 6}, {7, 8, 9}, {10, 11, 12}}");
    assertEquals(MatrixRank.of(matrix), 2);
    Tensor res1 = PseudoInverse.usingSvd(matrix);
    Tensor res2 = PseudoInverse.of(matrix);
    Tensor expect = Tensors.fromString("{{-(29/60), -(11/45), -(1/180), 7/30}, {-(1/30), -(1/90), 1/90, 1/30}, {5/12, 2/9, 1/36, -(1/6)}}");
    assertEquals(Dimensions.of(res1), Arrays.asList(3, 4));
    Chop._12.requireClose(res1, expect);
    Chop._12.requireClose(res1, res2);
  }

  public void testMathematica2() {
    Tensor matrix = Transpose.of(Tensors.fromString("{{1, 2, 3}, {4, 5, 6}, {7, 8, 9}, {10, 11, 12}}"));
    Tensor result = PseudoInverse.usingSvd(matrix);
    Tensor actual = Transpose.of(Tensors.fromString("{{-(29/60), -(11/45), -(1/180), 7/30}, {-(1/30), -(1/90), 1/90, 1/30}, {5/12, 2/9, 1/36, -(1/6)}}"));
    Chop._12.requireClose(result.subtract(actual), Array.zeros(4, 3));
  }

  public void testQuantity() {
    Random random = new Random(3);
    Distribution distribution = NormalDistribution.of(Quantity.of(0, "m"), Quantity.of(1, "m"));
    for (int n = 1; n < 7; ++n) {
      Tensor matrix = RandomVariate.of(distribution, random, n, n);
      Tensor invers = Inverse.of(matrix);
      Chop._12.requireClose(invers, PseudoInverse.usingSvd(matrix)); // chop_09 was insufficient
      Chop._12.requireClose(invers, PseudoInverse.usingQR(matrix));
    }
  }

  public void testRectangular() {
    Distribution distribution = NormalDistribution.of(Quantity.of(0, "m"), Quantity.of(1, "m"));
    Timing tsvd = Timing.stopped();
    Timing t_qr = Timing.stopped();
    for (int m = 5; m < 12; ++m) {
      int n = m + 3;
      Tensor matrix = RandomVariate.of(distribution, n, m);
      tsvd.start();
      Tensor pinv = PseudoInverse.usingSvd(matrix);
      tsvd.stop();
      t_qr.start();
      Tensor piqr = PseudoInverse.usingQR(matrix);
      t_qr.stop();
      Chop._09.requireClose(pinv, piqr);
      Tolerance.CHOP.requireClose(Dot.of(piqr, matrix), IdentityMatrix.of(m));
    }
    // assertTrue(t_qr.nanoSeconds() < tsvd.nanoSeconds());
  }

  public void testCDecRhs() {
    Random random = new Random(1);
    Distribution distribution = DiscreteUniformDistribution.of(-10, 10);
    Tensor matrix = RandomVariate.of(distribution, random, 5, 2);
    assertEquals(MatrixRank.of(matrix), 2);
    Tensor rhs = RandomVariate.of(distribution, random, 5);
    Tensor sol0 = PseudoInverse.usingCholesky(matrix).dot(rhs);
    Tensor sol1 = LeastSquares.usingCholesky(matrix, rhs);
    assertEquals(sol0, sol1);
  }

  public void testCDecRhs2() {
    Random random = new Random(2);
    Distribution distribution = DiscreteUniformDistribution.of(-10, 10);
    Tensor matrix = RandomVariate.of(distribution, random, 2, 5);
    assertEquals(MatrixRank.of(matrix), 2);
    Tensor rhs = RandomVariate.of(distribution, random, 2);
    Tensor sol0 = PseudoInverse.usingCholesky(matrix).dot(rhs);
    Tensor sol1 = LeastSquares.usingCholesky(matrix, rhs);
    assertEquals(sol0, sol1);
  }

  public void testComplexRectangular() {
    Distribution distribution = NormalDistribution.standard(); // of(Quantity.of(0, "m"), Quantity.of(1, "m"));
    for (int m = 3; m < 9; ++m) {
      int n = m + 3;
      Tensor matrix = Entrywise.with(ComplexScalar::of).apply( //
          RandomVariate.of(distribution, n, m), //
          RandomVariate.of(distribution, n, m));
      Tensor piqr = PseudoInverse.usingQR(matrix);
      Tolerance.CHOP.requireClose(Dot.of(piqr, matrix), IdentityMatrix.of(m));
      Tensor pbic = PseudoInverse.of(matrix);
      Chop._08.requireClose(piqr, pbic);
    }
  }

  public void testComplexExact() {
    Tensor expect = Tensors.fromString("{{-(1/3) - I/3, 1/6 - I/6, 1/6 + I/6}, {1/6 - I/3, 5/12 + I/12, -(1/12) - I/12}}");
    Tensor matrix = Tensors.fromString("{{-1 + I, 0}, {-I, 2}, {2 - I, 2 * I}}");
    assertEquals(PseudoInverse.of(matrix), expect);
  }

  public void testDecimalScalar() {
    Tensor matrix = HilbertMatrix.of(3, 5).map(N.DECIMAL128);
    Tensor pseudo = PseudoInverse.of(matrix);
    assertTrue(pseudo.Get(1, 2) instanceof DecimalScalar);
  }

  public void testPseudoInverseIdempotent() {
    for (int m = 3; m < 6; ++m) {
      int n = m + 3;
      Tensor matrix = RandomVariate.of(NormalDistribution.standard(), n, m);
      Tensor sol = LeastSquares.usingQR(matrix, IdentityMatrix.of(n));
      assertEquals(Dimensions.of(sol), Arrays.asList(m, n));
      Tolerance.CHOP.requireClose(PseudoInverse.usingSvd(matrix), sol);
    }
  }

  public void testRankDeficient() {
    int r = 2;
    Distribution distribution = NormalDistribution.standard();
    Tensor m1 = RandomVariate.of(distribution, 7, r);
    Tensor m2 = RandomVariate.of(distribution, r, 4);
    Tensor br = RandomVariate.of(distribution, 7);
    LeastSquares.usingQR(m1, br);
    Tensor matrix = m1.dot(m2);
    assertEquals(MatrixRank.of(matrix), r);
    {
      AssertFail.of(() -> PseudoInverse.usingQR(matrix));
      AssertFail.of(() -> LeastSquares.usingQR(matrix, br));
      Tensor ls1 = LeastSquares.of(matrix, br);
      Tensor ls2 = PseudoInverse.of(matrix).dot(br);
      Tolerance.CHOP.requireClose(ls1, ls2);
    }
    {
      Tensor m = Transpose.of(matrix);
      Tensor b = RandomVariate.of(distribution, 4);
      AssertFail.of(() -> PseudoInverse.usingQR(m));
      AssertFail.of(() -> LeastSquares.usingQR(m, b));
      Tensor ls1 = LeastSquares.of(m, b);
      Tensor ls2 = PseudoInverse.of(m).dot(b);
      Tolerance.CHOP.requireClose(ls1, ls2);
    }
  }

  public void testSimple() {
    Tensor sequence = RandomVariate.of(NormalDistribution.standard(), 10, 3);
    Tensor point = RandomVariate.of(NormalDistribution.standard(), 3);
    Tensor matrix = Tensor.of(sequence.stream().map(point.negate()::add));
    Tensor nullsp = LeftNullSpace.of(matrix);
    OrthogonalMatrixQ.require(nullsp);
    Chop._08.requireClose(PseudoInverse.usingSvd(nullsp), Transpose.of(nullsp));
  }

  private static Tensor deprec(Tensor vector, Tensor nullsp) {
    return vector.dot(PseudoInverse.usingSvd(nullsp)).dot(nullsp);
  }

  public void testQR() {
    Distribution distribution = UniformDistribution.unit();
    for (int count = 0; count < 10; ++count) {
      Tensor vector = RandomVariate.of(distribution, 10);
      Tensor design = RandomVariate.of(distribution, 10, 3);
      Tensor nullsp = LeftNullSpace.usingQR(design);
      Tensor p1 = deprec(vector, nullsp);
      Tensor p2 = Dot.of(nullsp, vector, nullsp);
      Tolerance.CHOP.requireClose(p1, p2);
    }
  }

  public void testEmptyFail() {
    Tensor tensor = Tensors.empty();
    AssertFail.of(() -> PseudoInverse.of(tensor));
    AssertFail.of(() -> PseudoInverse.usingSvd(tensor));
  }

  public void testVectorFail() {
    Tensor tensor = Tensors.vector(1, 2, 3, 4);
    AssertFail.of(() -> PseudoInverse.of(tensor));
    AssertFail.of(() -> PseudoInverse.usingSvd(tensor));
  }

  public void testScalarFail() {
    AssertFail.of(() -> PseudoInverse.of(RealScalar.ONE));
    AssertFail.of(() -> PseudoInverse.usingSvd(RealScalar.ONE));
  }

  public void testEmptyMatrixFail() {
    Tensor tensor = Tensors.of(Tensors.empty());
    AssertFail.of(() -> PseudoInverse.of(tensor));
    AssertFail.of(() -> PseudoInverse.usingSvd(tensor));
  }
}
