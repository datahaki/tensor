// code by jph
package ch.alpine.tensor.ext;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Arrays;
import java.util.LinkedList;

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
}
