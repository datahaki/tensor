// code by jph
package ch.alpine.tensor.mat.pi;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Random;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.ComplexScalar;
import ch.alpine.tensor.RationalScalar;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.alg.Array;
import ch.alpine.tensor.alg.Dimensions;
import ch.alpine.tensor.alg.NestList;
import ch.alpine.tensor.alg.Range;
import ch.alpine.tensor.alg.Transpose;
import ch.alpine.tensor.alg.UnitVector;
import ch.alpine.tensor.api.ScalarUnaryOperator;
import ch.alpine.tensor.chq.ExactTensorQ;
import ch.alpine.tensor.lie.Permutations;
import ch.alpine.tensor.mat.DiagonalMatrix;
import ch.alpine.tensor.mat.HilbertMatrix;
import ch.alpine.tensor.mat.IdentityMatrix;
import ch.alpine.tensor.mat.Tolerance;
import ch.alpine.tensor.mat.re.Inverse;
import ch.alpine.tensor.mat.re.LinearSolve;
import ch.alpine.tensor.mat.re.MatrixRank;
import ch.alpine.tensor.pdf.ComplexNormalDistribution;
import ch.alpine.tensor.pdf.Distribution;
import ch.alpine.tensor.pdf.RandomVariate;
import ch.alpine.tensor.pdf.c.NormalDistribution;
import ch.alpine.tensor.pdf.c.UniformDistribution;
import ch.alpine.tensor.qty.Quantity;
import ch.alpine.tensor.qty.Unit;
import ch.alpine.tensor.sca.Chop;
import ch.alpine.tensor.sca.N;

class LeastSquaresTest {
  @Test
  void testSquareExact() {
    Tensor matrix = HilbertMatrix.of(4, 4);
    Tensor pinv = PseudoInverse.of(matrix);
    assertEquals(pinv, Inverse.of(matrix));
    ExactTensorQ.require(pinv);
  }

  @Test
  void testMixedUnitsVantHoff() {
    // inspired by code by gjoel
    for (int deg = 0; deg <= 2; ++deg) {
      int degree = deg;
      Tensor x = Tensors.fromString("{100[K], 110.0[K], 130[K], 133[K]}").map(Scalar::reciprocal);
      Tensor y = Tensors.fromString("{10[bar], 20[bar], 22[bar], 23[bar]}");
      assertEquals(x.Get(0).one(), RealScalar.ONE);
      Tensor matrix = x.map(s -> NestList.of(s::multiply, s.one(), degree));
      Tensor sol = LeastSquares.of(matrix, y);
      matrix.dot(sol).subtract(y);
    }
  }

  private static void _checkSpecialExact(Tensor m) {
    assertEquals(MatrixRank.of(m), 3);
    Tensor b = Tensors.vector(1, 1, 1, 1);
    Tensor x0 = LeastSquares.of(m, b);
    ExactTensorQ.require(x0);
    Tensor x1 = PseudoInverse.usingCholesky(m).dot(b);
    ExactTensorQ.require(x1);
    assertEquals(x1, Tensors.vector(1, 1, 1));
    Tensor x2 = LeastSquares.usingQR(m, b);
    ExactTensorQ.require(x2);
    Tensor x3 = LeastSquares.usingSvd(m, b);
    Tolerance.CHOP.requireClose(x0, x1);
    Tolerance.CHOP.requireClose(x1, x3);
    Tolerance.CHOP.requireClose(x2, x3);
  }

  private static void _checkSpecialNumer(Tensor m) {
    assertEquals(MatrixRank.of(m), 3);
    Tensor b = Tensors.vector(1, 1, 1, 1);
    Tensor x0 = LeastSquares.of(m, b);
    Tensor x1 = PseudoInverse.usingCholesky(m).dot(b);
    assertEquals(x1, Tensors.vector(1, 1, 1));
    Tensor x2 = LeastSquares.usingQR(m, b);
    Tensor x3 = LeastSquares.usingSvd(m, b);
    Tolerance.CHOP.requireClose(x0, x1);
    Tolerance.CHOP.requireClose(x1, x2);
    Tolerance.CHOP.requireClose(x3, x2);
  }

