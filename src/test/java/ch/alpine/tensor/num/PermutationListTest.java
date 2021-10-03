// code by jph
package ch.alpine.tensor.num;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import junit.framework.TestCase;

public class PermutationListTest extends TestCase {
  public void testSimple() {
    Tensor perm = Tensors.fromString("{{3, 2}, {1, 6, 7}}").map(s -> s.subtract(RealScalar.ONE));
    List<Integer> list = PermutationList.of(Cycles.of(perm), 9).boxed().collect(Collectors.toList());
    List<Integer> math = IntStream.of(6, 3, 2, 4, 5, 7, 1, 8, 9) //
        .map(i -> i - 1).boxed().collect(Collectors.toList());
    assertEquals(list, math);
  }

  public void testMore() {
    Cycles cycles = Cycles.of("{{2, 1}, {0, 5, 6}}");
    List<Integer> list = PermutationList.of(cycles, 9).boxed().collect(Collectors.toList());
    assertEquals(list, Arrays.asList(5, 2, 1, 3, 4, 6, 0, 7, 8));
  }

  public void testExceeds() {
    Cycles cycles = Cycles.of("{{2, 1}, {0, 5, 6}}");
    List<Integer> list = PermutationList.of(cycles, 3).boxed().collect(Collectors.toList());
    assertEquals(list, Arrays.asList(5, 2, 1));
  }
}
