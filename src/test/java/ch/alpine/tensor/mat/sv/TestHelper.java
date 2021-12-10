// code by jph
package ch.alpine.tensor.mat.sv;

import java.util.List;

import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Unprotect;
import ch.alpine.tensor.alg.Array;
import ch.alpine.tensor.alg.Dimensions;
import ch.alpine.tensor.alg.Transpose;
import ch.alpine.tensor.mat.DiagonalMatrix;
import ch.alpine.tensor.mat.IdentityMatrix;
import ch.alpine.tensor.mat.MatrixDotTranspose;
import ch.alpine.tensor.mat.NullSpace;
import ch.alpine.tensor.mat.Tolerance;
import ch.alpine.tensor.mat.re.MatrixRank;
import ch.alpine.tensor.qty.Unit;
import ch.alpine.tensor.sca.Sign;
import junit.framework.TestCase;

/* package */ enum TestHelper {
  ;
  public static SingularValueDecomposition svd(Tensor matrix) {
    Init init = new Init(matrix);
    {
      Unprotect.getUnitUnique(init.u);
      Unprotect.getUnitUnique(init.v);
      TestCase.assertEquals( //
          Unprotect.getUnitUnique(init.w), //
          Unprotect.getUnitUnique(init.r));
    }
    SingularValueDecomposition svd = new SingularValueDecompositionImpl(init);
    Unit unit = Unprotect.getUnitUnique(matrix);
    List<Integer> dims = Dimensions.of(matrix);
    int N = dims.get(1);
    final Tensor U = svd.getU();
    TestCase.assertEquals(Unprotect.getUnitUnique(U), Unit.ONE);
    TestCase.assertEquals(dims, Dimensions.of(U));
    final Tensor w = svd.values();
    Unit unitUnique = Unprotect.getUnitUnique(w);
    TestCase.assertEquals(unit, unitUnique);
    final Tensor V = svd.getV();
    TestCase.assertEquals(Unprotect.getUnitUnique(V), Unit.ONE);
    Tensor W = DiagonalMatrix.with(w);
    Tensor UtU = Tolerance.CHOP.of(Transpose.of(U).dot(U).subtract(IdentityMatrix.of(N)));
    TestCase.assertEquals(UtU, Array.zeros(N, N));
    Tensor VVt = Tolerance.CHOP.of(MatrixDotTranspose.of(V, V).subtract(IdentityMatrix.of(N)));
    TestCase.assertEquals(VVt, Array.zeros(N, N));
    Tensor VtV = Tolerance.CHOP.of(Transpose.of(V).dot(V).subtract(IdentityMatrix.of(N)));
    TestCase.assertEquals(VtV, Array.zeros(N, N));
    Tensor UWVt = Tolerance.CHOP.of(MatrixDotTranspose.of(U.dot(W), V).subtract(matrix));
    TestCase.assertEquals(UWVt, UWVt.map(Scalar::zero));
    Tensor UW_AV = Tolerance.CHOP.of(U.dot(W).subtract(matrix.dot(V)));
    TestCase.assertEquals(UW_AV, UW_AV.map(Scalar::zero));
    TestCase.assertTrue(w.stream().map(Scalar.class::cast).noneMatch(Sign::isNegative));
    if (MatrixRank.of(svd) < N) {
      Tensor nul = NullSpace.of(svd);
      Tensor res = MatrixDotTranspose.of(matrix, nul);
      TestCase.assertEquals(Tolerance.CHOP.of(res), res.map(Scalar::zero));
    }
    return svd;
  }
}