  @Test
  void testEasy() {
    Tensor m = Transpose.of(IdentityMatrix.of(4).extract(0, 3));
    for (Tensor perm : Permutations.of(Range.of(0, 4))) {
      Tensor matrix = Tensor.of(perm.stream().map(Scalar.class::cast).map(s -> m.get(s.number().intValue())));
      _checkSpecialExact(matrix);
      _checkSpecialNumer(matrix.map(N.DOUBLE));
    }
  }

  @Test
  void testMathematica() {
    Tensor matrix = HilbertMatrix.of(2, 3);
    assertEquals(matrix, Tensors.fromString("{{1, 1/2, 1/3}, {1/2, 1/3, 1/4}}"));
    Tensor expect = Tensors.fromString("{{252/73, -(360/73)}, {-(198/73), 408/73}, {-(240/73), 468/73}}");
    Tensor cholesky2 = PseudoInverse.usingCholesky(matrix);
    assertEquals(cholesky2, expect);
  }

  @Test
  void testMathematicaB() {
    Tensor matrix = HilbertMatrix.of(2, 3);
    assertEquals(matrix, Tensors.fromString("{{1, 1/2, 1/3}, {1/2, 1/3, 1/4}}"));
    Tensor expect = Tensors.fromString( //
        "{{-(2376/73), 1476/73, -(72/73), 72/73}, {2868/73, -(1410/73), 432/73, 444/73}, {3264/73, -(1656/73), 444/73, 432/73}}");
    Tensor b = Tensors.fromString("{{2, 3, 4, 6}, {8, -2, 3, 4}}");
    Tensor cholesky2 = PseudoInverse.usingCholesky(matrix).dot(b);
    assertEquals(cholesky2, expect);
  }

  @Test
  void testFullRank() {
    Tensor m = Tensors.matrix( //
        (i, j) -> RationalScalar.of(2 * i + 2 + j, 1 + 9 * i + j), 4, 3);
    Tensor b = Tensors.vector(1, 1, 1, 1);
    Tensor x1 = PseudoInverse.usingCholesky(m).dot(b);
    Tensor x2 = LeastSquares.usingSvd(m, b);
    Tensor x3 = LeastSquares.usingQR(m, b);
    assertEquals(Dimensions.of(x1), List.of(3));
    Chop._10.requireClose(x1, x2); // not below tolerance
    Chop._10.requireClose(x3, x2); // not below tolerance
  }

  @Test
  void testLowRank() {
    Tensor m = Tensors.matrix( //
        (i, j) -> RationalScalar.of(2 * i + j, 9 + j), 4, 3);
    assertEquals(MatrixRank.of(m), 2);
    Tensor b = Tensors.vector(1, 1, 1, 1);
    Tensor x2 = LeastSquares.usingSvd(m, b);
    Tolerance.CHOP.requireClose(m.dot(x2), b);
  }

  @Test
  void testFullRankComplex() {
    Tensor m = Tensors.matrix( //
        (i, j) -> ComplexScalar.of( //
            RealScalar.of(i), RationalScalar.of(2 * i + 2 + j, 1 + 9 * i + j)),
        4, 3);
    assertEquals(MatrixRank.of(m), 3);
    Tensor b = Tensors.vector(1, 1, 1, 1);
    Tensor x1 = PseudoInverse.usingCholesky(m).dot(b);
    Tensor d1 = m.dot(x1).subtract(b);
    Tensor s1 = Tensors.fromString(
        "{726086997/5793115330933+44069346/5793115330933*I, 67991764500/5793115330933+4521379500/5793115330933*I,-22367102670/827587904419-1672185060/827587904419*I, 11662258824/827587904419+1019978082/827587904419*I}");
    assertEquals(d1, s1);
  }

  @Test
  void testRankDeficient() {
    assertEquals(LeastSquares.of(Array.zeros(3, 3), UnitVector.of(3, 1)), Array.zeros(3));
    assertEquals(LeastSquares.of(Array.zeros(3, 2), UnitVector.of(3, 1)), Array.zeros(2));
    assertEquals(LeastSquares.of(DiagonalMatrix.of(0, 1, 0), UnitVector.of(3, 0)), Array.zeros(3));
  }

