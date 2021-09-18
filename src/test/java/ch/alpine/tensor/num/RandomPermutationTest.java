// code by jph
package ch.alpine.tensor.num;

import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.TreeSet;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Scalars;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.red.StandardDeviation;
import ch.alpine.tensor.usr.AssertFail;
import junit.framework.TestCase;

public class RandomPermutationTest extends TestCase {
  public void testSimple() {
    for (int count = 0; count < 10; ++count)
      assertEquals(RandomPermutation.ofLength(count).length, count);
  }

  public void testCycles() {
    Cycles cycles1 = RandomPermutation.of(6);
    Cycles cycles2 = RandomPermutation.of(9);
    cycles1.combine(cycles2);
  }

  public void testComplete2() {
    Set<Cycles> set = new TreeSet<>();
    for (int count = 0; count < 8 && set.size() < 2; ++count)
      set.add(RandomPermutation.of(2));
    assertEquals(set.size(), 2);
    assertEquals(set.toString(), "[{}, {{0, 1}}]");
  }

  public void testComplete3() {
    Set<Cycles> set = new TreeSet<>();
    for (int count = 0; count < 120 && set.size() < 6; ++count)
      set.add(RandomPermutation.of(3));
    assertEquals(set.size(), 6);
  }

  public void testComplete4() {
    Set<Cycles> set = new TreeSet<>();
    for (int count = 0; count < 480 && set.size() < 24; ++count)
      set.add(RandomPermutation.of(4));
    assertEquals(set.size(), 24);
  }

  public void testTally() {
    Map<Cycles, Long> map = Stream.generate(() -> RandomPermutation.of(3)) //
        .limit(1000) //
        .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));
    Scalar scalar = StandardDeviation.ofVector(Tensor.of(map.values().stream().map(RealScalar::of)));
    assertTrue(Scalars.lessThan(scalar, RealScalar.of(20)));
  }

  public void testSame() {
    int seed = new Random().nextInt();
    Cycles c1 = RandomPermutation.of(123, new Random(seed));
    Cycles c2 = RandomPermutation.of(123, new Random(seed));
    assertEquals(c1, c2);
  }

  public void testFails() {
    AssertFail.of(() -> RandomPermutation.of(-1));
    AssertFail.of(() -> RandomPermutation.ofLength(-1));
  }
}
