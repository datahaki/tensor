// code by jph
package ch.alpine.tensor.alg;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.ComplexScalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.TensorRuntimeException;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.mat.HilbertMatrix;

class MapThreadTest {
  @Test
  void testEmptyPositive() {
    assertEquals(MapThread.of(l -> l.get(0), Collections.emptyList(), 1), Tensors.empty());
    assertEquals(MapThread.of(l -> l.get(0), Collections.emptyList(), 2), Tensors.empty());
    assertThrows(IndexOutOfBoundsException.class, () -> MapThread.of(l -> l.get(0), Collections.emptyList(), 0));
  }

  @Test
  void testEmptyZero() {
    Tensor result = MapThread.of(l -> ComplexScalar.I, Collections.emptyList(), 0);
    assertEquals(ComplexScalar.I, result);
  }

  @Test
  void testNegFail() {
    assertThrows(IllegalArgumentException.class, () -> MapThread.of(l -> ComplexScalar.I, Collections.emptyList(), -1));
  }

  @Test
  void testFail() {
    List<Tensor> list = Arrays.asList(HilbertMatrix.of(2, 3), HilbertMatrix.of(3, 3));
    MapThread.of(l -> ComplexScalar.I, list, 0);
    assertThrows(TensorRuntimeException.class, () -> MapThread.of(l -> ComplexScalar.I, list, 1));
  }
}
