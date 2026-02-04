// code by jph
package ch.alpine.tensor.mat.ev;

import java.util.Random;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.mat.IdentityMatrix;
import ch.alpine.tensor.mat.Tolerance;
import ch.alpine.tensor.pdf.ComplexNormalDistribution;
import ch.alpine.tensor.pdf.RandomVariate;
import ch.alpine.tensor.pdf.c.NormalDistribution;
import test.EigensystemQ;

class SimpleEigensystemTest {
  @ParameterizedTest
  @ValueSource(ints = { 1, 2, 3, 4, 5 })
  void testRandomReal(int n) {
    Tensor matrix = RandomVariate.of(NormalDistribution.standard(), n, n);
    RealEigensystem eigensystem = new RealEigensystem(matrix);
    new EigensystemQ(matrix).require(eigensystem);
  }

  @ParameterizedTest
  @ValueSource(ints = { 1, 2, 3, 4, 5 })
  void testRandomComplex(int n) {
    Random random = new Random(13);
    Tensor matrix = RandomVariate.of(ComplexNormalDistribution.STANDARD, random, n, n);
    new EigensystemQ(matrix).require(SimpleEigensystem.of(matrix), Tolerance.CHOP);
  }

  @ParameterizedTest
  @ValueSource(ints = { 1, 2, 3 })
  void testIdentity(int n) {
    Tensor matrix = IdentityMatrix.of(n);
    new EigensystemQ(matrix).require(SimpleEigensystem.of(matrix), Tolerance.CHOP);
  }
}
