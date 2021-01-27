// code by jph
package ch.ethz.idsc.tensor.num;

import java.io.IOException;
import java.math.BigInteger;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Map.Entry;
import java.util.Set;
import java.util.stream.Collectors;

import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalars;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.RotateLeft;
import ch.ethz.idsc.tensor.ext.Serialization;
import ch.ethz.idsc.tensor.red.ArgMin;
import junit.framework.TestCase;

public class CyclesGroupTest extends TestCase {
  private static final BinaryPower<Cycles> BINARY_POWER = new BinaryPower<>(CyclesGroup.INSTANCE);

  private static void _check(Cycles arg, BigInteger exponent, Cycles expexted) throws ClassNotFoundException, IOException {
    assertEquals(Serialization.copy(BINARY_POWER).raise(arg, exponent), expexted);
    assertEquals(arg.power(exponent), expexted);
    assertEquals(arg.power(RealScalar.of(exponent)), expexted);
  }

  public void testSimple() throws ClassNotFoundException, IOException {
    _check(Cycles.of(Tensors.fromString("{{4, 2, 5}, {6, 3, 1, 7}}")), BigInteger.valueOf(6), //
        Cycles.of(Tensors.fromString("{{1, 6}, {3, 7}}")));
    _check(Cycles.of(Tensors.fromString("{{4, 2, 5}, {6, 3, 1, 7}}")), BigInteger.valueOf(-2), //
        Cycles.of(Tensors.fromString("{{1, 6}, {2, 5, 4}, {3, 7}}")));
    _check(Cycles.of(Tensors.fromString("{{4, 2, 5}, {6, 3, 1, 7}}")), BigInteger.valueOf(12), //
        Cycles.identity());
  }

  public void testForloop() {
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

  public void testGroupEx0() {
    assertEquals(_group(Collections.singleton(Cycles.identity())).size(), 1);
  }

  public void testGroupEx1() {
    Set<Cycles> gen = new HashSet<>();
    gen.add(Cycles.of("{{2, 10}, {4, 11}, {5, 7}}"));
    gen.add(Cycles.of("{{1, 4, 3}, {2, 5, 6}}"));
    assertEquals(_group(gen).size(), 1440);
  }

  public void testGroupEx2() {
    Cycles cycles = Cycles.of( //
        "{{1, 18, 25, 8, 11, 33, 45, 34, 19, 39, 4, 35, 46, 37, 10, 48, 7, 31, 6, 42, 36, 15, 29}, {2, 21, 14, 38, 26, 24, 41, 22, 12, 49}, {3, 28,  20, 50, 43, 23, 9, 5, 16, 44, 30, 27, 17}, {13, 40, 32, 47}}");
    Set<Cycles> set = _group(Collections.singleton(cycles));
    assertEquals(set.size(), 5980);
  }

  public void testToString() {
    assertEquals(CyclesGroup.INSTANCE.toString(), "CyclesGroup");
  }
}
