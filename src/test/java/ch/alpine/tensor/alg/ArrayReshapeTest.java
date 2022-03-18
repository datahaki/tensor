// code by jph
package ch.alpine.tensor.alg;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.num.Pi;
import ch.alpine.tensor.usr.AssertFail;

public class ArrayReshapeTest {
  @Test
  public void testReshape() {
    Tensor s = Tensors.vector(1, 2, 3, 4, 5, 6);
    Tensor r = ArrayReshape.of(s, 2, 3, 1);
    assertEquals(r.toString(), "{{{1}, {2}, {3}}, {{4}, {5}, {6}}}");
  }

  @Test
  public void testScalar() {
    assertEquals(ArrayReshape.of(Pi.VALUE, 1), Tensors.of(Pi.VALUE));
  }

  @Test
  public void testNonScalar() {
    Tensor tensor = Tensors.fromString("{{1}, {2}, {3}, {4}, {5}, {6}}");
    List<Integer> tail = Dimensions.of(tensor);
    assertEquals(tail, Arrays.asList(6, 1));
    Tensor result = ArrayReshape.of(tensor.stream(), 2, 3);
    List<Integer> list = Dimensions.of(result);
    assertEquals(list, Arrays.asList(2, 3, 1));
  }

  @Test
  public void testFail() {
    Tensor s = Tensors.vector(1, 2, 3, 4, 5, 6);
    ArrayReshape.of(s, 2, 3);
    AssertFail.of(() -> ArrayReshape.of(s, 3, 3));
  }
}
