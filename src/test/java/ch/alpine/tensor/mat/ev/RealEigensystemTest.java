// code by jph
package ch.alpine.tensor.mat.ev;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.util.Random;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.alg.Array;
import ch.alpine.tensor.alg.Transpose;
import ch.alpine.tensor.mat.AntisymmetricMatrixQ;
import ch.alpine.tensor.mat.DiagonalMatrix;
import ch.alpine.tensor.mat.IdentityMatrix;
import ch.alpine.tensor.mat.OrthogonalMatrixQ;
import ch.alpine.tensor.nrm.MatrixInfinityNorm;
import ch.alpine.tensor.pdf.Distribution;
import ch.alpine.tensor.pdf.RandomVariate;
import ch.alpine.tensor.pdf.c.NormalDistribution;
import ch.alpine.tensor.pdf.c.UniformDistribution;
import ch.alpine.tensor.qty.Quantity;
import ch.alpine.tensor.sca.Chop;
import test.wrap.EigensystemQ;

class RealEigensystemTest {
  @ParameterizedTest
  @ValueSource(ints = { 1, 2, 3, 5, 6, 7, 10 })
  void testRandom(int n) {
    Tensor matrix = RandomVariate.of(NormalDistribution.standard(), n, n);
    RealEigensystem eigensystem = new RealEigensystem(matrix);
    new EigensystemQ(matrix).require(eigensystem);
  }

  @ParameterizedTest
  @ValueSource(ints = { 1, 2, 3, 5, 6, 7, 10 })
  void testRandomWithUnits(int n) {
    Tensor matrix = RandomVariate.of(NormalDistribution.of( //
        Quantity.of(0, "m"), //
        Quantity.of(1, "m")), n, n);
    RealEigensystem eigensystem = new RealEigensystem(matrix);
    new EigensystemQ(matrix).require(eigensystem);
  }

  @ParameterizedTest
  @ValueSource(ints = { 1, 2, 3, 5, 6, 7, 10 })
  void testRandomOfs(int n) {
    Tensor matrix = RandomVariate.of(NormalDistribution.of(3, 1), n, n);
    RealEigensystem eigensystem = new RealEigensystem(matrix);
    new EigensystemQ(matrix).require(eigensystem);
  }

  @ParameterizedTest
  @ValueSource(ints = { 1, 2, 3, 5, 6, 7, 10 })
  void testRandomEps(int n) {
    Tensor matrix = RandomVariate.of(NormalDistribution.of(0, 1e-10), n, n);
    RealEigensystem eigensystem = new RealEigensystem(matrix);
    new EigensystemQ(matrix).require(eigensystem, Chop._08);
  }

  @Disabled // schur decomp fails
  @ParameterizedTest
  @ValueSource(ints = { 1, 2, 3, 5, 6, 7, 10 })
  void testRandomEps2(int n) {
    Tensor matrix = RandomVariate.of(NormalDistribution.of(0, 1e-14), n, n);
    RealEigensystem eigensystem = new RealEigensystem(matrix);
    new EigensystemQ(matrix).require(eigensystem);
  }

  @ParameterizedTest
  @ValueSource(ints = { 1, 2, 3, 5, 6, 7, 10 })
  void testId(int n) {
    Tensor matrix = IdentityMatrix.of(n);
    RealEigensystem eigensystem = new RealEigensystem(matrix);
    new EigensystemQ(matrix).require(eigensystem);
  }

  @Test
  void test10() {
    Tensor matrix = DiagonalMatrix.of(1, 0, 0, 0, 1, 0, 0, 0, 0);
    RealEigensystem eigensystem = new RealEigensystem(matrix);
    new EigensystemQ(matrix).require(eigensystem);
  }

  @ParameterizedTest
  @ValueSource(ints = { 2, 3, 5, 6, 7, 10 })
  void testAntisymmetric(int n) {
    Tensor matrix = RandomVariate.of(NormalDistribution.standard(), n, n);
    matrix = Transpose.of(matrix).subtract(matrix);
    RealEigensystem eigensystem = new RealEigensystem(matrix);
    new EigensystemQ(matrix).require(eigensystem);
  }

  @ParameterizedTest
  @ValueSource(ints = { 8, 9 })
  void testDecimal(int n) {
    Distribution distribution = UniformDistribution.unit(40);
    Tensor ini = RandomVariate.of(distribution, new Random(3), n, n);
    // antisymm
    Tensor matrix = Transpose.of(ini).subtract(ini);
    assertTrue(AntisymmetricMatrixQ.INSTANCE.test(matrix));
    RealEigensystem realEigensystem = RealEigensystem.of(matrix);
    new EigensystemQ(matrix).require(realEigensystem);
    Tensor vectors = realEigensystem.vectors();
    Tensor values = realEigensystem.values();
    values.toString();
    Tensor diag = realEigensystem.diagonalMatrix();
    OrthogonalMatrixQ.INSTANCE.require(vectors);
    Tensor recons = Transpose.of(vectors).dot(diag).dot(vectors);
    Scalar err = MatrixInfinityNorm.of(matrix.subtract(recons));
    if (!Chop._20.isClose(matrix, recons)) {
      IO.println(err);
      fail();
    }
  }

  @ParameterizedTest
  @ValueSource(ints = { 1, 2, 3 })
  void testZeroFail(int n) {
    assertThrows(Exception.class, () -> new RealEigensystem(Array.zeros(n, n)));
  }
}
