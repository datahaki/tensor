// code by jph
package ch.alpine.tensor.opt.nd;

import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.ext.Serialization;
import junit.framework.TestCase;

public class NdCenterBaseTest extends TestCase {
  public void test1Norm() throws Exception {
    for (NdCenters ndCenters : NdCenters.values())
      Serialization.copy(ndCenters.apply(Tensors.vector(1, 2, 3)));
  }

  public void testInfinityNorm() throws Exception {
    NdCenterInterface ndCenterInterface = NdCenters.VECTOR_INFINITY_NORM.apply(Tensors.vector(1, 2, 3));
    Scalar distance = ndCenterInterface.distance(Tensors.vector(100, 100, 100));
    assertEquals(distance, RealScalar.of(99));
    Serialization.copy(ndCenterInterface);
  }
}
