// code by jph
package ch.alpine.tensor.mat.sv;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Unprotect;
import ch.alpine.tensor.alg.Dimensions;
import ch.alpine.tensor.alg.Transpose;
import ch.alpine.tensor.mat.DiagonalMatrix;
import ch.alpine.tensor.mat.IdentityMatrix;
import ch.alpine.tensor.mat.MatrixDotTranspose;
import ch.alpine.tensor.mat.NullSpace;
import ch.alpine.tensor.mat.Tolerance;
import ch.alpine.tensor.mat.re.MatrixRank;
import ch.alpine.tensor.qty.QuantityUnit;
import ch.alpine.tensor.qty.Unit;
import ch.alpine.tensor.red.EqualsReduce;
import ch.alpine.tensor.sca.Sign;

public enum SingularValueDecompositionWrap {
  ;
  public static SingularValueDecomposition of(Tensor matrix) {
    Init init = new Init(matrix);
    {
      assertTrue(Unprotect.isUnitUnique(init.u));
      assertTrue(Unprotect.isUnitUnique(init.v));
      assertEquals( //
          EqualsReduce.zero(init.w), //
          EqualsReduce.zero(init.r));
    }
    SingularValueDecomposition svd = SingularValueDecomposition.of(matrix);
    Unit unit = QuantityUnit.of(EqualsReduce.zero(matrix));
    List<Integer> dims = Dimensions.of(matrix);
    int N = dims.get(1);
    final Tensor U = svd.getU();
    assertEquals(EqualsReduce.zero(U), RealScalar.ZERO);
    assertEquals(dims, Dimensions.of(U));
    final Tensor w = svd.values();
    Unit unitUnique = QuantityUnit.of(EqualsReduce.zero(w));
    assertEquals(unit, unitUnique);
    final Tensor V = svd.getV();
    assertEquals(EqualsReduce.zero(V), RealScalar.ZERO);
    Tensor W = DiagonalMatrix.with(w);
    Tolerance.CHOP.requireClose(Transpose.of(U).dot(U), IdentityMatrix.of(N));
    Tolerance.CHOP.requireClose(MatrixDotTranspose.of(V, V), IdentityMatrix.of(N));
    Tolerance.CHOP.requireClose(Transpose.of(V).dot(V), IdentityMatrix.of(N));
    Tolerance.CHOP.requireClose(MatrixDotTranspose.of(U.dot(W), V), matrix);
    Tolerance.CHOP.requireClose(U.dot(W), matrix.dot(V));
    assertTrue(w.stream().map(Scalar.class::cast).noneMatch(Sign::isNegative));
    if (MatrixRank.of(matrix) < N) {
      Tensor nul = NullSpace.of(svd);
      Tensor res = MatrixDotTranspose.of(matrix, nul);
      Tolerance.CHOP.requireAllZero(res);
    }
    return svd;
  }
}
