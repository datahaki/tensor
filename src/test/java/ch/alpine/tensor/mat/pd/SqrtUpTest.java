// code by jph
package ch.alpine.tensor.mat.pd;

import java.util.Random;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.mat.PositiveSemidefiniteMatrixQ;
import ch.alpine.tensor.mat.UnitaryMatrixQ;
import ch.alpine.tensor.pdf.ComplexNormalDistribution;
import ch.alpine.tensor.pdf.RandomVariate;
import ch.alpine.tensor.pdf.c.NormalDistribution;
import ch.alpine.tensor.sca.Chop;

class SqrtUpTest {
  @ParameterizedTest
  @ValueSource(ints = { 1, 3, 4, 5 })
  void testRectangle(int n) {
    Random random = new Random(2);
    Tensor matrix = RandomVariate.of(NormalDistribution.standard(), random, n, n);
    PolarDecomposition polarDecomposition = SqrtUp.of(matrix);
    Tensor s = polarDecomposition.getPositiveSemidefinite();
    Tensor u = polarDecomposition.getUnitary();
    Tensor tensor = polarDecomposition.getUnitary().dot(s);
    PositiveSemidefiniteMatrixQ.ofHermitian(s);
    UnitaryMatrixQ.INSTANCE.require(u);
    Chop._10.requireClose(tensor, matrix);
  }

  @ParameterizedTest
  @ValueSource(ints = { 1, 3, 4, 5 })
  void testRectangleComplex(int n) {
    Random random = new Random(2);
    Tensor matrix = RandomVariate.of(ComplexNormalDistribution.STANDARD, random, n, n);
    PolarDecomposition polarDecomposition = SqrtUp.of(matrix);
    Tensor s = polarDecomposition.getPositiveSemidefinite();
    Tensor u = polarDecomposition.getUnitary();
    Tensor tensor = polarDecomposition.getUnitary().dot(s);
    PositiveSemidefiniteMatrixQ.ofHermitian(s);
    UnitaryMatrixQ.INSTANCE.require(u);
    Chop._10.requireClose(tensor, matrix);
  }
}
