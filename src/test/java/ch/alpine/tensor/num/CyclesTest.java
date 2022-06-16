// code by jph
package ch.alpine.tensor.num;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.TensorRuntimeException;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.ext.Serialization;

class CyclesTest {
  @Test
  void testSingleton() throws ClassNotFoundException, IOException {
    Cycles cycles = Serialization.copy(Cycles.of(Tensors.fromString("{{5, 9}, {7}, {}}")));
    assertEquals(cycles.toTensor(), Tensors.of(Tensors.vector(5, 9)));
  }

  @Test
  void testSimple() {
    Tensor _input = Tensors.fromString("{{5, 9}, {7, 14, 13}, {18, 4, 10, 19, 6}, {20, 1}, {}}");
    Cycles cycles = Cycles.of(_input);
    Tensor tensor = cycles.toTensor();
    assertEquals(tensor, Tensors.fromString("{{1, 20}, {4, 10, 19, 6, 18}, {5, 9}, {7, 14, 13}}"));
    assertEquals(cycles, tensor.stream().map(Tensors::of).map(Cycles::of).reduce(Cycles::combine).get());
    assertEquals(cycles, _input.stream().map(Tensors::of).map(Cycles::of).reduce(Cycles::combine).get());
  }

  @Test
  void testInverse() {
    Cycles cycles = Cycles.of(Tensors.fromString("{{1, 20}, {4, 10, 19, 6, 18}, {5, 9}, {7, 14, 13}}"));
    assertEquals(cycles.inverse().toTensor(), //
        Tensors.fromString("{{1, 20}, {4, 18, 6, 19, 10}, {5, 9}, {7, 13, 14}}"));
  }

  private static String _combo(String a, String b) {
    Cycles ca = Cycles.of(a);
    assertTrue(1 < ca.navigableMap().size());
    Cycles ci = ca.inverse();
    assertEquals(ca.navigableMap().size(), ci.navigableMap().size());
    assertEquals(ca.toTensor().length(), ci.toTensor().length());
    assertEquals(ca.combine(ci), Cycles.identity());
    assertEquals(ci.combine(ca), Cycles.identity());
    Cycles cb = Cycles.of(b);
    assertTrue(1 < cb.navigableMap().size());
    assertEquals(cb.combine(cb.inverse()), Cycles.identity());
    assertEquals(cb.inverse().combine(cb), Cycles.identity());
    return ca.combine(cb).toString();
  }

  @Test
  void testCombine() {
    assertEquals(_combo("{{1, 2, 3}}", "{{3, 4}}"), "{{1, 2, 4, 3}}");
    assertEquals(_combo("{{1, 2}, {4, 5}}", "{{3, 4}}"), "{{1, 2}, {3, 4, 5}}");
    assertEquals(_combo("{{1, 2}, {4, 5}}", "{{3, 4, 5}}"), "{{1, 2}, {3, 4}}");
    assertEquals(_combo("{{1, 2, 3}, {4, 5}}", "{{3, 4, 5}}"), "{{1, 2, 4, 3}}");
    assertEquals(_combo("{{2, 3}, {4, 5}}", "{{3, 4, 1}}"), "{{1, 3, 2, 4, 5}}");
    assertEquals(_combo("{{2, 3}}", "{{1, 2, 3}}"), "{{1, 2}}");
    assertEquals(_combo("{{2, 7, 3}, {4, 5}}", "{{1, 2, 3}, {7, 4, 5, 6}}"), "{{1, 2, 4, 6, 7}}");
    assertEquals(_combo("{{2, 7, 3}, {1, 6}, {4, 5}}", "{{1, 2, 3}, {7, 4, 5, 6}}"), "{{1, 7}, {2, 4, 6}}");
  }

  @Test
  void testEmpty() {
    assertEquals(Cycles.of(Tensors.empty()).toTensor(), Tensors.empty());
    assertEquals(Cycles.of(Tensors.empty()), Cycles.identity());
  }

  @Test
  void testNonEquals() throws ClassNotFoundException, IOException {
    Object cycles = Serialization.copy(Cycles.of(Tensors.fromString("{{5, 9}, {7}, {}}")));
    assertFalse(cycles.equals(Pi.VALUE));
  }

  @Test
  void testReplace() {
    assertEquals(Cycles.of(Tensors.fromString("{{2, 3, 4, 6}}")).replace(4), 6);
    assertEquals(Cycles.of(Tensors.fromString("{{2, 3, 4, 6}}")).replace(1), 1);
  }

  @Test
  void testReplaceFail() {
    Cycles cycles = Cycles.of(Tensors.fromString("{{2, 3, 4, 6}}"));
    assertEquals(cycles.replace(0), 0);
    assertThrows(IllegalArgumentException.class, () -> cycles.replace(-1));
  }

  @Test
  void testScalarFail() {
    assertThrows(TensorRuntimeException.class, () -> Cycles.of(Tensors.fromString("{3}")));
  }

  @Test
  void testDuplicateFail() {
    assertThrows(TensorRuntimeException.class, () -> Cycles.of(Tensors.fromString("{{5, 5}, {3}, {2, 2, 2}}")));
  }

  @Test
  void testNegativeFail() {
    assertThrows(TensorRuntimeException.class, () -> Cycles.of(Tensors.fromString("{{-3}}")));
    assertThrows(TensorRuntimeException.class, () -> Cycles.of(Tensors.fromString("{{3, -0.1}}")));
  }

  @Test
  void testPowerFail() {
    Cycles cycles = Cycles.of(Tensors.fromString("{{1, 20}, {4, 10, 19, 6, 18}, {5, 9}, {7, 14, 13}}"));
    assertThrows(TensorRuntimeException.class, () -> cycles.power(Pi.HALF));
  }
}
