// code by jph
package ch.alpine.tensor.mat.sv;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.alg.Dimensions;
import ch.alpine.tensor.alg.OrderedQ;
import ch.alpine.tensor.alg.Reverse;
import ch.alpine.tensor.sca.Chop;

class PrincipalComponentsTest {
  @Test
  void testSimple() {
    Tensor tensor = PrincipalComponents.of(Tensors.fromString("{{2, -5}, {3, 0}, {2, 5}, {2, 0}}")).unscaled();
    assertEquals(Dimensions.of(tensor), Arrays.asList(4, 2));
  }

  @Test
  void testExample() {
    Tensor matrix = Tensors.fromString("{{1, 2}, {2, 3}, {4, 10}}");
    Tensor expect = Tensors.fromString("{{3.27053, 0.285293}, {1.99969, -0.335165}, {-5.27023, 0.0498715}}");
    PrincipalComponents pc = PrincipalComponents.of(matrix);
    Tensor unscaled = pc.unscaled();
    boolean status = Chop._04.isClose(unscaled, expect) //
        || Chop._04.isClose(unscaled, expect.negate());
    assertTrue(status);
    Tensor values = pc.svd().values();
    OrderedQ.require(Reverse.of(values));
  }
}
