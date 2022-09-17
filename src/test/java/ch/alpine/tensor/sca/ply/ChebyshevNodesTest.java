// code by jph
package ch.alpine.tensor.sca.ply;

import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.RepetitionInfo;

import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.mat.SymmetricMatrixQ;

class ChebyshevNodesTest {
  @RepeatedTest(6)
  void testSymmetric(RepetitionInfo repetitionInfo) {
    Tensor matrix = ChebyshevNodes._0.matrix(repetitionInfo.getCurrentRepetition());
    SymmetricMatrixQ.require(matrix);
  }
}
