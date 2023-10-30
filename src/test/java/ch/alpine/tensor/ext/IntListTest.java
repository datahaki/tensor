// code by jph
package ch.alpine.tensor.ext;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.NoSuchElementException;
import java.util.RandomAccess;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;

class IntListTest {
  @Test
  void testEmpty() {
    List<Integer> intList = IntList.wrap(new int[] {});
    assertEquals(intList.size(), 0);
    assertTrue(intList.isEmpty());
  }

  @Test
  void testConstructEmpty() {
    List<Integer> intList = IntList.wrap(new int[] { 0, 1, 2, 3, 4, 5 }).subList(1, 1);
    assertEquals(intList.size(), 0);
    assertTrue(intList.isEmpty());
  }

  @Test
  void testSimple() {
    List<Integer> intList = IntList.wrap(new int[] { 0, 1, 2, 3, 4, 5 });
    assertEquals(intList.size(), 6);
    assertFalse(intList.isEmpty());
    for (int index = 0; index < intList.size(); ++index)
      assertEquals(intList.get(index), (Integer) index);
  }

  @Test
  void testConstructFails() {
    assertThrows(NullPointerException.class, () -> IntList.wrap(null));
    List<Integer> intList = IntList.wrap(new int[] { 0, 1, 2, 3, 4, 5 }).subList(4, 6);
    assertEquals(intList.size(), 2);
    assertFalse(intList.isEmpty());
  }

  @Test
  void testGetFail() {
    List<Integer> intList = IntList.wrap(new int[] { 0, 1, 2, 3, 4, 5 });
    assertThrows(IllegalArgumentException.class, () -> intList.get(-1));
    List<Integer> subList = intList.subList(2, 5);
    assertThrows(IllegalArgumentException.class, () -> subList.get(-1));
    assertThrows(IllegalArgumentException.class, () -> subList.get(4));
  }

  @Test
  void testSublist() {
    List<Integer> intList = IntList.wrap(new int[] { 0, 1, 2, 3, 4, 5 });
    intList = intList.subList(2, 5);
    assertEquals(intList.size(), 3);
    assertEquals(intList.get(0), (Integer) 2);
    assertEquals(intList.get(1), (Integer) 3);
    assertEquals(intList.get(2), (Integer) 4);
    assertEquals(intList.stream().collect(Collectors.toList()), Arrays.asList(2, 3, 4));
    intList = intList.subList(1, 2);
    assertEquals(intList.size(), 1);
    assertEquals(intList.get(0), (Integer) 3);
    intList = intList.subList(1, 1);
    assertEquals(intList.size(), 0);
  }

  @Test
  void testSublistFail0() {
    List<Integer> intList = IntList.wrap(new int[] { 0, 1, 2, 3, 4, 5 }).subList(2, 5);
    assertEquals(intList.subList(3, 3).size(), 0);
    assertThrows(IllegalArgumentException.class, () -> intList.subList(4, 4));
    assertThrows(IllegalArgumentException.class, () -> intList.subList(1, -1));
  }

  @Test
  void testSublistFail1() {
    List<Integer> intList = IntList.wrap(new int[] { 0, 1, 2, 3, 4, 5 });
    assertTrue(intList.subList(2, 2).isEmpty());
    assertThrows(IllegalArgumentException.class, () -> intList.subList(2, 7));
    assertThrows(IllegalArgumentException.class, () -> intList.subList(2, 1));
    assertThrows(IllegalArgumentException.class, () -> intList.subList(-1, 1));
  }

  @Test
  void testSublistFail2() {
    List<Integer> intList = IntList.wrap(new int[] { 0, 1, 2, 3, 4, 5 });
    assertThrows(IllegalArgumentException.class, () -> intList.subList(1, -1));
  }

  @Test
  void testEquals() {
    List<Integer> intList = IntList.wrap(new int[] { 2, 3, 4 });
    assertEquals(intList, Arrays.asList(2, 3, 4));
    assertEquals(Arrays.asList(2, 3, 4), intList);
    assertNotEquals(intList, Arrays.asList(2, 3));
    assertNotEquals(intList, Arrays.asList(2, 3, 5));
    assertNotEquals(null, intList);
    assertNotEquals(Arrays.asList(2, 3), intList);
    assertNotEquals(Arrays.asList(2, 3, 5), intList);
  }

  @Test
  void testFor() {
    List<Integer> intList = IntList.wrap(new int[] { 0, 1, 2, 3, 4, 5 }).subList(2, 5);
    for (Integer val : intList) {
      assertEquals(val.intValue(), 2);
      break;
    }
  }

  @Test
  void testContains() {
    List<Integer> intList = IntList.wrap(new int[] { 0, 1, 2, 3, 4, 5 }).subList(2, 5);
    assertFalse(intList.contains(1));
    assertTrue(intList.contains(2));
    assertTrue(intList.contains(3));
    assertTrue(intList.contains(4));
    assertFalse(intList.contains(5));
    assertThrows(NullPointerException.class, () -> intList.contains(null));
  }

  @Test
  void testContainsEmptyFail() {
    List<Integer> intList = IntList.wrap(new int[] {});
    assertFalse(intList.contains(5));
    assertThrows(NullPointerException.class, () -> intList.contains(null));
  }

  @Test
  void testContainsAll() {
    List<Integer> intList = IntList.wrap(new int[] { 0, 1, 2, 3, 4, 5 }).subList(2, 5);
    assertFalse(intList.containsAll(Arrays.asList(1, 2)));
    assertTrue(intList.containsAll(Arrays.asList(2, 2, 4)));
  }

