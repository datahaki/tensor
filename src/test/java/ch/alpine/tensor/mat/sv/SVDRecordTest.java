// code by jph
package ch.alpine.tensor.mat.sv;

import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.RepetitionInfo;

import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.pdf.RandomVariate;
import ch.alpine.tensor.pdf.c.NormalDistribution;

class SVDRecordTest {
  @RepeatedTest(4)
  void test(RepetitionInfo repetitionInfo) {
    int k = repetitionInfo.getCurrentRepetition();
    Tensor matrix = RandomVariate.of(NormalDistribution.standard(), 10, k);
    SingularValueDecomposition svd = SingularValueDecomposition.of(matrix);
    SingularValueDecompositionWrap.of(matrix, svd);
    SingularValueDecomposition dec = svd.decreasing();
    SingularValueDecompositionWrap.of(matrix, dec);
  }
}
