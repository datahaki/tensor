// code by jph
package ch.alpine.tensor.mat.sv;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Arrays;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.alg.Dimensions;

public class PrincipalComponentsTest {
  @Test
  public void testSimple() {
    Tensor tensor = PrincipalComponents.of(Tensors.fromString("{{2, -5}, {3, 0}, {2, 5}, {2, 0}}"));
    assertEquals(Dimensions.of(tensor), Arrays.asList(4, 2));
  }
}
