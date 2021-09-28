// code by jph
package ch.alpine.tensor.alg;

import java.util.ArrayList;

import junit.framework.TestCase;

public class OuterProductStreamTest extends TestCase {
  public void testSimple() {
    long count = OuterProductStream.of(new int[] { 3, 4 }, true) //
        .map(ArrayList::new) //
        .distinct().count();
    assertEquals(count, 12);
  }

  public void testReverse() {
    // OuterProductStream.of(new int[] { 3, 4 }, false) //
    // .map(ArrayList::new) //
    // .forEach(System.out::println);
    long count = OuterProductStream.of(new int[] { 3, 4 }, false) //
        .map(ArrayList::new) //
        .distinct().count();
    assertEquals(count, 12);
  }
}
