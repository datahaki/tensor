// code by jph
package ch.alpine.tensor.mat.cd;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Scalars;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.Throw;
import ch.alpine.tensor.alg.Array;
import ch.alpine.tensor.alg.TensorMap;
import ch.alpine.tensor.alg.Transpose;
import ch.alpine.tensor.chq.ExactTensorQ;
import ch.alpine.tensor.ext.Serialization;
import ch.alpine.tensor.mat.ConjugateTranspose;
import ch.alpine.tensor.mat.DiagonalMatrix;
import ch.alpine.tensor.mat.HermitianMatrixQ;
import ch.alpine.tensor.mat.HilbertMatrix;
import ch.alpine.tensor.mat.IdentityMatrix;
import ch.alpine.tensor.mat.NegativeDefiniteMatrixQ;
import ch.alpine.tensor.mat.NegativeSemidefiniteMatrixQ;
import ch.alpine.tensor.mat.PositiveDefiniteMatrixQ;
import ch.alpine.tensor.mat.PositiveSemidefiniteMatrixQ;
import ch.alpine.tensor.mat.SymmetricMatrixQ;
import ch.alpine.tensor.mat.Tolerance;
import ch.alpine.tensor.mat.UpperTriangularize;
import ch.alpine.tensor.mat.pi.LeastSquares;
import ch.alpine.tensor.mat.pi.PseudoInverse;
import ch.alpine.tensor.mat.re.Det;
import ch.alpine.tensor.mat.re.Inverse;
import ch.alpine.tensor.mat.re.LinearSolve;
import ch.alpine.tensor.mat.re.MatrixRank;
import ch.alpine.tensor.pdf.Distribution;
import ch.alpine.tensor.pdf.RandomVariate;
import ch.alpine.tensor.pdf.c.NormalDistribution;
import ch.alpine.tensor.qty.LenientAdd;
import ch.alpine.tensor.qty.Quantity;
import ch.alpine.tensor.red.Times;
import ch.alpine.tensor.sca.Chop;
import ch.alpine.tensor.sca.N;
import ch.alpine.tensor.sca.pow.Sqrt;

class CholeskyDecompositionTest {
  static CholeskyDecomposition checkDecomp(Tensor matrix) {
    int n = matrix.length();
    CholeskyDecomposition choleskyDecomposition = CholeskyDecomposition.of(matrix);
    Tensor res = choleskyDecomposition.getL().dot(Times.of(choleskyDecomposition.diagonal(), ConjugateTranspose.of(choleskyDecomposition.getL())));
    Tolerance.CHOP.requireClose(matrix.subtract(res), Array.zeros(n, n));
    Tolerance.CHOP.requireClose(choleskyDecomposition.det(), Det.of(matrix));
    return choleskyDecomposition;
  }

  @Test
  void testRosetta1() {
    // +5 0 0
    // +3 3 0
    // -1 1 3
    // {{5, 3, -1}, {0, 3, 1}, {0, 0, 3}}
    Tensor matrix = Tensors.matrix(new Number[][] { //
        { 25, 15, -5 }, //
        { 15, 18, 0 }, //
        { -5, 0, 11 } //
    });
    CholeskyDecomposition choleskyDecomposition = checkDecomp(matrix);
    {
      Tensor b = Tensors.vector(1, 2, 3);
      Tensor actual = choleskyDecomposition.solve(b);
      Tensor expect = Inverse.of(matrix).dot(b);
      assertEquals(actual, expect);
    }
    {
      Tensor b = Tensors.fromString("{{1, 2}, {3, 3}, {4, -1}}");
      Tensor expect = Inverse.of(matrix).dot(b);
      Tensor actual = choleskyDecomposition.solve(b);
      assertEquals(actual, expect);
    }
    assertThrows(IllegalArgumentException.class, () -> choleskyDecomposition.solve(Tensors.vector(1, 2, 3, 4)));
    assertThrows(IllegalArgumentException.class, () -> choleskyDecomposition.solve(Tensors.vector(1, 2)));
    assertThrows(IllegalArgumentException.class, () -> choleskyDecomposition.solve(RealScalar.ONE));
  }

  @Test
  void testWikiEn() throws Exception {
    CholeskyDecomposition cd = //
        checkDecomp(Tensors.matrix(new Number[][] { //
            { 4, 12, -16 }, //
            { 12, 37, -43 }, //
            { -16, -43, 98 } //
        }));
    Tensor ltrue = Tensors.matrix(new Number[][] { //
        { 1, 0, 0 }, //
        { 3, 1, 0 }, //
        { -4, 5, 1 } //
    });
    assertEquals(cd.getL(), ltrue);
    assertEquals(cd.diagonal(), Tensors.vector(4, 1, 9));
    Serialization.of(cd);
    Chop.NONE.requireAllZero(UpperTriangularize.of(cd.getL(), 1));
  }

