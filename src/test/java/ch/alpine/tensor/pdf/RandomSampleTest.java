package ch.alpine.tensor.pdf;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Random;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.alg.VectorQ;
import ch.alpine.tensor.mat.MatrixQ;
import ch.alpine.tensor.opt.nd.BoxRandomSample;
import ch.alpine.tensor.opt.nd.CoordinateBoundingBox;
import ch.alpine.tensor.sca.Clips;

class RandomSampleTest {
  @Test
  void test() {
    BoxRandomSample boxRandomSample = new BoxRandomSample(CoordinateBoundingBox.of(Clips.unit(), Clips.unit(), Clips.unit()));
    VectorQ.requireLength(RandomSample.of(boxRandomSample), 3);
    assertEquals(RandomSample.of(boxRandomSample, new Random(3)), RandomSample.of(boxRandomSample, new Random(3)));
    Tensor matrix = RandomSample.of(boxRandomSample, 5);
    MatrixQ.requireSize(matrix, 5, 3);
    Tensor more = Tensor.of(RandomSample.stream(boxRandomSample).limit(10));
    MatrixQ.requireSize(more, 10, 3);
  }
}
