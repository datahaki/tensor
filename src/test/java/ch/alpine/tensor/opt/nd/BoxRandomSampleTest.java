// code by jph
package ch.alpine.tensor.opt.nd;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalars;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.alg.Dimensions;
import ch.alpine.tensor.alg.VectorQ;
import ch.alpine.tensor.nrm.Vector2Norm;
import ch.alpine.tensor.pdf.RandomSample;
import ch.alpine.tensor.pdf.RandomSampleInterface;
import ch.alpine.tensor.red.Mean;
import ch.alpine.tensor.sca.Clips;
import test.wrap.SerializableQ;

class BoxRandomSampleTest {
  @Test
  void testSimple3D() {
    Tensor offset = Tensors.vector(2, 2, 3);
    Tensor width = Tensors.vector(1, 1, 1);
    RandomSampleInterface randomSampleInterface = new BoxRandomSample(CoordinateBounds.of(offset.subtract(width), offset.add(width)));
    Tensor samples = RandomSample.of(randomSampleInterface, 100);
    Scalars.compare(Vector2Norm.of(Mean.of(samples).subtract(offset)), RealScalar.of(0.1));
    assertEquals(Dimensions.of(samples), Arrays.asList(100, 3));
  }

  @Test
  void testSingle() {
    Tensor offset = Tensors.vector(2, 2, 3);
    Tensor width = Tensors.vector(1, 1, 1);
    RandomSampleInterface randomSampleInterface = new BoxRandomSample(CoordinateBounds.of(offset.subtract(width), offset.add(width)));
    Tensor rand = RandomSample.of(randomSampleInterface);
    assertEquals(Dimensions.of(rand), List.of(3));
  }

  @Test
  void testSerializable() {
    CoordinateBoundingBox cbb = CoordinateBoundingBox.of( //
        Clips.interval(1, 3), Clips.interval(2, 4), Clips.interval(3, 8));
    RandomSampleInterface randomSampleInterface = new BoxRandomSample(cbb);
    SerializableQ.require(randomSampleInterface);
    for (Tensor tensor : RandomSample.of(randomSampleInterface, 10)) {
      VectorQ.requireLength(tensor, 3);
      assertTrue(cbb.test(tensor));
    }
  }

  @Test
  void testDimensionFail() {
    assertThrows(Exception.class, () -> new BoxRandomSample(CoordinateBounds.of(Tensors.vector(1, 2), Tensors.vector(1, 2, 3))));
  }

  @Test
  void testSignFail() {
    assertThrows(Exception.class, () -> new BoxRandomSample(CoordinateBounds.of(Tensors.vector(1, 2), Tensors.vector(2, 1))));
  }
}
