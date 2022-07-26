// code by jph
package ch.alpine.tensor.red;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Collections;
import java.util.Map;
import java.util.NavigableMap;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.DoubleScalar;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.Throw;

class TallyTest {
  @Test
  void testSome() {
    Tensor tensor = Tensors.vector(4, 2, 3, 7, 2, 5, 4, 2, 2, 5);
    Map<Tensor, Long> map = Tally.of(tensor);
    assertEquals((long) map.get(RealScalar.of(2)), 4);
    assertEquals((long) map.get(RealScalar.of(4)), 2);
    assertEquals((long) map.get(RealScalar.of(5)), 2);
  }

  @Test
  void testEmpty() {
    Map<Tensor, Long> map = Tally.of(Tensors.empty());
    assertEquals(map, Collections.emptyMap());
  }

  @Test
  void testStreamScalar() {
    Tensor tensor = Tensors.vector(4, 2, 3, 7, 2, 5, 4, 2, 2, 5);
    Map<Scalar, Long> map = Tally.of(tensor.stream().map(Scalar.class::cast));
    assertEquals((long) map.get(RealScalar.of(2)), 4);
    assertEquals((long) map.get(RealScalar.of(4)), 2);
    assertEquals((long) map.get(RealScalar.of(5)), 2);
  }

  @Test
  void testInfty() {
    Tensor tensor = Tensors.of( //
        DoubleScalar.POSITIVE_INFINITY, RealScalar.ONE, //
        DoubleScalar.NEGATIVE_INFINITY, //
        DoubleScalar.POSITIVE_INFINITY, DoubleScalar.POSITIVE_INFINITY);
    Map<Tensor, Long> map = Tally.of(tensor);
    assertEquals((long) map.get(DoubleScalar.POSITIVE_INFINITY), 3);
    assertEquals((long) map.get(DoubleScalar.NEGATIVE_INFINITY), 1);
    assertEquals((long) map.get(RealScalar.of(1)), 1);
  }

  @Test
  void testSorted() {
    Tensor vector = Tensors.vector(4, 2, 3, 7, 2, 5, 4, 2, 2, 5);
    NavigableMap<Tensor, Long> navigableMap = Tally.sorted(vector);
    Tensor keys = Tensor.of(navigableMap.keySet().stream());
    assertEquals(keys, Tensors.vector(2, 3, 4, 5, 7));
  }

  @Test
  void testNumZero() {
    Tensor vector = Tensors.vector(-0.0, 0.0, -0.0);
    assertEquals(Tally.of(vector).size(), 1);
  }

  @Test
  void testFail() {
    assertThrows(Throw.class, () -> Tally.of(RealScalar.of(3.1234)));
  }
}
