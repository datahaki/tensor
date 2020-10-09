// code by jph
package ch.ethz.idsc.tensor.ext;

import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.stream.Collectors;

import ch.ethz.idsc.tensor.usr.AssertFail;
import junit.framework.TestCase;

public class BoundedLinkedListTest extends TestCase {
  public void testSimple() {
    BoundedLinkedList<Integer> boundedLinkedList = new BoundedLinkedList<>(3);
    assertTrue(boundedLinkedList.add(0));
    assertTrue(boundedLinkedList.add(1));
    assertTrue(boundedLinkedList.add(2));
    assertTrue(boundedLinkedList.add(3));
    assertTrue(boundedLinkedList.add(4));
    assertEquals(boundedLinkedList.size(), 3);
    assertEquals(boundedLinkedList.get(0), Integer.valueOf(2));
    assertEquals(boundedLinkedList.get(1), Integer.valueOf(3));
    assertEquals(boundedLinkedList.get(2), Integer.valueOf(4));
    assertEquals(boundedLinkedList.stream().collect(Collectors.toList()), Arrays.asList(2, 3, 4));
    assertEquals(boundedLinkedList.peek(), Integer.valueOf(2));
    assertEquals(boundedLinkedList.poll(), Integer.valueOf(2));
    assertEquals(boundedLinkedList.size(), 2);
    assertEquals(boundedLinkedList.stream().collect(Collectors.toList()), Arrays.asList(3, 4));
    boundedLinkedList.clear();
    assertEquals(boundedLinkedList.size(), 0);
  }

  public void testAddAll() {
    BoundedLinkedList<Integer> boundedLinkedList = new BoundedLinkedList<>(2);
    assertTrue(boundedLinkedList.add(0));
    assertTrue(boundedLinkedList.add(1));
    AssertFail.of(() -> boundedLinkedList.addAll(Arrays.asList(6, 7)));
    AssertFail.of(() -> boundedLinkedList.addAll(0, Arrays.asList(6, 7)));
  }

  public void testEmpty() {
    BoundedLinkedList<Integer> boundedLinkedList = new BoundedLinkedList<>(0);
    assertEquals(boundedLinkedList.size(), 0);
    assertFalse(boundedLinkedList.add(0));
    assertFalse(boundedLinkedList.add(1));
    assertEquals(boundedLinkedList.size(), 0);
  }

  public void testAddAtIndex() {
    LinkedList<String> linkedList = new BoundedLinkedList<>(3);
    linkedList.add(0, "3");
    linkedList.add(0, "2");
    linkedList.add(0, "0");
    linkedList.add(1, "1");
    assertEquals(linkedList, Arrays.asList("1", "2", "3"));
  }

  public void testAddLast() {
    LinkedList<String> linkedList = new BoundedLinkedList<>(3);
    linkedList.addLast("0");
    linkedList.addLast("1");
    linkedList.addLast("2");
    linkedList.addLast("3");
    assertEquals(linkedList, Arrays.asList("1", "2", "3"));
  }

  public void testSerializable() throws ClassNotFoundException, IOException {
    BoundedLinkedList<Integer> boundedLinkedList = new BoundedLinkedList<>(2);
    assertTrue(boundedLinkedList.add(3));
    assertTrue(boundedLinkedList.add(4));
    BoundedLinkedList<Integer> copy = Serialization.copy(boundedLinkedList);
    boundedLinkedList.add(1);
    boundedLinkedList.add(2);
    assertEquals(copy.size(), 2);
    assertEquals(copy.get(0).intValue(), 3);
    assertEquals(copy.get(1).intValue(), 4);
  }

  public void testAddAllFail() {
    BoundedLinkedList<Integer> boundedLinkedList = new BoundedLinkedList<>(7);
    AssertFail.of(() -> boundedLinkedList.addAll(Arrays.asList(1, 2, 3, 4)));
  }

  public void testAddFirstFail() {
    BoundedLinkedList<Integer> boundedLinkedList = new BoundedLinkedList<>(7);
    try {
      boundedLinkedList.addFirst(4);
      fail();
    } catch (Exception exception) {
      // ---
    }
  }

  public void testFailNegativeSize() {
    new BoundedLinkedList<>(0);
    AssertFail.of(() -> new BoundedLinkedList<>(-1));
  }
}
