// code by jph
package ch.alpine.tensor.num;

import java.io.IOException;
import java.util.IdentityHashMap;
import java.util.Map;

import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.ext.Serialization;
import ch.alpine.tensor.usr.AssertFail;
import junit.framework.TestCase;

public class CyclesTest extends TestCase {
  public void testSingleton() throws ClassNotFoundException, IOException {
    Cycles cycles = Serialization.copy(Cycles.of(Tensors.fromString("{{5, 9}, {7}, {}}")));
    assertEquals(cycles.toTensor(), Tensors.of(Tensors.vector(5, 9)));
  }

  public void testSimple() {
    Tensor _input = Tensors.fromString("{{5, 9}, {7, 14, 13}, {18, 4, 10, 19, 6}, {20, 1}, {}}");
    Cycles cycles = Cycles.of(_input);
    Tensor tensor = cycles.toTensor();
    assertEquals(tensor, Tensors.fromString("{{1, 20}, {4, 10, 19, 6, 18}, {5, 9}, {7, 14, 13}}"));
    assertEquals(cycles, tensor.stream().map(Tensors::of).map(Cycles::of).reduce(Cycles::combine).get());
    assertEquals(cycles, _input.stream().map(Tensors::of).map(Cycles::of).reduce(Cycles::combine).get());
  }

  public void testInverse() {
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

  public void testCombine() {
    assertEquals(_combo("{{1, 2, 3}}", "{{3, 4}}"), "{{1, 2, 4, 3}}");
    assertEquals(_combo("{{1, 2}, {4, 5}}", "{{3, 4}}"), "{{1, 2}, {3, 4, 5}}");
    assertEquals(_combo("{{1, 2}, {4, 5}}", "{{3, 4, 5}}"), "{{1, 2}, {3, 4}}");
    assertEquals(_combo("{{1, 2, 3}, {4, 5}}", "{{3, 4, 5}}"), "{{1, 2, 4, 3}}");
    assertEquals(_combo("{{2, 3}, {4, 5}}", "{{3, 4, 1}}"), "{{1, 3, 2, 4, 5}}");
    assertEquals(_combo("{{2, 3}}", "{{1, 2, 3}}"), "{{1, 2}}");
    assertEquals(_combo("{{2, 7, 3}, {4, 5}}", "{{1, 2, 3}, {7, 4, 5, 6}}"), "{{1, 2, 4, 6, 7}}");
    assertEquals(_combo("{{2, 7, 3}, {1, 6}, {4, 5}}", "{{1, 2, 3}, {7, 4, 5, 6}}"), "{{1, 7}, {2, 4, 6}}");
  }

  public void testEmpty() {
    assertEquals(Cycles.of(Tensors.empty()).toTensor(), Tensors.empty());
    assertEquals(Cycles.of(Tensors.empty()), Cycles.identity());
  }

  public void testNonEquals() throws ClassNotFoundException, IOException {
    Object cycles = Serialization.copy(Cycles.of(Tensors.fromString("{{5, 9}, {7}, {}}")));
    assertFalse(cycles.equals(Pi.VALUE));
  }

  public void testKeyCollision() {
    Map<Integer, Integer> map = new IdentityHashMap<>();
    map.put(3, 5);
    map.put(4, 5);
    map.entrySet().stream().collect(Cycles.INVERSE);
  }

  public void testReplace() {
    assertEquals(Cycles.of(Tensors.fromString("{{2, 3, 4, 6}}")).replace(4), 6);
    assertEquals(Cycles.of(Tensors.fromString("{{2, 3, 4, 6}}")).replace(1), 1);
  }

  public void testReplaceFail() {
    Cycles cycles = Cycles.of(Tensors.fromString("{{2, 3, 4, 6}}"));
    assertEquals(cycles.replace(0), 0);
    AssertFail.of(() -> cycles.replace(-1));
  }

  public void testScalarFail() {
    AssertFail.of(() -> Cycles.of(Tensors.fromString("{3}")));
  }

  public void testDuplicateFail() {
    AssertFail.of(() -> Cycles.of(Tensors.fromString("{{5, 5}, {3}, {2, 2, 2}}")));
  }

  public void testNegativeFail() {
    AssertFail.of(() -> Cycles.of(Tensors.fromString("{{-3}}")));
    AssertFail.of(() -> Cycles.of(Tensors.fromString("{{3, -0.1}}")));
  }

  public void testPowerFail() {
    Cycles cycles = Cycles.of(Tensors.fromString("{{1, 20}, {4, 10, 19, 6, 18}, {5, 9}, {7, 14, 13}}"));
    AssertFail.of(() -> cycles.power(Pi.HALF));
  }
}
