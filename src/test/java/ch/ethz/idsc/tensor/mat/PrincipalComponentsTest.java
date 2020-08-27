// code by jph
package ch.ethz.idsc.tensor.mat;

import java.util.Arrays;

import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Dimensions;
import junit.framework.TestCase;

public class PrincipalComponentsTest extends TestCase {
  public void testSimple() {
    Tensor tensor = PrincipalComponents.of(Tensors.fromString("{{2, -5}, {3, 0}, {2, 5}, {2, 0}}"));
    assertEquals(Dimensions.of(tensor), Arrays.asList(4, 2));
  }
}
