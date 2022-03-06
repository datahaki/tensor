// code by jph
package ch.alpine.tensor.num;

import java.util.stream.IntStream;

import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import junit.framework.TestCase;

public class PrimeTest extends TestCase {
  public void testSimple() {
    Tensor tensor = Tensor.of(IntStream.range(1, 11).mapToObj(Prime::of));
    Tensor expect = Tensors.vector(2, 3, 5, 7, 11, 13, 17, 19, 23, 29);
    assertEquals(tensor, expect);
  }
}
