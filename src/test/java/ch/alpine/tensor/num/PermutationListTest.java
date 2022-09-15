// code by jph
package ch.alpine.tensor.num;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.ext.Integers;

class PermutationListTest {
  @Test
  void testSimple() {
    Tensor perm = Tensors.fromString("{{3, 2}, {1, 6, 7}}").map(s -> s.subtract(RealScalar.ONE));
    List<Integer> list = Integers.asList(PermutationList.of(Cycles.of(perm), 9));
    List<Integer> math = IntStream.of(6, 3, 2, 4, 5, 7, 1, 8, 9) //
        .map(i -> i - 1).boxed().collect(Collectors.toList());
    assertEquals(list, math);
  }

  @Test
  void testMore() {
    Cycles cycles = TestHelper.of("{{2, 1}, {0, 5, 6}}");
    List<Integer> list = Integers.asList(PermutationList.of(cycles, 9));
    assertEquals(list, Arrays.asList(5, 2, 1, 3, 4, 6, 0, 7, 8));
    int parity = Integers.parity(PermutationList.of(cycles, 9));
    assertEquals(parity, 1);
  }

  @Test
  void testExceeds() {
    Cycles cycles = TestHelper.of("{{2, 1}, {0, 5, 6}}");
    List<Integer> list = Integers.asList(PermutationList.of(cycles, 7));
    assertEquals(list, Arrays.asList(5, 2, 1, 3, 4, 6, 0));
  }

  @Test
  void testLengthFail() {
    Cycles cycles = TestHelper.of("{{2, 1}, {0, 5, 6}}");
    assertThrows(IllegalArgumentException.class, () -> PermutationList.of(cycles, 0));
    assertThrows(IllegalArgumentException.class, () -> PermutationList.of(cycles, 6));
  }

  @Test
  void testMinLength2() {
    Cycles cycles = TestHelper.of("{{0, 1}}");
    PermutationList.of(cycles, 2);
    assertThrows(IllegalArgumentException.class, () -> PermutationList.of(cycles, 1));
  }

  @Test
  void testIdFail() {
    Cycles cycles = Cycles.identity();
    PermutationList.of(cycles, 0);
    PermutationList.of(cycles, 1);
    assertThrows(IllegalArgumentException.class, () -> PermutationList.of(cycles, -1));
  }
}
