// code by jph
package ch.ethz.idsc.tensor.mat;

import java.util.Arrays;

import ch.ethz.idsc.tensor.ComplexScalar;
import ch.ethz.idsc.tensor.ExactTensorQ;
import ch.ethz.idsc.tensor.RationalScalar;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Array;
import ch.ethz.idsc.tensor.alg.Dimensions;
import ch.ethz.idsc.tensor.alg.UnitVector;
import ch.ethz.idsc.tensor.pdf.Distribution;
import ch.ethz.idsc.tensor.pdf.NormalDistribution;
import ch.ethz.idsc.tensor.pdf.RandomVariate;
import ch.ethz.idsc.tensor.pdf.UniformDistribution;
import ch.ethz.idsc.tensor.red.Entrywise;
import ch.ethz.idsc.tensor.sca.Chop;
import ch.ethz.idsc.tensor.sca.N;
import junit.framework.TestCase;

public class LeastSquaresTest extends TestCase {
  public void testSquareExact() {
    Tensor matrix = HilbertMatrix.of(4, 4);
    Tensor pinv = PseudoInverse.of(matrix);
    assertEquals(pinv, Inverse.of(matrix));
    ExactTensorQ.require(pinv);
  }

  public void testEasy() {
    Tensor m = Tensors.matrix( //
        (i, j) -> i.equals(j) ? RationalScalar.of(1, 1) : RealScalar.ZERO, 4, 3);
    assertEquals(MatrixRank.of(m), 3);
    Tensor b = Tensors.vector(1, 1, 1, 1);
    Tensor x0 = LeastSquares.of(m, b);
    ExactTensorQ.require(x0);
    Tensor x1 = LeastSquares.usingCholesky(m, b);
    ExactTensorQ.require(x1);
    assertEquals(x1, Tensors.vector(1, 1, 1));
    Tensor x2 = LeastSquares.usingQR(m, b);
    Tensor x3 = LeastSquares.usingSvd(m, b);
    Tolerance.CHOP.requireClose(x0, x1);
    Tolerance.CHOP.requireClose(x1, x3);
    Tolerance.CHOP.requireClose(x2, x3);
  }

  public void testMathematica() {
    Tensor matrix = HilbertMatrix.of(2, 3);
    assertEquals(matrix, Tensors.fromString("{{1, 1/2, 1/3}, {1/2, 1/3, 1/4}}"));
    Tensor expect = Tensors.fromString("{{252/73, -(360/73)}, {-(198/73), 408/73}, {-(240/73), 468/73}}");
    Tensor cholesky2 = LeastSquares.usingCholesky(matrix, IdentityMatrix.of(2));
    assertEquals(cholesky2, expect);
  }

  public void testMathematicaB() {
    Tensor matrix = HilbertMatrix.of(2, 3);
    assertEquals(matrix, Tensors.fromString("{{1, 1/2, 1/3}, {1/2, 1/3, 1/4}}"));
    Tensor expect = Tensors.fromString( //
        "{{-(2376/73), 1476/73, -(72/73), 72/73}, {2868/73, -(1410/73), 432/73, 444/73}, {3264/73, -(1656/73), 444/73, 432/73}}");
    Tensor b = Tensors.fromString("{{2, 3, 4, 6}, {8, -2, 3, 4}}");
    Tensor cholesky2 = LeastSquares.usingCholesky(matrix, b);
    assertEquals(cholesky2, expect);
  }

  public void testEasyNumeric() {
    Tensor m = N.DOUBLE.of(Tensors.matrix( //
        (i, j) -> i.equals(j) ? RationalScalar.of(1, 1) : RealScalar.ZERO, 4, 3));
    assertEquals(MatrixRank.of(m), 3);
    Tensor b = Tensors.vector(1, 1, 1, 1);
    Tensor x0 = LeastSquares.of(m, b);
    Tensor x1 = LeastSquares.usingCholesky(m, b);
    assertEquals(x1, Tensors.vector(1, 1, 1));
    Tensor x2 = LeastSquares.usingQR(m, b);
    Tensor x3 = LeastSquares.usingSvd(m, b);
    Tolerance.CHOP.requireClose(x0, x1);
    Tolerance.CHOP.requireClose(x1, x2);
    Tolerance.CHOP.requireClose(x3, x2);
  }

  public void testFullRank() {
    Tensor m = Tensors.matrix( //
        (i, j) -> RationalScalar.of(2 * i + 2 + j, 1 + 9 * i + j), 4, 3);
    Tensor b = Tensors.vector(1, 1, 1, 1);
    Tensor x1 = LeastSquares.usingCholesky(m, b);
    Tensor x2 = LeastSquares.usingSvd(m, b);
    Tensor x3 = LeastSquares.usingQR(m, b);
    assertEquals(Dimensions.of(x1), Arrays.asList(3));
    Chop._10.requireClose(x1, x2);
    Chop._10.requireClose(x3, x2);
  }

  public void testLowRank() {
    Tensor m = Tensors.matrix( //
        (i, j) -> RationalScalar.of(2 * i + j, 9 + j), 4, 3);
    assertEquals(MatrixRank.of(m), 2);
    Tensor b = Tensors.vector(1, 1, 1, 1);
    Tensor x2 = LeastSquares.usingSvd(m, b);
    assertEquals(Chop._12.of(m.dot(x2).subtract(b)), b.multiply(RealScalar.ZERO));
  }

