// code by jph
package ch.alpine.tensor.lie;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Arrays;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.alg.Array;
import ch.alpine.tensor.alg.Dimensions;

class KhatriRaoTest {
  @Test
  public void testSimple() {
    Tensor a = Tensors.fromString("{{7, 7, 7}, {8, 8, 8}, {9, 9, 9}}");
    Tensor b = Tensors.fromString("{{1, 2, 3}, {4, 5, 6}}");
    Tensor tensor = KhatriRao.of(a, b);
    Tensor result = Tensors.fromString("{{7, 14, 21}, {28, 35, 42}, {8, 16, 24}, {32, 40, 48}, {9, 18, 27}, {36, 45, 54}}");
    assertEquals(tensor, result);
  }

  @Test
  public void testRank3() {
    Tensor tensor = KhatriRao.of(Array.zeros(3, 3, 3), Array.zeros(3, 3, 3));
    assertEquals(Dimensions.of(tensor), Arrays.asList(9, 3, 3));
  }
}
