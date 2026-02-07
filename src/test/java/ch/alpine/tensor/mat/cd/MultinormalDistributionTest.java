// code by jph
package ch.alpine.tensor.mat.cd;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.alg.VectorQ;
import ch.alpine.tensor.pdf.RandomSample;
import ch.alpine.tensor.pdf.RandomSampleInterface;

class MultinormalDistributionTest {
  @Test
  void testRosetta1() {
    // +5 0 0
    // +3 3 0
    // -1 1 3
    // {{5, 3, -1}, {0, 3, 1}, {0, 0, 3}}
    Tensor sigma = Tensors.matrix(new Number[][] { //
        { 25, 15, -5 }, //
        { 15, 18, 0 }, //
        { -5, 0, 11 } //
    });
    RandomSampleInterface randomSampleInterface = MultinormalDistribution.of(sigma);
    Tensor vector = RandomSample.of(randomSampleInterface);
    VectorQ.requireLength(vector, 3);
  }
}