  @Test
  void testFullRankComplex2() {
    Tensor m = Tensors.matrix( //
        (i, j) -> ComplexScalar.of( //
            RealScalar.of(18 * i + j * 100), RationalScalar.of(2 * i + 2 + j, 1 + 9 * i + j)),
        4, 3);
    assertEquals(MatrixRank.of(m), 3);
    Tensor pinv1 = PseudoInverse.usingCholesky(m);
    Tensor pinv2 = BenIsraelCohen.of(m);
    Chop._08.requireClose(pinv1, pinv2);
    Tensor b = Tensors.fromString("{2+3*I, 1-8*I, 99-100*I, 2/5}");
    Tensor d0 = LeastSquares.of(m, b);
    Tensor d1 = pinv1.dot(b);
    Tensor d2 = pinv2.dot(b);
    Chop._08.requireClose(d0, d1);
    Chop._08.requireClose(d1, d2);
  }

  @Test
  void testRect() {
    Distribution distribution = UniformDistribution.unit();
    Tensor matrix = RandomVariate.of(distribution, 10, 3);
    Tensor b = RandomVariate.of(distribution, 10, 2);
    Tensor x = LeastSquares.usingSvd(matrix, b);
    assertEquals(Dimensions.of(x), Arrays.asList(3, 2));
    assertEquals(Dimensions.of(matrix.dot(x)), Dimensions.of(b));
  }

  @Test
  void testLeastSquaresReal() {
    for (int n = 3; n < 6; ++n) {
      Tensor matrix = IdentityMatrix.of(n).add(RandomVariate.of(NormalDistribution.standard(), n, n));
      Tensor b = RandomVariate.of(NormalDistribution.standard(), n, 2);
      Chop._09.requireClose( //
          LinearSolve.of(matrix, b), //
          LeastSquares.usingQR(matrix, b));
    }
  }

  @Test
  void testLeastSquaresComplexSquare() {
    Random random = new Random(3);
    for (int n = 3; n < 6; ++n) {
      Tensor matrix = RandomVariate.of(ComplexNormalDistribution.STANDARD, random, n, n);
      Tensor b = RandomVariate.of(NormalDistribution.standard(), random, n, 3);
      Tensor sol = LinearSolve.of(matrix, b);
      Tolerance.CHOP.requireClose(sol, LeastSquares.of(matrix, b));
      Tolerance.CHOP.requireClose(sol, LeastSquares.usingQR(matrix, b));
    }
  }

