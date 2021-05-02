// code by jph
package ch.alpine.tensor.opt.nd;

import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.ext.Serialization;
import junit.framework.TestCase;

public class EuclideanNdCenterTest extends TestCase {
  public void testSerializable() throws Exception {
    NdCenterInterface ndCenterInterface = EuclideanNdCenter.of(Tensors.vector(1, 2, 3));
    Serialization.copy(ndCenterInterface);
  }
}
