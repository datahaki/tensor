// code by jph
package ch.alpine.tensor.alg;

import java.util.Arrays;
import java.util.List;

import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.usr.AssertFail;
import junit.framework.TestCase;

public class ArrayReshapeTest extends TestCase {
  public void testReshape() {
    Tensor s = Tensors.vector(1, 2, 3, 4, 5, 6);
    Tensor r = ArrayReshape.of(s, 2, 3, 1);
    assertEquals(r.toString(), "{{{1}, {2}, {3}}, {{4}, {5}, {6}}}");
  }

  public void testNonScalar() {
    Tensor tensor = Tensors.fromString("{{1}, {2}, {3}, {4}, {5}, {6}}");
    List<Integer> tail = Dimensions.of(tensor);
    assertEquals(tail, Arrays.asList(6, 1));
    Tensor result = ArrayReshape.of(tensor.stream(), 2, 3);
    List<Integer> list = Dimensions.of(result);
    assertEquals(list, Arrays.asList(2, 3, 1));
  }

  public void testFail() {
    Tensor s = Tensors.vector(1, 2, 3, 4, 5, 6);
    ArrayReshape.of(s, 2, 3);
    AssertFail.of(() -> ArrayReshape.of(s, 3, 3));
  }
}
