// code by jph
package ch.alpine.tensor.mat.pd;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.RepetitionInfo;
import org.junit.jupiter.api.Test;

import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.mat.HilbertMatrix;
import ch.alpine.tensor.mat.PositiveSemidefiniteMatrixQ;
import ch.alpine.tensor.mat.UnitaryMatrixQ;
import ch.alpine.tensor.pdf.ComplexNormalDistribution;
import ch.alpine.tensor.pdf.RandomVariate;
import ch.alpine.tensor.pdf.c.NormalDistribution;

class SqrtPuTest {
  @Test
  void testSimple() {
    PolarDecompositionSqrt sqrtPu = SqrtPu.of(HilbertMatrix.of(3));
    UnitaryMatrixQ.require(sqrtPu.getUnitary());
    assertTrue(PositiveSemidefiniteMatrixQ.ofHermitian(sqrtPu.getPositiveSemidefinite()));
  }

  @RepeatedTest(5)
  void testReal1(RepetitionInfo repetitionInfo) {
    int n = repetitionInfo.getCurrentRepetition();
    Tensor matrix = RandomVariate.of(NormalDistribution.standard(), n, n + 2);
    PolarDecompositionSqrt sqrtPu = SqrtPu.of(matrix);
    UnitaryMatrixQ.require(sqrtPu.getUnitary());
    assertTrue(PositiveSemidefiniteMatrixQ.ofHermitian(sqrtPu.getPositiveSemidefinite()));
  }

  @RepeatedTest(5)
  void testComplex(RepetitionInfo repetitionInfo) {
    int n = repetitionInfo.getCurrentRepetition();
    Tensor matrix = RandomVariate.of(ComplexNormalDistribution.STANDARD, n, n);
    PolarDecompositionSqrt sqrtPu = SqrtPu.of(matrix);
    UnitaryMatrixQ.require(sqrtPu.getUnitary());
    assertTrue(PositiveSemidefiniteMatrixQ.ofHermitian(sqrtPu.getPositiveSemidefinite()));
  }

  @RepeatedTest(5)
  void testComplex1(RepetitionInfo repetitionInfo) {
    int n = repetitionInfo.getCurrentRepetition();
    Tensor matrix = RandomVariate.of(ComplexNormalDistribution.STANDARD, n, n + 2);
    PolarDecompositionSqrt sqrtPu = SqrtPu.of(matrix);
    UnitaryMatrixQ.require(sqrtPu.getUnitary());
    assertTrue(PositiveSemidefiniteMatrixQ.ofHermitian(sqrtPu.getPositiveSemidefinite()));
  }
}
