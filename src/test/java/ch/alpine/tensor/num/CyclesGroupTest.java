// code by jph
package ch.alpine.tensor.num;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import java.io.IOException;
import java.math.BigInteger;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Scalars;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.alg.RotateLeft;
import ch.alpine.tensor.ext.ArgMin;
import ch.alpine.tensor.ext.Integers;
import ch.alpine.tensor.ext.Serialization;

class CyclesGroupTest {
  private static final BinaryPower<Cycles> BINARY_POWER = new BinaryPower<>(CyclesGroup.INSTANCE);

  private static void _check(Cycles arg, BigInteger exponent, Cycles expexted) throws ClassNotFoundException, IOException {
    assertEquals(Serialization.copy(BINARY_POWER).raise(arg, exponent), expexted);
    assertEquals(arg.power(exponent), expexted);
    assertEquals(arg.power(RealScalar.of(exponent)), expexted);
  }

  @Test
  void testSimple() throws ClassNotFoundException, IOException {
    _check(Cycles.of(Tensors.fromString("{{4, 2, 5}, {6, 3, 1, 7}}")), BigInteger.valueOf(6), //
        Cycles.of(Tensors.fromString("{{1, 6}, {3, 7}}")));
    _check(Cycles.of(Tensors.fromString("{{4, 2, 5}, {6, 3, 1, 7}}")), BigInteger.valueOf(-2), //
        Cycles.of(Tensors.fromString("{{1, 6}, {2, 5, 4}, {3, 7}}")));
    _check(Cycles.of(Tensors.fromString("{{4, 2, 5}, {6, 3, 1, 7}}")), BigInteger.valueOf(12), //
        Cycles.identity());
  }

  @Test
  void testForloop() {
    Tensor factor = Tensors.fromString("{{5, 9}, {7, 14, 13}, {18, 4, 10, 19, 6}, {20, 1}, {}}");
    Cycles cycles = Cycles.of(factor);
    Cycles cumprd = Cycles.identity();
    for (int exp = 0; exp < 20; ++exp) {
      assertEquals(cumprd, cycles.power(BigInteger.valueOf(exp)));
      assertEquals(cumprd.inverse(), cycles.power(BigInteger.valueOf(exp).negate()));
      cumprd = cumprd.combine(cycles);
    }
  }

  private static Tensor minFirst(Tensor vector) {
    return RotateLeft.of(vector, ArgMin.of(vector));
  }

  private static final Comparator<Tensor> COMPARATOR = //
      (cycle1, cycle2) -> Scalars.compare(cycle1.Get(0), cycle2.Get(0));

  private static Set<Cycles> _group(Set<Cycles> gen) {
    Set<Cycles> all = new HashSet<>();
    Set<Cycles> ite = new HashSet<>(gen);
    while (all.addAll(ite))
      ite = ite.stream().flatMap(cycles -> gen.stream().map(cycles::combine)).collect(Collectors.toSet());
    for (Cycles cycles : all)
      for (Entry<Integer, Integer> entry : cycles.navigableMap().entrySet())
        assertFalse(entry.getKey().equals(entry.getValue()));
    for (Cycles cycles : all) {
      Tensor result = Tensor.of(cycles.toTensor().stream().map(CyclesGroupTest::minFirst).sorted(COMPARATOR));
      assertEquals(cycles.toTensor(), result);
    }
    return all;
  }

  @Test
  void testOrbit3() {
    Set<Cycles> set = new HashSet<>();
    set.add(TestHelper.of("{{0, 1}}"));
    set.add(TestHelper.of("{{0, 1, 2}}"));
    Set<Cycles> group = _group(set);
    assertEquals(group.size(), 6);
    Cycles other = TestHelper.of("{{2, 3}}");
    assertEquals(6, group.stream().map(other::combine).distinct().count());
    assertEquals(6, group.stream().map(e -> e.combine(other)).distinct().count());
    assertEquals(6, group.stream().map(Cycles::inverse).distinct().count());
    Map<Integer, Long> map = group.stream() //
        .map(c -> PermutationList.of(c, 5)).map(Integers::parity) //
        .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));
    assertEquals(map.get(0).intValue(), 3);
    assertEquals(map.get(1).intValue(), 3);
    for (Cycles cycles : group)
      assertEquals(cycles.parity(), Integers.parity(PermutationList.of(cycles, 3)));
  }

  @Test
  void testOrbit4() {
    Set<Cycles> set = new HashSet<>();
    set.add(TestHelper.of("{{0, 1}}"));
    set.add(TestHelper.of("{{0, 1, 2, 3}}"));
    Set<Cycles> group = _group(set);
    assertEquals(group.size(), 24);
    Cycles other = TestHelper.of("{{2, 3}}");
    assertEquals(24, group.stream().map(other::combine).distinct().count());
    assertEquals(24, group.stream().map(e -> e.combine(other)).distinct().count());
    Map<Integer, Long> map = group.stream() //
        .map(c -> PermutationList.of(c, 4)).map(Integers::parity) //
        .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));
    assertEquals(map.get(0).intValue(), 12);
    assertEquals(map.get(1).intValue(), 12);
    for (Cycles cycles : group)
      assertEquals(cycles.parity(), Integers.parity(PermutationList.of(cycles, 4)));
  }

  @Test
  void testGroupEx0() {
    assertEquals(_group(Collections.singleton(Cycles.identity())).size(), 1);
  }

  @Test
  void testGroupEx1() {
    Set<Cycles> gen = new HashSet<>();
    gen.add(TestHelper.of("{{2, 10}, {4, 11}, {5, 7}}"));
    gen.add(TestHelper.of("{{1, 4, 3}, {2, 5, 6}}"));
    assertEquals(_group(gen).size(), 1440);
  }

  @Test
  void testGroupEx2() {
    Cycles cycles = TestHelper.of( //
        "{{1, 18, 25, 8, 11, 33, 45, 34, 19, 39, 4, 35, 46, 37, 10, 48, 7, 31, 6, 42, 36, 15, 29}, {2, 21, 14, 38, 26, 24, 41, 22, 12, 49}, {3, 28,  20, 50, 43, 23, 9, 5, 16, 44, 30, 27, 17}, {13, 40, 32, 47}}");
    int[] array = cycles.toTensor().stream().mapToInt(Tensor::length).toArray();
    Scalar scalar = Tensors.vectorInt(array).stream().map(Scalar.class::cast).reduce(LCM::of).get();
    assertEquals(scalar, RealScalar.of(5980));
    Set<Cycles> set = _group(Collections.singleton(cycles));
    assertEquals(set.size(), 5980);
  }

  @Test
  void testToString() {
    assertEquals(CyclesGroup.INSTANCE.toString(), "CyclesGroup");
  }
}
