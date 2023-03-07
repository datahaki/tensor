// code by jph
package ch.alpine.tensor.mat.ev;

import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.alg.Array;
import ch.alpine.tensor.alg.Dot;
import ch.alpine.tensor.alg.Transpose;
import ch.alpine.tensor.mat.DiagonalMatrix;
import ch.alpine.tensor.mat.IdentityMatrix;
import ch.alpine.tensor.mat.Tolerance;
import ch.alpine.tensor.mat.UnitaryMatrixQ;
import ch.alpine.tensor.pdf.RandomVariate;
import ch.alpine.tensor.pdf.c.NormalDistribution;
import ch.alpine.tensor.sca.Chop;

class RealEigensystemTest {
  @ParameterizedTest
  @ValueSource(ints = { 1, 2, 3, 5, 6, 7, 10 })
  void testRandom(int n) {
    Tensor matrix = RandomVariate.of(NormalDistribution.standard(), n, n);
    Eigensystem eigensystem = new RealEigensystem(matrix);
    Tensor v = Transpose.of(eigensystem.vectors());
    Tensor d = eigensystem.diagonalMatrix();
    // A*V = V*D
    Tensor lhs = Dot.of(matrix, v);
    Tensor rhs = Dot.of(v, d);
    Tolerance.CHOP.requireClose(lhs, rhs);
  }

  @ParameterizedTest
  @ValueSource(ints = { 1, 2, 3, 5, 6, 7, 10 })
  void testRandomOfs(int n) {
    Tensor matrix = RandomVariate.of(NormalDistribution.of(10, 1), n, n);
    Eigensystem eigensystem = new RealEigensystem(matrix);
    Tensor v = Transpose.of(eigensystem.vectors());
    Tensor d = eigensystem.diagonalMatrix();
    // A*V = V*D
    Tensor lhs = Dot.of(matrix, v);
    Tensor rhs = Dot.of(v, d);
    Tolerance.CHOP.requireClose(lhs, rhs);
  }

  @ParameterizedTest
  @ValueSource(ints = { 1, 2, 3, 5, 6, 7, 10 })
  void testRandomEps(int n) {
    Tensor matrix = RandomVariate.of(NormalDistribution.of(0, 1e-12), n, n);
    Eigensystem eigensystem = new RealEigensystem(matrix);
    Tensor v = Transpose.of(eigensystem.vectors());
    Tensor d = eigensystem.diagonalMatrix();
    // A*V = V*D
    Tensor lhs = Dot.of(matrix, v);
    Tensor rhs = Dot.of(v, d);
    Chop._06.requireClose(lhs, rhs);
  }

  @Disabled // schur decomp fails
  @ParameterizedTest
  @ValueSource(ints = { 1, 2, 3, 5, 6, 7, 10 })
  void testRandomEps2(int n) {
    Tensor matrix = RandomVariate.of(NormalDistribution.of(0, 1e-14), n, n);
    Eigensystem eigensystem = new RealEigensystem(matrix);
    Tensor v = Transpose.of(eigensystem.vectors());
    Tensor d = eigensystem.diagonalMatrix();
    // A*V = V*D
    Tensor lhs = Dot.of(matrix, v);
    Tensor rhs = Dot.of(v, d);
    Chop._06.requireClose(lhs, rhs);
  }

  @ParameterizedTest
  @ValueSource(ints = { 1, 2, 3, 5, 6, 7, 10 })
  void testId(int n) {
    Tensor matrix = IdentityMatrix.of(n);
    Eigensystem eigensystem = new RealEigensystem(matrix);
    Tensor v = Transpose.of(eigensystem.vectors());
    Tensor d = eigensystem.diagonalMatrix();
    // A*V = V*D
    Tensor lhs = Dot.of(matrix, v);
    Tensor rhs = Dot.of(v, d);
    Tolerance.CHOP.requireClose(lhs, rhs);
    UnitaryMatrixQ.require(v);
  }

  @Test
  void test10() {
    Tensor matrix = DiagonalMatrix.of(1, 0, 0, 0, 1, 0, 0, 0, 0);
    Eigensystem eigensystem = new RealEigensystem(matrix);
    Tensor v = Transpose.of(eigensystem.vectors());
    Tensor d = eigensystem.diagonalMatrix();
    // A*V = V*D
    Tensor lhs = Dot.of(matrix, v);
    Tensor rhs = Dot.of(v, d);
    Tolerance.CHOP.requireClose(lhs, rhs);
    UnitaryMatrixQ.require(v);
  }

  @ParameterizedTest
  @ValueSource(ints = { 2, 3, 5, 6, 7, 10 })
  void testAntisymmetric(int n) {
    Tensor matrix = RandomVariate.of(NormalDistribution.standard(), n, n);
    matrix = Transpose.of(matrix).subtract(matrix);
    Eigensystem eigensystem = new RealEigensystem(matrix);
    Tensor v = Transpose.of(eigensystem.vectors());
    Tensor d = eigensystem.diagonalMatrix();
    // A*V = V*D
    Tensor lhs = Dot.of(matrix, v);
    Tensor rhs = Dot.of(v, d);
    Tolerance.CHOP.requireClose(lhs, rhs);
    UnitaryMatrixQ.require(v);
    // System.out.println(eigensystem.values());
  }

  @ParameterizedTest
  @ValueSource(ints = { 1, 2, 3 })
  void testZeroFail(int n) {
    assertThrows(Exception.class, () -> new RealEigensystem(Array.zeros(n, n)));
  }
}
