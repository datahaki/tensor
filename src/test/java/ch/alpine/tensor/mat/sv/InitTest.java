// code by jph
package ch.alpine.tensor.mat.sv;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.lang.reflect.Modifier;
import java.util.List;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.UnprotectDepr;
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

class InitTest {
  public static SingularValueDecomposition svd(Tensor matrix) {
    Init init = new Init(matrix);
    {
      UnprotectDepr.getUnitUnique(init.u);
      UnprotectDepr.getUnitUnique(init.v);
      assertEquals( //
          UnprotectDepr.getUnitUnique(init.w), //
          UnprotectDepr.getUnitUnique(init.r));
    }
    SingularValueDecomposition svd = new SingularValueDecompositionImpl(init);
    Unit unit = UnprotectDepr.getUnitUnique(matrix);
    List<Integer> dims = Dimensions.of(matrix);
    int N = dims.get(1);
    final Tensor U = svd.getU();
    assertEquals(UnprotectDepr.getUnitUnique(U), Unit.ONE);
    assertEquals(dims, Dimensions.of(U));
    final Tensor w = svd.values();
    Unit unitUnique = UnprotectDepr.getUnitUnique(w);
    assertEquals(unit, unitUnique);
    final Tensor V = svd.getV();
    assertEquals(UnprotectDepr.getUnitUnique(V), Unit.ONE);
    Tensor W = DiagonalMatrix.with(w);
    Tensor UtU = Tolerance.CHOP.of(Transpose.of(U).dot(U).subtract(IdentityMatrix.of(N)));
    assertEquals(UtU, Array.zeros(N, N));
    Tensor VVt = Tolerance.CHOP.of(MatrixDotTranspose.of(V, V).subtract(IdentityMatrix.of(N)));
    assertEquals(VVt, Array.zeros(N, N));
    Tensor VtV = Tolerance.CHOP.of(Transpose.of(V).dot(V).subtract(IdentityMatrix.of(N)));
    assertEquals(VtV, Array.zeros(N, N));
    Tensor UWVt = Tolerance.CHOP.of(MatrixDotTranspose.of(U.dot(W), V).subtract(matrix));
    assertEquals(UWVt, UWVt.map(Scalar::zero));
    Tensor UW_AV = Tolerance.CHOP.of(U.dot(W).subtract(matrix.dot(V)));
    assertEquals(UW_AV, UW_AV.map(Scalar::zero));
    assertTrue(w.stream().map(Scalar.class::cast).noneMatch(Sign::isNegative));
    if (MatrixRank.of(matrix) < N) {
      Tensor nul = NullSpace.of(svd);
      Tensor res = MatrixDotTranspose.of(matrix, nul);
      assertEquals(Tolerance.CHOP.of(res), res.map(Scalar::zero));
    }
    return svd;
  }

  @Test
  void testPackageVisibility() {
    assertFalse(Modifier.isPublic(Init.class.getModifiers()));
  }
}