  @Test
  void testLeastSquaresRect() {
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

  @Test
  void testLeastSquaresSmallBig() {
    for (int n = 3; n < 6; ++n) {
      int m = n + 3;
      Tensor matrix = RandomVariate.of(NormalDistribution.standard(), n, m);
      Tensor b = RandomVariate.of(NormalDistribution.standard(), n, 2);
      Tensor sol1 = PseudoInverse.of(matrix).dot(b);
      Tensor sol2 = LeastSquares.usingQR(matrix, b);
      Tolerance.CHOP.requireClose(sol1, sol2);
    }
  }

  @Test
  void testComplexExact() {
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
    Tensor r3 = LeastSquares.usingQR(m, b);
    Tolerance.CHOP.requireClose(r1, r3);
    Tensor r4 = PseudoInverse.usingQR(m).dot(b);
    Tolerance.CHOP.requireClose(r1, r4);
  }

  /** @param unit non-null
   * @return operator that maps a scalar to the quantity with value
   * of given scalar and given unit
   * @throws Exception if given unit is null */
  public static ScalarUnaryOperator attach(Unit unit) {
    Objects.requireNonNull(unit);
    return scalar -> Quantity.of(scalar, unit);
  }

  @Test
  void testComplexExactQuantity() {
    Tensor m = Tensors.fromString("{{1, 1/2 - I}, {1/2 + I, 1/3 + 3*I}, {1/3, 1/4}}").map(attach(Unit.of("A")));
    Tensor b = Tensors.fromString("{{2, 8 + I}, {3*I, -2}, {4 - I, 3}}").map(attach(Unit.of("s")));
    Tensor pinv = PseudoInverse.of(m);
    ExactTensorQ.require(pinv);
    Tensor r1 = pinv.dot(b);
    Tensor r2 = LeastSquares.of(m, b);
    ExactTensorQ.require(r2);
    assertEquals(r1, r2);
    Tensor row0 = Tensors.fromString("{130764/54541 + (78*I)/54541, 384876/54541 - (122436*I)/54541}") //
        .map(attach(Unit.of("s*A^-1")));
    assertEquals(r1.get(0), row0);
    Tensor row1 = Tensors.fromString("{10512/54541 + (16452*I)/54541, -(120372/54541) + (126072*I)/54541}") //
        .map(attach(Unit.of("s*A^-1")));
    assertEquals(r1.get(1), row1);
    Tensor r3 = LeastSquares.usingQR(m, b);
    Tolerance.CHOP.requireClose(r1, r3);
    Tensor r4 = PseudoInverse.usingQR(m).dot(b);
    Tolerance.CHOP.requireClose(r1, r4);
  }

  @Test
  void testComplexSmallBig() {
    Tensor m = Tensors.fromString("{{1, 1/2 + I, 1/3}, {1/2 - I, 1/3 + 3*I, 1/4}}");
    Tensor b = Tensors.fromString("{{2, 3*I, 4 - I}, {8 + I, -2, 3}}");
    Tensor pinv = PseudoInverse.of(m);
    ExactTensorQ.require(pinv);
    Tensor r1 = pinv.dot(b);
    Tensor r2 = LeastSquares.of(m, b);
    ExactTensorQ.require(r2);
    assertEquals(r1, r2);
    Tensor row0 = Tensors.fromString("{-(29304/54541) + (51768*I)/54541, 86256/54541 + (109332*I)/54541, 120888/54541 - (85356*I)/54541}");
    assertEquals(r1.get(0), row0);
    Tensor row1 = Tensors.fromString("{14532/54541 - (131568*I)/54541, -(2436/54541) + (87534*I)/54541, 61452/54541 - (52506*I)/54541}");
    assertEquals(r1.get(1), row1);
    Tensor row2 = Tensors.fromString("{-(1344/54541) - (1548*I)/54541, 7488/54541 + (38880*I)/54541, 42132/54541 - (13152*I)/54541}");
    assertEquals(r1.get(2), row2);
    Tensor r3 = LeastSquares.usingQR(m, b);
    Tolerance.CHOP.requireClose(r1, r3);
    Tensor r4 = PseudoInverse.usingQR(m).dot(b);
    Tolerance.CHOP.requireClose(r1, r4);
  }

  @Test
  void testComplexSmallBigQuantity() {
    Tensor m = Tensors.fromString("{{1, 1/2 + I, 1/3}, {1/2 - I, 1/3 + 3*I, 1/4}}").map(attach(Unit.of("kg^-1")));
    Tensor b = Tensors.fromString("{{2, 3*I, 4 - I}, {8 + I, -2, 3}}").map(attach(Unit.of("m")));
    Tensor pinv = PseudoInverse.of(m);
    ExactTensorQ.require(pinv);
    Tensor r1 = pinv.dot(b);
    Tensor r2 = LeastSquares.of(m, b);
    ExactTensorQ.require(r2);
    assertEquals(r1, r2);
    Tensor row0 = Tensors.fromString("{-(29304/54541) + (51768*I)/54541, 86256/54541 + (109332*I)/54541, 120888/54541 - (85356*I)/54541}");
    assertEquals(r1.get(0), row0.map(attach(Unit.of("kg*m"))));
    Tensor row1 = Tensors.fromString("{14532/54541 - (131568*I)/54541, -(2436/54541) + (87534*I)/54541, 61452/54541 - (52506*I)/54541}");
    assertEquals(r1.get(1), row1.map(attach(Unit.of("kg*m"))));
    Tensor row2 = Tensors.fromString("{-(1344/54541) - (1548*I)/54541, 7488/54541 + (38880*I)/54541, 42132/54541 - (13152*I)/54541}");
    assertEquals(r1.get(2), row2.map(attach(Unit.of("kg*m"))));
    Tensor r3 = LeastSquares.usingQR(m, b);
    Tolerance.CHOP.requireClose(r1, r3);
    Tensor r4 = PseudoInverse.usingQR(m).dot(b);
    Tolerance.CHOP.requireClose(r1, r4);
  }
}
