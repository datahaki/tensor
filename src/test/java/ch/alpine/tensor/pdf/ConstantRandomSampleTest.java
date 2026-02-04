// code by jph
package ch.alpine.tensor.pdf;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.mat.HilbertMatrix;

class ConstantRandomSampleTest {
  @Test
  void testSimple() {
    Tensor tensor = HilbertMatrix.of(2, 3);
    RandomSampleInterface randomSampleInterface = new ConstantRandomSample(tensor);
    assertEquals(RandomSample.of(randomSampleInterface), tensor);
  }
}
