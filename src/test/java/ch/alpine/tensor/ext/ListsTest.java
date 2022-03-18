// code by jph
package ch.alpine.tensor.ext;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Arrays;
import java.util.LinkedList;

import org.junit.jupiter.api.Test;

public class ListsTest {
  @Test
  public void testWithoutHead() {
    assertEquals(Lists.rest(Arrays.asList(3, 2, 8)), Arrays.asList(2, 8));
  }

  @Test
  public void testWithoutHeadFail() {
    assertThrows(IllegalArgumentException.class, () -> Lists.rest(new LinkedList<>()));
  }

  @Test
  public void testLast() {
    assertEquals(Lists.last(Arrays.asList(3, 2, 8)), (Integer) 8);
  }

  @Test
  public void testLastFail() {
    assertThrows(IndexOutOfBoundsException.class, () -> Lists.last(new LinkedList<>()));
  }
}
