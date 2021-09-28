// code by jph
package ch.alpine.tensor.alg;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import ch.alpine.tensor.usr.AssertFail;
import junit.framework.TestCase;

public class SizeTest extends TestCase {
  public void testIndexOf() {
    Size size = Size.of(Arrays.asList(4, 2, 3));
    assertEquals(size.indexOf(new int[] { 0, 0, 0 }, new int[] { 0, 1, 2 }), 0);
    assertEquals(size.indexOf(new int[] { 3, 1, 2 }, new int[] { 0, 1, 2 }), 23);
  }

  public void testString() {
    Size size = Size.of(Arrays.asList(4, 2, 3));
    String string = size.toString();
    assertFalse(string.isEmpty());
  }

  public void testStream() {
    List<Integer> list = Stream.iterate(3, i -> i < 10, i -> i + 1).collect(Collectors.toList());
    assertEquals(list, Arrays.asList(3, 4, 5, 6, 7, 8, 9));
  }

  public void testEmptyFail() {
    AssertFail.of(() -> Size.of(Arrays.asList()));
  }
}
