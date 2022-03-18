// code by jph
package ch.alpine.tensor.num;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.TreeSet;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Scalars;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.ext.Integers;
import ch.alpine.tensor.red.StandardDeviation;
import ch.alpine.tensor.usr.AssertFail;

public class RandomPermutationTest {
  @Test
  public void testSimple() {
    for (int count = 0; count < 10; ++count) {
      int[] sigma = RandomPermutation.of(count);
      Integers.requirePermutation(sigma);
      assertEquals(sigma.length, count);
    }
    Integers.requirePermutation(RandomPermutation.of(13));
  }

  @Test
  public void testCycles() {
    Cycles cycles1 = RandomPermutation.cycles(6);
    Cycles cycles2 = RandomPermutation.cycles(9);
    cycles1.combine(cycles2);
  }

  @Test
  public void testComplete2() {
    Random random = new Random(345);
    Set<Cycles> set = new TreeSet<>();
    for (int count = 0; count < 8 && set.size() < 2; ++count)
      set.add(RandomPermutation.cycles(2, random));
    assertEquals(set.size(), 2);
    assertEquals(set.toString(), "[{}, {{0, 1}}]");
  }

  @Test
  public void testComplete3() {
    Random random = new Random(345);
    Set<Cycles> set = new HashSet<>();
    for (int count = 0; count < 120 && set.size() < 6; ++count)
      set.add(RandomPermutation.cycles(3, random));
    assertEquals(set.size(), 6);
  }

  @Test
  public void testComplete4() {
    Random random = new Random(345);
    Set<Cycles> set = new HashSet<>();
    for (int count = 0; count < 480 && set.size() < 24; ++count)
      set.add(RandomPermutation.cycles(4, random));
    assertEquals(set.size(), 24);
  }

  @Test
  public void testTally() {
    Random random = new Random(2);
    Map<Cycles, Long> map = Stream.generate(() -> RandomPermutation.cycles(3, random)) //
        .limit(200) //
        .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));
    Scalar scalar = StandardDeviation.ofVector(Tensor.of(map.values().stream().map(RealScalar::of)));
    assertTrue(Scalars.lessThan(scalar, RealScalar.of(5)));
  }

  @Test
  public void testCycles0() {
    assertEquals(RandomPermutation.cycles(0), Cycles.identity());
  }

  @Test
  public void testSameCycles() {
    int seed = new Random().nextInt();
    Cycles c1 = RandomPermutation.cycles(123, new Random(seed));
    Cycles c2 = RandomPermutation.cycles(123, new Random(seed));
    assertEquals(c1, c2);
  }

  @Test
  public void testSameArrays() {
    int seed = new Random().nextInt();
    int[] c1 = RandomPermutation.of(123, new Random(seed));
    int[] c2 = RandomPermutation.of(123, new Random(seed));
    assertTrue(Arrays.equals(c1, c2));
  }

  @Test
  public void testFails() {
    AssertFail.of(() -> RandomPermutation.cycles(-1));
    AssertFail.of(() -> RandomPermutation.of(-1));
  }
}