  @Test
  void testMathematica1() {
    checkDecomp(Tensors.matrix(new Number[][] { //
        { 2, 1 }, //
        { 1, 2 } //
    }));
  }

  @Test
  void testMathematica2() {
    checkDecomp(Tensors.fromString("{{4, 3, 2, 1}, {3, 4, 3, 2}, {2, 3, 4, 3}, {+1, 2, 3, 4}}"));
    checkDecomp(Tensors.fromString("{{4, 3, 2, I}, {3, 4, 3, 2}, {2, 3, 4, 3}, {-I, 2, 3, 4}}"));
    checkDecomp(Tensors.fromString("{{4, 3, 2, I}, {3, 4, 3, 2}, {2, 3, 4, 3}, {-I, 2, 3, 0}}"));
  }

  @Test
  void testFail1() {
    assertThrows(Throw.class, () -> checkDecomp(Tensors.fromString("{{4, 2}, {1, 4}}")));
  }

  @Test
  void testFail2() {
    assertThrows(Throw.class, () -> checkDecomp(Tensors.fromString("{{4, I}, {I, 4}}")));
  }

  @Test
  void testHilbert1() {
    checkDecomp(HilbertMatrix.of(10));
  }

  @Test
  void testHilbertN1() {
    checkDecomp(N.DOUBLE.of(HilbertMatrix.of(16)));
  }

  @Test
  void testDiag() {
    checkDecomp(DiagonalMatrix.of(1, -2, 3, -4));
    checkDecomp(DiagonalMatrix.of(0, -2, 3, -4));
    checkDecomp(DiagonalMatrix.of(0, -2, 3, -4));
    checkDecomp(DiagonalMatrix.of(0, -2, 0, -4));
    checkDecomp(DiagonalMatrix.of(1, 0, 3, -4));
    checkDecomp(DiagonalMatrix.of(1, -2, 3, 0));
    checkDecomp(DiagonalMatrix.of(1, 0, 3, 0));
  }

  @Test
  void testZeros1() {
    checkDecomp(Array.zeros(1, 1));
    checkDecomp(Array.zeros(5, 5));
  }

  @Test
  void testComplex() {
    checkDecomp(Tensors.fromString("{{10, I}, {-I, 10}}"));
    checkDecomp(N.DOUBLE.of(Tensors.fromString("{{10, I}, {-I, 10}}")));
  }

  @Test
  void testQuantity1() {
    Scalar qs1 = Quantity.of(1, "m");
    Scalar qs2 = Quantity.of(2, "m");
    Tensor ve1 = Tensors.of(qs2, qs1);
    Tensor ve2 = Tensors.of(qs1, qs2);
    Tensor mat = Tensors.of(ve1, ve2);
    CholeskyDecomposition cd = CholeskyDecomposition.of(mat);
    assertTrue(Scalars.nonZero(cd.det()));
    assertEquals(cd.diagonal().toString(), "{2[m], 3/2[m]}");
    SymmetricMatrixQ.require(mat);
    assertTrue(HermitianMatrixQ.of(mat));
    assertTrue(PositiveDefiniteMatrixQ.ofHermitian(mat));
    assertTrue(PositiveSemidefiniteMatrixQ.ofHermitian(mat));
    assertFalse(NegativeDefiniteMatrixQ.ofHermitian(mat));
    assertFalse(NegativeSemidefiniteMatrixQ.ofHermitian(mat));
  }

