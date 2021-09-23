// code by jph
package ch.alpine.tensor.opt.nd;

import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.ext.Serialization;
import junit.framework.TestCase;

public class NdCenterBaseTest extends TestCase {
  public void test1Norm() throws Exception {
    NdCenterInterface ndCenterInterface = NdCenterBase.of1Norm(Tensors.vector(1, 2, 3));
    Serialization.copy(ndCenterInterface);
  }

  public void test2Norm() throws Exception {
    NdCenterInterface ndCenterInterface = NdCenterBase.of2Norm(Tensors.vector(1, 2, 3));
    Serialization.copy(ndCenterInterface);
  }

  public void testInfinityNorm() throws Exception {
    NdCenterInterface ndCenterInterface = NdCenterBase.ofInfinityNorm(Tensors.vector(1, 2, 3));
    Scalar distance = ndCenterInterface.distance(Tensors.vector(100, 100, 100));
    assertEquals(distance, RealScalar.of(99));
    Serialization.copy(ndCenterInterface);
  }
}