  @Test
  void testIndexOf() {
    List<Integer> intList = IntList.wrap(new int[] { 0, 1, 2, 3, 4, 5 }).subList(2, 5);
    assertEquals(intList.indexOf(2), 0);
    assertEquals(intList.indexOf(3), 1);
    assertEquals(intList.indexOf(4), 2);
    assertEquals(intList.indexOf(5), -1);
    assertThrows(NullPointerException.class, () -> intList.indexOf(null));
  }

  @Test
  void testLastIndexOf() {
    List<Integer> intList = IntList.wrap(new int[] { 0, 1, 2, 3, 2, 4, 5 }).subList(2, 6);
    assertEquals(intList.lastIndexOf(2), 2);
    assertEquals(intList.lastIndexOf(3), 1);
    assertEquals(intList.lastIndexOf(4), 3);
    assertEquals(intList.lastIndexOf(5), -1);
    assertThrows(NullPointerException.class, () -> intList.lastIndexOf(null));
  }

  @Test
  void testAddRemoveFail() {
    List<Integer> intList = IntList.wrap(new int[] { 0, 1, 2, 3, 2, 4, 5 }).subList(2, 6);
    assertThrows(UnsupportedOperationException.class, () -> intList.add(3));
    assertThrows(UnsupportedOperationException.class, () -> intList.add(0, 0));
    assertThrows(UnsupportedOperationException.class, () -> intList.remove(3));
    assertThrows(UnsupportedOperationException.class, () -> intList.remove(Integer.valueOf(3)));
    assertThrows(UnsupportedOperationException.class, () -> intList.addAll(Arrays.asList(2, 3, 4)));
    assertThrows(UnsupportedOperationException.class, () -> intList.addAll(0, Arrays.asList(2, 3, 4)));
    assertThrows(UnsupportedOperationException.class, () -> intList.removeAll(Arrays.asList(2, 3, 4)));
    assertThrows(UnsupportedOperationException.class, () -> intList.retainAll(Arrays.asList(2, 3, 4)));
    assertThrows(UnsupportedOperationException.class, intList::clear);
    assertThrows(UnsupportedOperationException.class, () -> intList.set(0, 0));
    assertThrows(UnsupportedOperationException.class, () -> intList.toArray(new Integer[10]));
  }

  @Test
  void testHashCode() {
    List<Integer> intList = IntList.wrap(new int[] { 2, 3, 4 });
    assertEquals(intList.hashCode(), Arrays.asList(2, 3, 4).hashCode());
    assertEquals(IntList.wrap(new int[] {}).hashCode(), List.of().hashCode());
  }

  @Test
  void testToArray() {
    List<Integer> intList = IntList.wrap(new int[] { 2, 3, 4 });
    ArrayList<Integer> arrayList = new ArrayList<>(intList);
    assertEquals(arrayList, Arrays.asList(2, 3, 4));
  }

  @Test
  void testInterator() {
    List<Integer> intList = IntList.wrap(new int[] { 0, 1, 2, 3, 4, 5 }).subList(2, 5);
    Iterator<Integer> iterator = intList.iterator();
    assertTrue(iterator.hasNext());
    assertThrows(UnsupportedOperationException.class, iterator::remove);
    assertEquals(iterator.next().intValue(), 2);
    assertTrue(iterator.hasNext());
    assertEquals(iterator.next().intValue(), 3);
    assertTrue(iterator.hasNext());
    assertEquals(iterator.next().intValue(), 4);
    assertFalse(iterator.hasNext());
    assertThrows(NoSuchElementException.class, iterator::next);
  }

  @Test
  void testListIterator() {
    List<Integer> intList = IntList.wrap(new int[] { 0, 1, 2, 3, 4, 5 }).subList(2, 5);
    assertTrue(intList.listIterator(0).hasNext());
    assertTrue(intList.listIterator(2).hasNext());
    assertFalse(intList.listIterator(3).hasNext());
    assertThrows(IllegalArgumentException.class, () -> intList.listIterator(-1));
    assertThrows(IllegalArgumentException.class, () -> intList.listIterator(4));
  }

  @Test
  void testListIteratorPrevious() {
    List<Integer> intList = IntList.wrap(new int[] { 0, 1, 2, 3, 4, 5 }).subList(2, 5);
    ListIterator<Integer> listIterator = intList.listIterator(1);
    assertEquals(listIterator.previousIndex(), 0);
    assertThrows(UnsupportedOperationException.class, () -> listIterator.add(3));
    assertThrows(UnsupportedOperationException.class, () -> listIterator.set(3));
    assertEquals(listIterator.nextIndex(), 2);
    assertTrue(listIterator.hasPrevious());
    assertEquals(listIterator.previous().intValue(), 2);
    assertFalse(listIterator.hasPrevious());
    assertThrows(NoSuchElementException.class, listIterator::previous);
    assertFalse(listIterator.hasPrevious());
    listIterator.next();
    assertTrue(listIterator.hasPrevious());
  }

  @Test
  void testSerializable() throws ClassNotFoundException, IOException {
    List<Integer> intList = IntList.wrap(new int[] { 0, 1, 2, 3, 4, 5 }).subList(2, 5);
    List<Integer> copy = Serialization.copy(intList);
    assertEquals(copy, Arrays.asList(2, 3, 4));
    assertInstanceOf(RandomAccess.class, intList);
  }

  @Test
  void testToString() {
    String s1 = Arrays.asList(10, 2, 0, -4).toString();
    String s2 = Integers.asList(new int[] { 10, 2, 0, -4 }).toString();
    assertEquals(s1, s2);
  }
}
