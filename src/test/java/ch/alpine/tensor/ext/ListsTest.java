// code by jph
package ch.alpine.tensor.ext;

import java.util.Arrays;
import java.util.LinkedList;

import ch.alpine.tensor.usr.AssertFail;
import junit.framework.TestCase;

public class ListsTest extends TestCase {
  public void testWithoutHead() {
    assertEquals(Lists.withoutHead(Arrays.asList(3, 2, 8)), Arrays.asList(2, 8));
  }

  public void testWithoutHeadFail() {
    AssertFail.of(() -> Lists.withoutHead(new LinkedList<>()));
  }

  public void testLast() {
    assertEquals(Lists.last(Arrays.asList(3, 2, 8)), (Integer) 8);
  }

  public void testLastFail() {
    AssertFail.of(() -> Lists.last(new LinkedList<>()));
  }
}
