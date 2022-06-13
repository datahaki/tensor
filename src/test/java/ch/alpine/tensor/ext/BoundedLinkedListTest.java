// code by jph
package ch.alpine.tensor.ext;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;

class BoundedLinkedListTest {
  @Test
  void testSimple() {
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

  @Test
  void testPoll() {
    BoundedLinkedList<Integer> boundedLinkedList = new BoundedLinkedList<>(3);
    assertTrue(boundedLinkedList.add(0));
    assertTrue(boundedLinkedList.add(1));
    assertEquals(boundedLinkedList.poll(), Integer.valueOf(0));
    assertEquals(boundedLinkedList.poll(), Integer.valueOf(1));
    assertEquals(boundedLinkedList.size(), 0);
    assertTrue(boundedLinkedList.isEmpty());
  }

  @Test
  void testAddAll() {
    BoundedLinkedList<Integer> boundedLinkedList = new BoundedLinkedList<>(2);
    assertTrue(boundedLinkedList.add(0));
    assertTrue(boundedLinkedList.add(1));
    assertThrows(UnsupportedOperationException.class, () -> boundedLinkedList.addAll(Arrays.asList(6, 7)));
    assertThrows(UnsupportedOperationException.class, () -> boundedLinkedList.addAll(0, Arrays.asList(6, 7)));
  }

  @Test
  void testEmpty() {
    BoundedLinkedList<Integer> boundedLinkedList = new BoundedLinkedList<>(0);
    assertEquals(boundedLinkedList.size(), 0);
    assertFalse(boundedLinkedList.add(0));
    assertFalse(boundedLinkedList.add(1));
    assertEquals(boundedLinkedList.size(), 0);
  }

  @Test
  void testAddAtIndex() {
    LinkedList<String> linkedList = new BoundedLinkedList<>(3);
    linkedList.add(0, "3");
    linkedList.add(0, "2");
    linkedList.add(0, "0");
    linkedList.add(1, "1");
    assertEquals(linkedList, Arrays.asList("1", "2", "3"));
  }

  @Test
  void testAddLast() {
    LinkedList<String> linkedList = new BoundedLinkedList<>(3);
    linkedList.addLast("0");
    linkedList.addLast("1");
    linkedList.addLast("2");
    linkedList.addLast("3");
    assertEquals(linkedList, Arrays.asList("1", "2", "3"));
  }

  @Test
  void testSerializable() throws ClassNotFoundException, IOException {
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

  @Test
  void testAddAllFail() {
    BoundedLinkedList<Integer> boundedLinkedList = new BoundedLinkedList<>(7);
    assertThrows(UnsupportedOperationException.class, () -> boundedLinkedList.addAll(Arrays.asList(1, 2, 3, 4)));
  }

  @Test
  void testAddFirstFail() {
    BoundedLinkedList<Integer> boundedLinkedList = new BoundedLinkedList<>(7);
    assertThrows(UnsupportedOperationException.class, () -> boundedLinkedList.addFirst(4));
  }

  @Test
  void testFailNegativeSize() {
    assertThrows(IllegalArgumentException.class, () -> new BoundedLinkedList<>(-1));
  }
}
