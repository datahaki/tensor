// code by jph
package ch.alpine.tensor.mat.cd;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.lang.reflect.Modifier;
import java.util.Random;
import java.util.random.RandomGenerator;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.DecimalScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.alg.Dot;
import ch.alpine.tensor.alg.Transpose;
import ch.alpine.tensor.ext.Serialization;
import ch.alpine.tensor.mat.DiagonalMatrix;
import ch.alpine.tensor.mat.HermitianMatrixQ;
import ch.alpine.tensor.mat.HilbertMatrix;
import ch.alpine.tensor.mat.IdentityMatrix;
import ch.alpine.tensor.mat.SymmetricMatrixQ;
import ch.alpine.tensor.mat.Tolerance;
import ch.alpine.tensor.mat.re.Inverse;
import ch.alpine.tensor.mat.re.Pivots;
import ch.alpine.tensor.num.GaussScalar;
import ch.alpine.tensor.sca.N;

class CholeskyDecompositionImplTest {
  @Test
  void testSolveQuantity() throws ClassNotFoundException, IOException {
    Tensor matrix = Tensors.fromString( //
        "{{60[m^2], 30[m*rad], 20[kg*m]}, {30[m*rad], 20[rad^2], 15[kg*rad]}, {20[kg*m], 15[kg*rad], 12[kg^2]}}");
    CholeskyDecomposition choleskyDecomposition = //
        Serialization.copy(CholeskyDecomposition.of(matrix));
    Tensor inverse = Inverse.of(matrix);
    Tensor solve = choleskyDecomposition.solve(IdentityMatrix.of(3));
    assertEquals(solve, inverse);
    assertTrue(choleskyDecomposition.toString().startsWith("CholeskyDecomposition["));
  }

  @Test
  void testGaussScalar() throws ClassNotFoundException, IOException {
    int n = 7;
    int prime = 7879;
    RandomGenerator random = new Random(1);
    Tensor matrix = Tensors.matrix((i, j) -> GaussScalar.of(random.nextInt(), prime), n, n);
    // Symmetrize
    matrix = Transpose.of(matrix).add(matrix).divide(GaussScalar.of(2, prime));
    SymmetricMatrixQ.require(matrix);
    HermitianMatrixQ.require(matrix);
    CholeskyDecomposition choleskyDecomposition = //
        Serialization.copy(CholeskyDecomposition.of(matrix));
    Tensor result = Dot.of( //
        choleskyDecomposition.getL(), //
        DiagonalMatrix.with(choleskyDecomposition.diagonal()), //
        Transpose.of(choleskyDecomposition.getL()));
    assertEquals(result, matrix);
    Scalar one = GaussScalar.of(1, prime);
    Tensor id = DiagonalMatrix.of(n, one);
    Tensor res1 = choleskyDecomposition.solve(id);
    Tensor res2 = Inverse.of(matrix, Pivots.FIRST_NON_ZERO);
    assertEquals(res1, res2);
  }

  @Test
  void testDecimalScalar() {
    Tensor matrix = HilbertMatrix.of(5).map(N.DECIMAL128);
    CholeskyDecomposition choleskyDecomposition = CholeskyDecomposition.of(matrix);
    Tensor result = Dot.of( //
        choleskyDecomposition.getL(), //
        DiagonalMatrix.with(choleskyDecomposition.diagonal()), //
        Transpose.of(choleskyDecomposition.getL()));
    assertInstanceOf(DecimalScalar.class, result.Get(3, 3));
    Tolerance.CHOP.requireClose(matrix, result);
  }

  @Test
  void testPackageVisibility() {
    assertTrue(Modifier.isPublic(CholeskyDecomposition.class.getModifiers()));
    assertFalse(Modifier.isPublic(CholeskyDecompositionImpl.class.getModifiers()));
  }
}
