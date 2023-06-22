// code by jph
package ch.alpine.tensor.mat.ev;

import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Random;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.alg.Array;
import ch.alpine.tensor.alg.Transpose;
import ch.alpine.tensor.mat.DiagonalMatrix;
import ch.alpine.tensor.mat.IdentityMatrix;
import ch.alpine.tensor.mat.UnitaryMatrixQ;
import ch.alpine.tensor.pdf.RandomVariate;
import ch.alpine.tensor.pdf.c.NormalDistribution;
import ch.alpine.tensor.qty.Quantity;
import ch.alpine.tensor.sca.Chop;

class RealEigensystemTest {
  @ParameterizedTest
  @ValueSource(ints = { 1, 2, 3, 5, 6, 7, 10 })
  void testRandom(int n) {
    Tensor matrix = RandomVariate.of(NormalDistribution.standard(), n, n);
    Eigensystem eigensystem = new RealEigensystem(matrix);
    TestHelper.checkEquation(matrix, eigensystem);
  }

  @ParameterizedTest
  @ValueSource(ints = { 1, 2, 3, 5, 6, 7, 10 })
  void testRandomWithUnits(int n) {
    Tensor matrix = RandomVariate.of(NormalDistribution.of( //
        Quantity.of(0, "m"), //
        Quantity.of(1, "m")), n, n);
    Eigensystem eigensystem = new RealEigensystem(matrix);
    TestHelper.checkEquation(matrix, eigensystem);
  }

  @ParameterizedTest
  @ValueSource(ints = { 1, 2, 3, 5, 6, 7, 10 })
  void testRandomOfs(int n) {
    Tensor matrix = RandomVariate.of(NormalDistribution.of(10, 1), n, n);
    Eigensystem eigensystem = new RealEigensystem(matrix);
    TestHelper.checkEquation(matrix, eigensystem);
  }

  @ParameterizedTest
  @ValueSource(ints = { 1, 2, 3, 5, 6, 7, 10 })
  void testRandomEps(int n) {
    Random random = new Random(3);
    Tensor matrix = RandomVariate.of(NormalDistribution.of(0, 1e-12), random, n, n);
    Eigensystem eigensystem = new RealEigensystem(matrix);
    TestHelper.checkEquation(matrix, eigensystem, Chop._08);
  }

  @Disabled // schur decomp fails
  @ParameterizedTest
  @ValueSource(ints = { 1, 2, 3, 5, 6, 7, 10 })
  void testRandomEps2(int n) {
    Tensor matrix = RandomVariate.of(NormalDistribution.of(0, 1e-14), n, n);
    Eigensystem eigensystem = new RealEigensystem(matrix);
    TestHelper.checkEquation(matrix, eigensystem);
  }

  @ParameterizedTest
  @ValueSource(ints = { 1, 2, 3, 5, 6, 7, 10 })
  void testId(int n) {
    Tensor matrix = IdentityMatrix.of(n);
    Eigensystem eigensystem = new RealEigensystem(matrix);
    TestHelper.checkEquation(matrix, eigensystem);
    UnitaryMatrixQ.require(eigensystem.vectors());
  }

  @Test
  void test10() {
    Tensor matrix = DiagonalMatrix.of(1, 0, 0, 0, 1, 0, 0, 0, 0);
    Eigensystem eigensystem = new RealEigensystem(matrix);
    TestHelper.checkEquation(matrix, eigensystem);
    UnitaryMatrixQ.require(eigensystem.vectors());
  }

  @ParameterizedTest
  @ValueSource(ints = { 2, 3, 5, 6, 7, 10 })
  void testAntisymmetric(int n) {
    Tensor matrix = RandomVariate.of(NormalDistribution.standard(), n, n);
    matrix = Transpose.of(matrix).subtract(matrix);
    Eigensystem eigensystem = new RealEigensystem(matrix);
    TestHelper.checkEquation(matrix, eigensystem);
    UnitaryMatrixQ.require(eigensystem.vectors());
  }

  @ParameterizedTest
  @ValueSource(ints = { 1, 2, 3 })
  void testZeroFail(int n) {
    assertThrows(Exception.class, () -> new RealEigensystem(Array.zeros(n, n)));
  }
}
