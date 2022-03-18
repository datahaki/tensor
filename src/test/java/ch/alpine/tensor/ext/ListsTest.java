// code by jph
package ch.alpine.tensor.ext;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Arrays;
import java.util.LinkedList;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.usr.AssertFail;

public class ListsTest {
  @Test
  public void testWithoutHead() {
    assertEquals(Lists.rest(Arrays.asList(3, 2, 8)), Arrays.asList(2, 8));
  }

  @Test
  public void testWithoutHeadFail() {
    AssertFail.of(() -> Lists.rest(new LinkedList<>()));
  }

  @Test
  public void testLast() {
    assertEquals(Lists.last(Arrays.asList(3, 2, 8)), (Integer) 8);
  }

  @Test
  public void testLastFail() {
    AssertFail.of(() -> Lists.last(new LinkedList<>()));
  }
}
