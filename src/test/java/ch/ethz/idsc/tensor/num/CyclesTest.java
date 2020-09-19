// code by jph
package ch.ethz.idsc.tensor.num;

import java.io.IOException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map.Entry;
import java.util.Set;
import java.util.stream.Collectors;

import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.io.Serialization;
import ch.ethz.idsc.tensor.opt.Pi;
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
    assertTrue(1 < ca.map().size());
    Cycles ci = ca.inverse();
    assertEquals(ca.map().size(), ci.map().size());
    assertEquals(ca.toTensor().length(), ci.toTensor().length());
    assertEquals(ca.combine(ci), Cycles.identity());
    assertEquals(ci.combine(ca), Cycles.identity());
    Cycles cb = Cycles.of(b);
    assertTrue(1 < cb.map().size());
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
    Cycles cycles = Serialization.copy(Cycles.of(Tensors.fromString("{{5, 9}, {7}, {}}")));
    assertFalse(cycles.equals(Pi.VALUE));
  }

  private static Set<Cycles> _group(Set<Cycles> gen) {
    Set<Cycles> all = new HashSet<>();
    Set<Cycles> ite = new HashSet<>(gen);
    while (all.addAll(ite))
      ite = ite.stream().flatMap(cycles -> gen.stream().map(cycles::combine)).collect(Collectors.toSet());
    for (Cycles cycles : all)
      for (Entry<Integer, Integer> entry : cycles.map().entrySet())
        assertFalse(entry.getKey().equals(entry.getValue()));
    return all;
  }

  public void testGroupEx0() {
    assertEquals(_group(Collections.singleton(Cycles.identity())).size(), 1);
  }

  public void testGroupEx1() {
    Set<Cycles> gen = new HashSet<>();
    gen.add(Cycles.of("{{2, 10}, {4, 11}, {5, 7}}"));
    gen.add(Cycles.of("{{1, 4, 3}, {2, 5, 6}}"));
    // ---
    assertEquals(_group(gen).size(), 1440);
  }

  public void testGroupEx2() {
    Cycles cycles = Cycles.of( //
        "{{1, 18, 25, 8, 11, 33, 45, 34, 19, 39, 4, 35, 46, 37, 10, 48, 7, 31, 6, 42, 36, 15, 29}, {2, 21, 14, 38, 26, 24, 41, 22, 12, 49}, {3, 28,  20, 50, 43, 23, 9, 5, 16, 44, 30, 27, 17}, {13, 40, 32, 47}}");
    Set<Cycles> set = _group(Collections.singleton(cycles));
    assertEquals(set.size(), 5980);
  }

  public void testScalarFail() {
    try {
      Cycles.of(Tensors.fromString("{3}"));
      fail();
    } catch (Exception exception) {
      // ---
    }
  }

  public void testDuplicateFail() {
    try {
      Cycles.of(Tensors.fromString("{{5, 5}, {3}, {2, 2, 2}}"));
      fail();
    } catch (Exception exception) {
      // ---
    }
  }

  public void testNegativeFail() {
    try {
      Cycles.of(Tensors.fromString("{{-3}}"));
      fail();
    } catch (Exception exception) {
      // ---
    }
    try {
      Cycles.of(Tensors.fromString("{{3, -0.1}}"));
      fail();
    } catch (Exception exception) {
      // ---
    }
  }
}