  @Test
  void testQuantity2() {
    Tensor matrix = Tensors.fromString( //
        "{{60[m^2], 30[m*rad], 20[kg*m]}, {30[m*rad], 20[rad^2], 15[kg*rad]}, {20[kg*m], 15[kg*rad], 12[kg^2]}}");
    {
      Tensor eye = IdentityMatrix.of(3);
      Tensor inv = LinearSolve.of(matrix, eye);
      Tensor res = matrix.dot(inv);
      Tensor expect = Tensors.fromString("{{1, 0[m*rad^-1], 0[kg^-1*m]}, {0[m^-1*rad], 1, 0[kg^-1*rad]}, {0[kg*m^-1], 0[kg*rad^-1], 1}}");
      assertEquals(res, expect);
    }
    CholeskyDecomposition cd = CholeskyDecomposition.of(matrix);
    {
      assertEquals(Det.of(matrix), cd.det()); // 100[kg^2, m^2, rad^2]
      Tensor lower = rows_pmul_v(cd.getL(), Sqrt.of(cd.diagonal()));
      Tensor upper = Times.of(Sqrt.of(cd.diagonal()), ConjugateTranspose.of(cd.getL()));
      Tensor res = Tensors.matrix((i, j) -> Times.of(lower.get(i), upper.get(Tensor.ALL, j)).stream().reduce(LenientAdd::of).orElseThrow(), 3, 3);
      Chop._10.requireClose(matrix, res);
    }
    {
      Tensor lower = rows_pmul_v(cd.getL(), cd.diagonal());
      Tensor upper = ConjugateTranspose.of(cd.getL());
      Tensor res = Tensors.matrix((i, j) -> Times.of(lower.get(i), upper.get(Tensor.ALL, j)).stream().reduce(LenientAdd::of).orElseThrow(), 3, 3);
      ExactTensorQ.require(res);
      assertEquals(res, matrix);
    }
    SymmetricMatrixQ.require(matrix);
    assertTrue(HermitianMatrixQ.of(matrix));
    assertTrue(PositiveDefiniteMatrixQ.ofHermitian(matrix));
    assertTrue(PositiveSemidefiniteMatrixQ.ofHermitian(matrix));
    assertFalse(NegativeDefiniteMatrixQ.ofHermitian(matrix));
    assertFalse(NegativeSemidefiniteMatrixQ.ofHermitian(matrix));
  }

  private static Tensor rows_pmul_v(Tensor L, Tensor diag) {
    return TensorMap.of(row -> Times.of(row, diag), L, 1); // apply pmul on level 1
  }

  /** Mathematica fails to compute L'.L
   * A = {{Quantity[10, "Meters"^2], Quantity[I, "Kilograms"*"Meters"]}, {Quantity[-I,
   * "Kilograms"*"Meters"], Quantity[10, "Kilograms"^2]}}
   * L = CholeskyDecomposition[A]
   * ConjugateTranspose[L] . L */
  @Test
  void testQuantityComplex() {
    Tensor matrix = Tensors.fromString("{{10[m^2], I[m*kg]}, {-I[m*kg], 10[kg^2]}}");
    assertTrue(PositiveDefiniteMatrixQ.ofHermitian(matrix));
    // System.out.println(MathematicaForm.of(matrix));
    CholeskyDecomposition cd = CholeskyDecomposition.of(matrix);
    Tensor sdiag = Sqrt.of(cd.diagonal());
    Times.of(sdiag, ConjugateTranspose.of(cd.getL()));
    {
      Tensor lower = rows_pmul_v(cd.getL(), cd.diagonal());
      Tensor upper = ConjugateTranspose.of(cd.getL());
      Tensor res = Tensors.matrix((i, j) -> Times.of(lower.get(i), upper.get(Tensor.ALL, j)).stream().reduce(LenientAdd::of).orElseThrow(), 2, 2);
      ExactTensorQ.require(res);
      assertEquals(res, matrix);
    }
    assertEquals( //
        cd.solve(IdentityMatrix.of(2)), //
        Inverse.of(matrix));
  }

  @ParameterizedTest
  @ValueSource(ints = { 1, 2, 3 })
  void testRankDeficient(int r) {
    int n = 7;
    int _m = 5;
    Distribution distribution = NormalDistribution.standard();
    Tensor m1 = RandomVariate.of(distribution, n, r);
    Tensor m2 = RandomVariate.of(distribution, r, _m);
    Tensor br = RandomVariate.of(distribution, n);
    assertEquals(MatrixRank.of(m1), r);
    Tensor matrix = m1.dot(m2);
    assertEquals(MatrixRank.of(matrix), r);
    {
      assertThrows(Throw.class, () -> PseudoInverse.usingCholesky(matrix));
      Tensor ls1 = LeastSquares.of(matrix, br);
      Tensor ls2 = PseudoInverse.of(matrix).dot(br);
      Tolerance.CHOP.requireClose(ls1, ls2);
    }
    {
      Tensor m = Transpose.of(matrix);
      Tensor b = RandomVariate.of(distribution, _m);
      assertThrows(Throw.class, () -> PseudoInverse.usingCholesky(m));
      Tensor ls1 = LeastSquares.of(m, b);
      Tensor ls2 = PseudoInverse.of(m).dot(b);
      Tolerance.CHOP.requireClose(ls1, ls2);
    }
  }

  @Test
  void testRectFail() {
    Tensor matrix = Tensors.fromString("{{10, I}}");
    assertThrows(IllegalArgumentException.class, () -> CholeskyDecomposition.of(matrix));
  }
}