  public void testFullRankComplex() {
    Tensor m = Tensors.matrix( //
        (i, j) -> ComplexScalar.of( //
            RealScalar.of(i), RationalScalar.of(2 * i + 2 + j, 1 + 9 * i + j)),
        4, 3);
    assertEquals(MatrixRank.of(m), 3);
    Tensor b = Tensors.vector(1, 1, 1, 1);
    Tensor x1 = LeastSquares.usingCholesky(m, b);
    Tensor d1 = m.dot(x1).subtract(b);
    Tensor s1 = Tensors.fromString(
        "{726086997/5793115330933+44069346/5793115330933*I, 67991764500/5793115330933+4521379500/5793115330933*I,-22367102670/827587904419-1672185060/827587904419*I, 11662258824/827587904419+1019978082/827587904419*I}");
    assertEquals(d1, s1);
  }

  public void testRankDeficient() {
    assertEquals(LeastSquares.of(Array.zeros(3, 3), UnitVector.of(3, 1)), Array.zeros(3));
    assertEquals(LeastSquares.of(Array.zeros(3, 2), UnitVector.of(3, 1)), Array.zeros(2));
    assertEquals(LeastSquares.of(DiagonalMatrix.of(0, 1, 0), UnitVector.of(3, 0)), Array.zeros(3));
  }

  public void testFullRankComplex2() {
    Tensor m = Tensors.matrix( //
        (i, j) -> ComplexScalar.of( //
            RealScalar.of(18 * i + j * 100), RationalScalar.of(2 * i + 2 + j, 1 + 9 * i + j)),
        4, 3);
    Tensor b = Tensors.fromString("{2+3*I, 1-8*I, 99-100*I, 2/5}");
    Tensor x1 = LeastSquares.usingCholesky(m, b);
    @SuppressWarnings("unused")
    Tensor d1 = m.dot(x1).subtract(b);
    // 112.45647858810342
    // 112.45619362133209
    // 114.01661102428717
    // 114.00823633112584
    // 81.36251932398088
    // 81.3565431234251
    // System.out.println(Norm._2.of(d1));
    // System.out.println(d1);
    // Tensor s1 = Tensors.fromString(
    // "[726086997/5793115330933+44069346/5793115330933*I, 67991764500/5793115330933+4521379500/5793115330933*I,
    // -22367102670/827587904419-1672185060/827587904419*I, 11662258824/827587904419+1019978082/827587904419*I]");
    // assertEquals(d1, s1);
  }

  public void testRect() {
    Distribution distribution = UniformDistribution.unit();
    Tensor matrix = RandomVariate.of(distribution, 10, 3);
    Tensor b = RandomVariate.of(distribution, 10, 2);
    Tensor x = LeastSquares.usingSvd(matrix, b);
    assertEquals(Dimensions.of(x), Arrays.asList(3, 2));
    assertEquals(Dimensions.of(matrix.dot(x)), Dimensions.of(b));
  }

  public void testLeastSquaresReal() {
    for (int n = 3; n < 6; ++n) {
      Tensor matrix = RandomVariate.of(NormalDistribution.standard(), n, n);
      Tensor b = RandomVariate.of(NormalDistribution.standard(), n, 2);
      Chop._09.requireClose( //
          LinearSolve.of(matrix, b), //
          LeastSquares.usingQR(matrix, b));
    }
  }

  public void testLeastSquaresComplexSquare() {
    for (int n = 3; n < 6; ++n) {
      Tensor matrix = Entrywise.with(ComplexScalar::of).apply( //
          RandomVariate.of(NormalDistribution.standard(), n, n), //
          RandomVariate.of(NormalDistribution.standard(), n, n));
      Tensor b = RandomVariate.of(NormalDistribution.standard(), n, 3);
      Tensor sol = LinearSolve.of(matrix, b);
      Tolerance.CHOP.requireClose(sol, LeastSquares.of(matrix, b));
      Tolerance.CHOP.requireClose(sol, LeastSquares.usingQR(matrix, b));
    }
  }

  public void testLeastSquaresRect() {
    for (int m = 3; m < 6; ++m) {
      int n = m + 3;
      Tensor matrix = RandomVariate.of(NormalDistribution.standard(), n, m);
      {
        Tensor b = RandomVariate.of(NormalDistribution.standard(), n, 2);
        Tolerance.CHOP.requireClose( //
            LeastSquares.usingSvd(matrix, b), //
            LeastSquares.usingQR(matrix, b));
      }
      {
        Tensor b = RandomVariate.of(NormalDistribution.standard(), n);
        Tolerance.CHOP.requireClose( //
            LeastSquares.usingSvd(matrix, b), //
            LeastSquares.usingQR(matrix, b));
      }
    }
  }

  public void testLeastSquaresSmallBig() {
    for (int n = 3; n < 6; ++n) {
      int m = n + 3;
      Tensor matrix = RandomVariate.of(NormalDistribution.standard(), n, m);
      Tensor b = RandomVariate.of(NormalDistribution.standard(), n, 2);
      LeastSquares.of(matrix, b);
    }
  }

  public void testComplexExact() {
    Tensor m = Tensors.fromString("{{1, 1/2 - I}, {1/2 + I, 1/3 + 3*I}, {1/3, 1/4}}");
    Tensor b = Tensors.fromString("{{2, 8 + I}, {3*I, -2}, {4 - I, 3}}");
    Tensor pinv = PseudoInverse.of(m);
    ExactTensorQ.require(pinv);
    Tensor r1 = pinv.dot(b);
    Tensor r2 = LeastSquares.of(m, b);
    ExactTensorQ.require(r2);
    assertEquals(r1, r2);
    Tensor row0 = Tensors.fromString("{130764/54541 + (78*I)/54541, 384876/54541 - (122436*I)/54541}");
    assertEquals(r1.get(0), row0);
    Tensor row1 = Tensors.fromString("{10512/54541 + (16452*I)/54541, -(120372/54541) + (126072*I)/54541}");
    assertEquals(r1.get(1), row1);
  }
}
