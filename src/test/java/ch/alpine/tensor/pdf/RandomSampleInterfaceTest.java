// code by jph
package ch.alpine.tensor.pdf;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.RepetitionInfo;
import org.junit.jupiter.api.Test;

import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.mat.ex.MatrixExp;
import ch.alpine.tensor.mat.re.Inverse;
import ch.alpine.tensor.pdf.c.NormalDistribution;

class RandomSampleInterfaceTest {
  @Test
  void test() {
    RandomSampleInterface array = RandomVariate.array(NormalDistribution.standard());
    Tensor tensor = RandomSample.of(array);
    assertTrue(tensor instanceof Scalar);
  }

  @RepeatedTest(5)
  void testAndThen(RepetitionInfo repetitionInfo) {
    int n = repetitionInfo.getCurrentRepetition();
    RandomSampleInterface rsi = RandomVariate.array(NormalDistribution.standard(), n, n).andThen(MatrixExp::of);
    Inverse.of(RandomSample.of(rsi));
  }
}
