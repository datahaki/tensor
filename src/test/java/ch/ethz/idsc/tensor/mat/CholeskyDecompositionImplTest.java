// code by jph
package ch.ethz.idsc.tensor.mat;

import java.io.IOException;
import java.lang.reflect.Modifier;
import java.util.Random;

import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Dot;
import ch.ethz.idsc.tensor.alg.Transpose;
import ch.ethz.idsc.tensor.ext.Serialization;
import ch.ethz.idsc.tensor.num.GaussScalar;
import junit.framework.TestCase;

public class CholeskyDecompositionImplTest extends TestCase {
  public void testSolveQuantity() throws ClassNotFoundException, IOException {
    Tensor matrix = Tensors.fromString( //
        "{{60[m^2], 30[m*rad], 20[kg*m]}, {30[m*rad], 20[rad^2], 15[kg*rad]}, {20[kg*m], 15[kg*rad], 12[kg^2]}}");
    CholeskyDecomposition choleskyDecomposition = //
        Serialization.copy(CholeskyDecomposition.of(matrix));
    assertEquals( //
        choleskyDecomposition.solve(IdentityMatrix.of(3)), //
        Inverse.of(matrix));
    assertTrue(choleskyDecomposition.toString().startsWith("CholeskyDecomposition"));
  }

  public void testGaussScalar() throws ClassNotFoundException, IOException {
    int n = 7;
    int prime = 7879;
    Random random = new Random();
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

  public void testPackageVisibility() {
    assertTrue(Modifier.isPublic(CholeskyDecomposition.class.getModifiers()));
    assertFalse(Modifier.isPublic(CholeskyDecompositionImpl.class.getModifiers()));
  }
}
