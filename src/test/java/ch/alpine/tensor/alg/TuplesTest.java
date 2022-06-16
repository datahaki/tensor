// code by jph
package ch.alpine.tensor.alg;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.num.Pi;

class TuplesTest {
  @Test
  void testSimple0() {
    Tensor tuples = Tuples.of(Tensors.vector(3, 4, 5), 0);
    assertEquals(tuples, Tensors.empty());
  }

  @Test
  void testOne() {
    Tensor tuples = Tuples.of(Tensors.vector(3, 4, 5), 1);
    Tensor actual = Tensors.fromString("{{3}, {4}, {5}}");
    assertEquals(tuples, actual);
  }

  @Test
  void testTwo() {
    Tensor tuples = Tuples.of(Tensors.vector(3, 4, 5), 2);
    Tensor actual = //
        Tensors.fromString("{{3, 3}, {3, 4}, {3, 5}, {4, 3}, {4, 4}, {4, 5}, {5, 3}, {5, 4}, {5, 5}}");
    assertEquals(tuples, actual);
  }

  @Test
  void testThree() {
    Tensor tuples = Tuples.of(Tensors.vector(4, 5), 3);
    Tensor actual = //
        Tensors.fromString("{{4, 4, 4}, {4, 4, 5}, {4, 5, 4}, {4, 5, 5}, {5, 4, 4}, {5, 4, 5}, {5, 5, 4}, {5, 5, 5}}");
    assertEquals(tuples, actual);
  }

  @Test
  void testFive() {
    Tensor tensor = Tuples.of(Range.of(0, 5), 2);
    Tensor result = Tensor.of(tensor.stream().filter(OrderedQ::of));
    assertEquals(result.length(), 15);
  }

  @Test
  void testFailNegative() {
    assertThrows(IllegalArgumentException.class, () -> Tuples.of(Tensors.vector(1, 2, 3), -1));
  }

  @Test
  void testFailScalar() {
    assertThrows(IllegalArgumentException.class, () -> Tuples.of(Pi.VALUE, 2));
  }
}
