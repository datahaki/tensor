// code by jph
package ch.alpine.tensor.mat.ev;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.alg.Transpose;
import ch.alpine.tensor.mat.IdentityMatrix;
import ch.alpine.tensor.mat.Tolerance;
import ch.alpine.tensor.pdf.ComplexNormalDistribution;
import ch.alpine.tensor.pdf.RandomVariate;
import ch.alpine.tensor.pdf.c.NormalDistribution;

class SimpleEigensystemTest {
  private static void _check(Tensor matrix, Eigensystem eigensystem) {
    Tensor values = eigensystem.diagonalMatrix();
    Tensor vectors = Transpose.of(eigensystem.vectors());
    Tensor lhs = matrix.dot(vectors);
    Tensor rhs = vectors.dot(values);
    Tolerance.CHOP.requireClose(lhs, rhs);
  }

  @ParameterizedTest
  @ValueSource(ints = { 1, 2, 3, 4, 5 })
  void testRandomReal(int n) {
    Tensor matrix = RandomVariate.of(NormalDistribution.standard(), n, n);
    _check(matrix, Eigensystem.of(matrix));
  }

  @ParameterizedTest
  @ValueSource(ints = { 1, 2, 3, 4, 5 })
  void testRandomComplex(int n) {
    Tensor matrix = RandomVariate.of(ComplexNormalDistribution.STANDARD, n, n);
    _check(matrix, new SimpleEigensystem(matrix));
  }

  @ParameterizedTest
  @ValueSource(ints = { 1, 2, 3 })
  void testIdentity(int n) {
    Tensor matrix = IdentityMatrix.of(n);
    _check(matrix, new SimpleEigensystem(matrix));
  }
}
