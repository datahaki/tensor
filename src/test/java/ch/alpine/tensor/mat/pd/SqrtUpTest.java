// code by jph
package ch.alpine.tensor.mat.pd;

import java.util.Random;

import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.RepetitionInfo;

import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.mat.PositiveSemidefiniteMatrixQ;
import ch.alpine.tensor.mat.UnitaryMatrixQ;
import ch.alpine.tensor.pdf.ComplexNormalDistribution;
import ch.alpine.tensor.pdf.RandomVariate;
import ch.alpine.tensor.pdf.c.NormalDistribution;
import ch.alpine.tensor.sca.Chop;

class SqrtUpTest {
  @RepeatedTest(5)
  void testRectangle(RepetitionInfo repetitionInfo) {
    Random random = new Random(2);
    int n = repetitionInfo.getCurrentRepetition();
    Tensor matrix = RandomVariate.of(NormalDistribution.standard(), random, n, n);
    PolarDecomposition polarDecomposition = SqrtUp.of(matrix);
    Tensor s = polarDecomposition.getPositiveSemidefinite();
    Tensor u = polarDecomposition.getUnitary();
    Tensor tensor = polarDecomposition.getUnitary().dot(s);
    PositiveSemidefiniteMatrixQ.ofHermitian(s);
    UnitaryMatrixQ.require(u);
    Chop._10.requireClose(tensor, matrix);
  }

  @RepeatedTest(5)
  void testRectangleComplex(RepetitionInfo repetitionInfo) {
    Random random = new Random(2);
    int n = repetitionInfo.getCurrentRepetition();
    Tensor matrix = RandomVariate.of(ComplexNormalDistribution.STANDARD, random, n, n);
    PolarDecomposition polarDecomposition = SqrtUp.of(matrix);
    Tensor s = polarDecomposition.getPositiveSemidefinite();
    Tensor u = polarDecomposition.getUnitary();
    Tensor tensor = polarDecomposition.getUnitary().dot(s);
    PositiveSemidefiniteMatrixQ.ofHermitian(s);
    UnitaryMatrixQ.require(u);
    Chop._10.requireClose(tensor, matrix);
  }
}
