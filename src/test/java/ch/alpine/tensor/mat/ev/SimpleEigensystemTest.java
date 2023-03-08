// code by jph
package ch.alpine.tensor.mat.ev;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.mat.IdentityMatrix;
import ch.alpine.tensor.pdf.ComplexNormalDistribution;
import ch.alpine.tensor.pdf.RandomVariate;
import ch.alpine.tensor.pdf.c.NormalDistribution;

class SimpleEigensystemTest {
  @ParameterizedTest
  @ValueSource(ints = { 1, 2, 3, 4, 5 })
  void testRandomReal(int n) {
    Tensor matrix = RandomVariate.of(NormalDistribution.standard(), n, n);
    TestHelper.checkEquation(matrix, Eigensystem.of(matrix));
  }

  @ParameterizedTest
  @ValueSource(ints = { 1, 2, 3, 4, 5 })
  void testRandomComplex(int n) {
    Tensor matrix = RandomVariate.of(ComplexNormalDistribution.STANDARD, n, n);
    TestHelper.checkEquation(matrix, new SimpleEigensystem(matrix));
  }

  @ParameterizedTest
  @ValueSource(ints = { 1, 2, 3 })
  void testIdentity(int n) {
    Tensor matrix = IdentityMatrix.of(n);
    TestHelper.checkEquation(matrix, new SimpleEigensystem(matrix));
  }
}
