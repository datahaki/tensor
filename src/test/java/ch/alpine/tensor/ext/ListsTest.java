// code by jph
package ch.alpine.tensor.ext;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import org.junit.jupiter.api.Test;

class ListsTest {
  @Test
  void testWithoutHead() {
    assertEquals(Lists.rest(Arrays.asList(3, 2, 8)), Arrays.asList(2, 8));
  }

  @Test
  void testWithoutHeadFail() {
    assertThrows(IllegalArgumentException.class, () -> Lists.rest(new LinkedList<>()));
  }

  @Test
  void testReverseList() {
    assertEquals(List.of(3, 2, 1), List.of(1, 2, 3).reversed().stream().toList());
    assertEquals(List.of(3, 2, 1), Arrays.asList(1, 2, 3).reversed().stream().toList());
    List<Integer> list = new ArrayList<>();
    list.add(1);
    list.add(2);
    list.add(3);
    assertEquals(List.of(3, 2, 1), list.reversed().stream().toList());
  }
}
