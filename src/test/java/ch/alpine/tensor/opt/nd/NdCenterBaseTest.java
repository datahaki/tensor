// code by jph
package ch.alpine.tensor.opt.nd;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.ext.Serialization;

public class NdCenterBaseTest {
  @Test
  public void test1Norm() throws Exception {
    for (NdCenters ndCenters : NdCenters.values())
      Serialization.copy(ndCenters.apply(Tensors.vector(1, 2, 3)));
  }

  @Test
  public void testInfinityNorm() throws Exception {
    NdCenterInterface ndCenterInterface = NdCenters.VECTOR_INFINITY_NORM.apply(Tensors.vector(1, 2, 3));
    Scalar distance = ndCenterInterface.distance(Tensors.vector(100, 100, 100));
    assertEquals(distance, RealScalar.of(99));
    Serialization.copy(ndCenterInterface);
  }
}
