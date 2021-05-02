// code by jph
package ch.alpine.tensor.mat.sv;

import java.util.Arrays;

import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.alg.Dimensions;
import junit.framework.TestCase;

public class PrincipalComponentsTest extends TestCase {
  public void testSimple() {
    Tensor tensor = PrincipalComponents.of(Tensors.fromString("{{2, -5}, {3, 0}, {2, 5}, {2, 0}}"));
    assertEquals(Dimensions.of(tensor), Arrays.asList(4, 2));
  }
}
