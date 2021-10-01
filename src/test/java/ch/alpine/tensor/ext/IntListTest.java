// code by jph
package ch.alpine.tensor.ext;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.ListIterator;
import java.util.stream.Collectors;

import ch.alpine.tensor.usr.AssertFail;
import junit.framework.TestCase;

public class IntListTest extends TestCase {
  public void testEmpty() {
    IntList intList = new IntList(new int[] {});
    assertEquals(intList.size(), 0);
    assertTrue(intList.isEmpty());
  }

  public void testConstructEmpty() {
    IntList intList = new IntList(new int[] { 0, 1, 2, 3, 4, 5 }, 1, 0);
    assertTrue(intList.isEmpty());
  }

  public void testSimple() {
    IntList intList = new IntList(new int[] { 0, 1, 2, 3, 4, 5 });
    assertEquals(intList.size(), 6);
    assertFalse(intList.isEmpty());
    for (int index = 0; index < intList.size(); ++index)
      assertEquals(intList.get(index), (Integer) index);
  }

  public void testConstructFails() {
    AssertFail.of(() -> new IntList(new int[] { 0, 1, 2, 3, 4, 5 }, -1, 3));
    AssertFail.of(() -> new IntList(new int[] { 0, 1, 2, 3, 4, 5 }, 1, -1));
    AssertFail.of(() -> new IntList(new int[] { 0, 1, 2, 3, 4, 5 }, 3, 4));
    IntList intList = new IntList(new int[] { 0, 1, 2, 3, 4, 5 }, 4, 2);
    assertEquals(intList.size(), 2);
  }

  public void testGetFail() {
    IntList intList = new IntList(new int[] { 0, 1, 2, 3, 4, 5 });
    AssertFail.of(() -> intList.get(-1));
    IntList subList = intList.subList(2, 5);
    AssertFail.of(() -> subList.get(-1));
    AssertFail.of(() -> subList.get(4));
  }

  public void testSublist() {
    IntList intList = new IntList(new int[] { 0, 1, 2, 3, 4, 5 });
    intList = intList.subList(2, 5);
    assertEquals(intList.size(), 3);
    assertEquals(intList.get(0), (Integer) 2);
    assertEquals(intList.get(1), (Integer) 3);
    assertEquals(intList.get(2), (Integer) 4);
    assertEquals(intList.stream().collect(Collectors.toList()), Arrays.asList(2, 3, 4));
  }

  public void testSublistFails() {
    IntList intList = new IntList(new int[] { 0, 1, 2, 3, 4, 5 });
    assertTrue(intList.subList(2, 2).isEmpty());
    AssertFail.of(() -> intList.subList(2, 7));
    AssertFail.of(() -> intList.subList(2, 1));
  }

  public void testEquals() {
    IntList intList = new IntList(new int[] { 2, 3, 4 });
    assertEquals(intList, Arrays.asList(2, 3, 4));
    assertTrue(Arrays.asList(2, 3, 4).equals(intList));
    assertFalse(intList.equals(Arrays.asList(2, 3)));
    assertFalse(intList.equals(Arrays.asList(2, 3, 5)));
    assertFalse(Arrays.asList(2, 3).equals(intList));
    assertFalse(Arrays.asList(2, 3, 5).equals(intList));
  }

  public void testFor() {
    IntList intList = new IntList(new int[] { 0, 1, 2, 3, 4, 5 }).subList(2, 5);
    for (Integer val : intList) {
      assertEquals(val.intValue(), 2);
      break;
    }
  }

  public void testContains() {
    IntList intList = new IntList(new int[] { 0, 1, 2, 3, 4, 5 }).subList(2, 5);
    assertFalse(intList.contains(1));
    assertTrue(intList.contains(2));
    assertTrue(intList.contains(3));
    assertTrue(intList.contains(4));
    assertFalse(intList.contains(5));
  }

  public void testContainsAll() {
    IntList intList = new IntList(new int[] { 0, 1, 2, 3, 4, 5 }).subList(2, 5);
    assertFalse(intList.containsAll(Arrays.asList(1, 2)));
    assertTrue(intList.containsAll(Arrays.asList(2, 2, 4)));
  }

  public void testIndexOf() {
    IntList intList = new IntList(new int[] { 0, 1, 2, 3, 4, 5 }).subList(2, 5);
    assertEquals(intList.indexOf(2), 0);
    assertEquals(intList.indexOf(3), 1);
    assertEquals(intList.indexOf(4), 2);
    assertEquals(intList.indexOf(5), -1);
    AssertFail.of(() -> intList.indexOf(null));
  }

  public void testLastIndexOf() {
    IntList intList = new IntList(new int[] { 0, 1, 2, 3, 2, 4, 5 }).subList(2, 6);
    assertEquals(intList.lastIndexOf(2), 2);
    assertEquals(intList.lastIndexOf(3), 1);
    assertEquals(intList.lastIndexOf(4), 3);
    assertEquals(intList.lastIndexOf(5), -1);
    AssertFail.of(() -> intList.lastIndexOf(null));
  }

  public void testHashCode() {
    IntList intList = new IntList(new int[] { 2, 3, 4 });
    assertEquals(intList.hashCode(), Arrays.asList(2, 3, 4).hashCode());
    assertEquals(new IntList(new int[] {}).hashCode(), Arrays.asList().hashCode());
  }

  public void testToArray() {
    IntList intList = new IntList(new int[] { 2, 3, 4 });
    ArrayList<Integer> arrayList = new ArrayList<>(intList);
    assertEquals(arrayList, Arrays.asList(2, 3, 4));
  }

  public void testInterator() {
    IntList intList = new IntList(new int[] { 0, 1, 2, 3, 4, 5 }).subList(2, 5);
    Iterator<Integer> iterator = intList.iterator();
    assertTrue(iterator.hasNext());
    assertEquals(iterator.next().intValue(), 2);
    assertTrue(iterator.hasNext());
    assertEquals(iterator.next().intValue(), 3);
    assertTrue(iterator.hasNext());
    assertEquals(iterator.next().intValue(), 4);
    assertFalse(iterator.hasNext());
    AssertFail.of(() -> iterator.next());
  }

  public void testListIterator() {
    IntList intList = new IntList(new int[] { 0, 1, 2, 3, 4, 5 }).subList(2, 5);
    assertTrue(intList.listIterator(0).hasNext());
    assertTrue(intList.listIterator(2).hasNext());
    assertFalse(intList.listIterator(3).hasNext());
    AssertFail.of(() -> intList.listIterator(-1));
    AssertFail.of(() -> intList.listIterator(4));
  }

  public void testListIteratorPrevious() {
    IntList intList = new IntList(new int[] { 0, 1, 2, 3, 4, 5 }).subList(2, 5);
    ListIterator<Integer> listIterator = intList.listIterator(1);
    assertEquals(listIterator.previousIndex(), 0);
    assertEquals(listIterator.nextIndex(), 2);
    assertTrue(listIterator.hasPrevious());
    assertEquals(listIterator.previous().intValue(), 2);
    assertFalse(listIterator.hasPrevious());
    AssertFail.of(() -> listIterator.previous());
    assertFalse(listIterator.hasPrevious());
    listIterator.next();
    assertTrue(listIterator.hasPrevious());
  }

  public void testSerializable() throws ClassNotFoundException, IOException {
    IntList intList = new IntList(new int[] { 0, 1, 2, 3, 4, 5 }).subList(2, 5);
    IntList copy = Serialization.copy(intList);
    assertEquals(copy, Arrays.asList(2, 3, 4));
  }
}
