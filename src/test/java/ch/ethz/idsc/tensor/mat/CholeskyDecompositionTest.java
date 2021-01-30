// code by jph
package ch.ethz.idsc.tensor.mat;

import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Scalars;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Array;
import ch.ethz.idsc.tensor.alg.TensorMap;
import ch.ethz.idsc.tensor.alg.Transpose;
import ch.ethz.idsc.tensor.ext.Serialization;
import ch.ethz.idsc.tensor.pdf.Distribution;
import ch.ethz.idsc.tensor.pdf.NormalDistribution;
import ch.ethz.idsc.tensor.pdf.RandomVariate;
import ch.ethz.idsc.tensor.qty.Quantity;
import ch.ethz.idsc.tensor.sca.Chop;
import ch.ethz.idsc.tensor.sca.N;
import ch.ethz.idsc.tensor.sca.Sqrt;
import ch.ethz.idsc.tensor.usr.AssertFail;
import junit.framework.TestCase;

public class CholeskyDecompositionTest extends TestCase {
  static CholeskyDecomposition checkDecomp(Tensor matrix) {
    int n = matrix.length();
    CholeskyDecomposition choleskyDecomposition = CholeskyDecomposition.of(matrix);
    Tensor res = choleskyDecomposition.getL().dot(choleskyDecomposition.diagonal().pmul(ConjugateTranspose.of(choleskyDecomposition.getL())));
    Tolerance.CHOP.requireClose(matrix.subtract(res), Array.zeros(n, n));
    Tolerance.CHOP.requireClose(choleskyDecomposition.det(), Det.of(matrix));
    return choleskyDecomposition;
  }

  public void testRosetta1() {
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
    AssertFail.of(() -> choleskyDecomposition.solve(Tensors.vector(1, 2, 3, 4)));
    AssertFail.of(() -> choleskyDecomposition.solve(Tensors.vector(1, 2)));
    AssertFail.of(() -> choleskyDecomposition.solve(RealScalar.ONE));
  }

