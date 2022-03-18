// code by jph
package ch.alpine.tensor.opt.nd;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.Scalars;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.alg.Array;
import ch.alpine.tensor.ext.Serialization;

public class NdCentersTest {
  @Test
  public void testSimple() {
    CoordinateBoundingBox outer = CoordinateBounds.of(Tensors.vector(0.1, 0.3), Tensors.vector(2.4, 3.5));
    CoordinateBoundingBox inner = CoordinateBounds.of(Tensors.vector(1, 1), Tensors.vector(2, 3));
    for (NdCenters ndCenters : NdCenters.values())
      for (int count = 0; count < 100; ++count) {
        NdCenterInterface ndCenterInterface = ndCenters.apply(TestHelper.sample(outer));
        Tensor point = TestHelper.sample(inner);
        assertTrue(Scalars.lessEquals(ndCenterInterface.distance(inner), ndCenterInterface.distance(point)));
      }
  }

  @Test
  public void testQuantity() {
    CoordinateBoundingBox outer = CoordinateBounds.of(Tensors.fromString("{0.1[m], 0.3[m]}"), Tensors.fromString("{2.4[m], 2.7[m]}"));
    CoordinateBoundingBox inner = CoordinateBounds.of(Tensors.fromString("{1[m], 1[m]}"), Tensors.fromString("{2[m], 3[m]}"));
    for (NdCenters ndCenters : NdCenters.values())
      for (int count = 0; count < 100; ++count) {
        NdCenterInterface ndCenterInterface = ndCenters.apply(TestHelper.sample(outer));
        Tensor point = TestHelper.sample(inner);
        assertTrue(Scalars.lessEquals(ndCenterInterface.distance(inner), ndCenterInterface.distance(point)));
      }
  }

  @Test
  public void testZero() {
    CoordinateBoundingBox inner = CoordinateBounds.of(Tensors.fromString("{1[m], 1[m]}"), Tensors.fromString("{2[m], 3[m]}"));
    for (NdCenters ndCenters : NdCenters.values())
      for (int count = 0; count < 10; ++count) {
        NdCenterInterface ndCenterInterface = ndCenters.apply(TestHelper.sample(inner));
        assertTrue(Scalars.isZero(ndCenterInterface.distance(inner)));
      }
  }

  @Test
  public void testSerializable() throws ClassNotFoundException, IOException {
    for (NdCenters ndCenters : NdCenters.values())
      Serialization.copy(ndCenters);
  }

  @Test
  public void testSerializableIf() throws ClassNotFoundException, IOException {
    for (NdCenters ndCenters : NdCenters.values())
      Serialization.copy(ndCenters.apply(Array.zeros(3)));
  }

  @Test
  public void testNullFail() {
    for (NdCenters ndCenters : NdCenters.values())
      assertThrows(NullPointerException.class, () -> ndCenters.apply(null));
  }
}