  public void testWikiEn() throws Exception {
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

  public void testMathematica1() {
    checkDecomp(Tensors.matrix(new Number[][] { //
        { 2, 1 }, //
        { 1, 2 } //
    }));
  }

  public void testMathematica2() {
    checkDecomp(Tensors.fromString("{{4, 3, 2, 1}, {3, 4, 3, 2}, {2, 3, 4, 3}, {+1, 2, 3, 4}}"));
    checkDecomp(Tensors.fromString("{{4, 3, 2, I}, {3, 4, 3, 2}, {2, 3, 4, 3}, {-I, 2, 3, 4}}"));
    checkDecomp(Tensors.fromString("{{4, 3, 2, I}, {3, 4, 3, 2}, {2, 3, 4, 3}, {-I, 2, 3, 0}}"));
  }

  public void testFail1() {
    AssertFail.of(() -> checkDecomp(Tensors.fromString("{{4, 2}, {1, 4}}")));
  }

  public void testFail2() {
    AssertFail.of(() -> checkDecomp(Tensors.fromString("{{4, I}, {I, 4}}")));
  }

  public void testHilbert1() {
    checkDecomp(HilbertMatrix.of(10));
  }

  public void testHilbertN1() {
    checkDecomp(N.DOUBLE.of(HilbertMatrix.of(16)));
  }

  public void testDiag() {
    checkDecomp(DiagonalMatrix.of(1, -2, 3, -4));
    checkDecomp(DiagonalMatrix.of(0, -2, 3, -4));
    checkDecomp(DiagonalMatrix.of(0, -2, 3, -4));
    checkDecomp(DiagonalMatrix.of(0, -2, 0, -4));
    checkDecomp(DiagonalMatrix.of(1, 0, 3, -4));
    checkDecomp(DiagonalMatrix.of(1, -2, 3, 0));
    checkDecomp(DiagonalMatrix.of(1, 0, 3, 0));
  }

  public void testZeros1() {
    checkDecomp(Array.zeros(1, 1));
    checkDecomp(Array.zeros(5, 5));
  }

  public void testComplex() {
    checkDecomp(Tensors.fromString("{{10, I}, {-I, 10}}"));
    checkDecomp(N.DOUBLE.of(Tensors.fromString("{{10, I}, {-I, 10}}")));
  }

  public void testQuantity1() {
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

  public void testQuantity2() {
    Tensor matrix = Tensors.fromString( //
        "{{60[m^2], 30[m*rad], 20[kg*m]}, {30[m*rad], 20[rad^2], 15[kg*rad]}, {20[kg*m], 15[kg*rad], 12[kg^2]}}");
    {
      Tensor eye = IdentityMatrix.of(3);
      Tensor inv = LinearSolve.of(matrix, eye);
      Tensor res = matrix.dot(inv);
      Chop.NONE.requireClose(eye, res);
    }
    {
      Tensor inv = Inverse.of(matrix);
      Chop.NONE.requireClose(matrix.dot(inv), inv.dot(matrix));
      Chop.NONE.requireClose(matrix.dot(inv), IdentityMatrix.of(3));
    }
    {
      CholeskyDecomposition cd = CholeskyDecomposition.of(matrix);
      assertEquals(Det.of(matrix), cd.det()); // 100[kg^2, m^2, rad^2]
      Tensor lower = rows_pmul_v(cd.getL(), Sqrt.of(cd.diagonal()));
      Tensor upper = Sqrt.of(cd.diagonal()).pmul(ConjugateTranspose.of(cd.getL()));
      Tensor res = lower.dot(upper);
      Chop._10.requireClose(matrix, res);
    }
    SymmetricMatrixQ.require(matrix);
    assertTrue(HermitianMatrixQ.of(matrix));
    assertTrue(PositiveDefiniteMatrixQ.ofHermitian(matrix));
    assertTrue(PositiveSemidefiniteMatrixQ.ofHermitian(matrix));
    assertFalse(NegativeDefiniteMatrixQ.ofHermitian(matrix));
    assertFalse(NegativeSemidefiniteMatrixQ.ofHermitian(matrix));
  }

  private static Tensor rows_pmul_v(Tensor L, Tensor diag) {
    return TensorMap.of(row -> row.pmul(diag), L, 1); // apply pmul on level 1
  }

  public void testQuantityComplex() {
    Tensor matrix = Tensors.fromString("{{10[m^2], I[m*kg]}, {-I[m*kg], 10[kg^2]}}");
    CholeskyDecomposition cd = CholeskyDecomposition.of(matrix);
    Tensor sdiag = Sqrt.of(cd.diagonal());
    Tensor upper = sdiag.pmul(ConjugateTranspose.of(cd.getL()));
    {
      Tensor res = ConjugateTranspose.of(upper).dot(upper);
      Chop._10.requireClose(matrix, res);
    }
    {
      // the construction of the lower triangular matrix L . L* is not so convenient
      // Tensor lower = Transpose.of(sdiag.pmul(Transpose.of(cd.getL())));
      Tensor lower = rows_pmul_v(cd.getL(), sdiag);
      Tensor res = lower.dot(upper);
      Chop._10.requireClose(matrix, res);
    }
    {
      assertEquals( //
          cd.solve(IdentityMatrix.of(2)), //
          Inverse.of(matrix));
    }
  }

  public void testRankDeficient() {
    int n = 7;
    int _m = 5;
    Distribution distribution = NormalDistribution.standard();
    for (int r = 1; r < _m - 1; ++r) {
      Tensor m1 = RandomVariate.of(distribution, n, r);
      Tensor m2 = RandomVariate.of(distribution, r, _m);
      Tensor br = RandomVariate.of(distribution, n);
      assertEquals(MatrixRank.of(m1), r);
      {
        Tensor lsqr = LeastSquares.usingCholesky(m1, br);
        Tensor pinv = PseudoInverse.usingCholesky(m1).dot(br);
        Chop._10.requireClose(lsqr, pinv);
      }
      Tensor matrix = m1.dot(m2);
      assertEquals(MatrixRank.of(matrix), r);
      {
        AssertFail.of(() -> PseudoInverse.usingCholesky(matrix));
        AssertFail.of(() -> LeastSquares.usingCholesky(matrix, br));
        Tensor ls1 = LeastSquares.of(matrix, br);
        Tensor ls2 = PseudoInverse.of(matrix).dot(br);
        Tolerance.CHOP.requireClose(ls1, ls2);
      }
      {
        Tensor m = Transpose.of(matrix);
        Tensor b = RandomVariate.of(distribution, _m);
        AssertFail.of(() -> PseudoInverse.usingCholesky(m));
        AssertFail.of(() -> LeastSquares.usingCholesky(m, b));
        Tensor ls1 = LeastSquares.of(m, b);
        Tensor ls2 = PseudoInverse.of(m).dot(b);
        Tolerance.CHOP.requireClose(ls1, ls2);
      }
    }
  }

  public void testRectFail() {
    Tensor matrix = Tensors.fromString("{{10, I}}");
    AssertFail.of(() -> CholeskyDecomposition.of(matrix));
  }
}
