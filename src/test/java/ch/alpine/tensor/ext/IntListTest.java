// code by jph
package ch.alpine.tensor.ext;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.RandomAccess;
import java.util.stream.Collectors;

import ch.alpine.tensor.usr.AssertFail;
import junit.framework.TestCase;

public class IntListTest extends TestCase {
  public void testEmpty() {
    List<Integer> intList = IntList.wrap(new int[] {});
    assertEquals(intList.size(), 0);
    assertTrue(intList.isEmpty());
  }

  public void testConstructEmpty() {
    List<Integer> intList = IntList.wrap(new int[] { 0, 1, 2, 3, 4, 5 }).subList(1, 1);
    assertEquals(intList.size(), 0);
    assertTrue(intList.isEmpty());
  }

  public void testSimple() {
    List<Integer> intList = IntList.wrap(new int[] { 0, 1, 2, 3, 4, 5 });
    assertEquals(intList.size(), 6);
    assertFalse(intList.isEmpty());
    for (int index = 0; index < intList.size(); ++index)
      assertEquals(intList.get(index), (Integer) index);
  }

  public void testConstructFails() {
    AssertFail.of(() -> IntList.wrap(null));
    List<Integer> intList = IntList.wrap(new int[] { 0, 1, 2, 3, 4, 5 }).subList(4, 6);
    assertEquals(intList.size(), 2);
    assertFalse(intList.isEmpty());
  }

  public void testGetFail() {
    List<Integer> intList = IntList.wrap(new int[] { 0, 1, 2, 3, 4, 5 });
    AssertFail.of(() -> intList.get(-1));
    List<Integer> subList = intList.subList(2, 5);
    AssertFail.of(() -> subList.get(-1));
    AssertFail.of(() -> subList.get(4));
  }

  public void testSublist() {
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

  public void testSublistFail0() {
    List<Integer> intList = IntList.wrap(new int[] { 0, 1, 2, 3, 4, 5 }).subList(2, 5);
    assertEquals(intList.subList(3, 3).size(), 0);
    AssertFail.of(() -> intList.subList(4, 4));
    AssertFail.of(() -> intList.subList(1, -1));
  }

  public void testSublistFail1() {
    List<Integer> intList = IntList.wrap(new int[] { 0, 1, 2, 3, 4, 5 });
    assertTrue(intList.subList(2, 2).isEmpty());
    AssertFail.of(() -> intList.subList(2, 7));
    AssertFail.of(() -> intList.subList(2, 1));
    AssertFail.of(() -> intList.subList(-1, 1));
  }

  public void testSublistFail2() {
    List<Integer> intList = IntList.wrap(new int[] { 0, 1, 2, 3, 4, 5 });
    AssertFail.of(() -> intList.subList(1, -1));
  }

  public void testEquals() {
    List<Integer> intList = IntList.wrap(new int[] { 2, 3, 4 });
    assertEquals(intList, Arrays.asList(2, 3, 4));
    assertTrue(Arrays.asList(2, 3, 4).equals(intList));
    assertFalse(intList.equals(Arrays.asList(2, 3)));
    assertFalse(intList.equals(Arrays.asList(2, 3, 5)));
    assertFalse(intList.equals(null));
    assertFalse(Arrays.asList(2, 3).equals(intList));
    assertFalse(Arrays.asList(2, 3, 5).equals(intList));
  }

  public void testFor() {
    List<Integer> intList = IntList.wrap(new int[] { 0, 1, 2, 3, 4, 5 }).subList(2, 5);
    for (Integer val : intList) {
      assertEquals(val.intValue(), 2);
      break;
    }
  }

  public void testContains() {
    List<Integer> intList = IntList.wrap(new int[] { 0, 1, 2, 3, 4, 5 }).subList(2, 5);
    assertFalse(intList.contains(1));
    assertTrue(intList.contains(2));
    assertTrue(intList.contains(3));
    assertTrue(intList.contains(4));
    assertFalse(intList.contains(5));
    AssertFail.of(() -> intList.contains(null));
  }

  public void testContainsEmptyFail() {
    List<Integer> intList = IntList.wrap(new int[] {});
    assertFalse(intList.contains(5));
    AssertFail.of(() -> intList.contains(null));
  }

  public void testContainsAll() {
    List<Integer> intList = IntList.wrap(new int[] { 0, 1, 2, 3, 4, 5 }).subList(2, 5);
    assertFalse(intList.containsAll(Arrays.asList(1, 2)));
    assertTrue(intList.containsAll(Arrays.asList(2, 2, 4)));
  }

  public void testIndexOf() {
    List<Integer> intList = IntList.wrap(new int[] { 0, 1, 2, 3, 4, 5 }).subList(2, 5);
    assertEquals(intList.indexOf(2), 0);
    assertEquals(intList.indexOf(3), 1);
    assertEquals(intList.indexOf(4), 2);
    assertEquals(intList.indexOf(5), -1);
    AssertFail.of(() -> intList.indexOf(null));
  }

  public void testLastIndexOf() {
    List<Integer> intList = IntList.wrap(new int[] { 0, 1, 2, 3, 2, 4, 5 }).subList(2, 6);
    assertEquals(intList.lastIndexOf(2), 2);
    assertEquals(intList.lastIndexOf(3), 1);
    assertEquals(intList.lastIndexOf(4), 3);
    assertEquals(intList.lastIndexOf(5), -1);
    AssertFail.of(() -> intList.lastIndexOf(null));
  }

  public void testAddRemoveFail() {
    List<Integer> intList = IntList.wrap(new int[] { 0, 1, 2, 3, 2, 4, 5 }).subList(2, 6);
    AssertFail.of(() -> intList.add(3));
    AssertFail.of(() -> intList.add(0, 0));
    AssertFail.of(() -> intList.remove(3));
    AssertFail.of(() -> intList.remove(Integer.valueOf(3)));
    AssertFail.of(() -> intList.addAll(Arrays.asList(2, 3, 4)));
    AssertFail.of(() -> intList.addAll(0, Arrays.asList(2, 3, 4)));
    AssertFail.of(() -> intList.removeAll(Arrays.asList(2, 3, 4)));
    AssertFail.of(() -> intList.retainAll(Arrays.asList(2, 3, 4)));
    AssertFail.of(() -> intList.clear());
    AssertFail.of(() -> intList.set(0, 0));
    AssertFail.of(() -> intList.toArray(new Integer[10]));
  }

  public void testHashCode() {
    List<Integer> intList = IntList.wrap(new int[] { 2, 3, 4 });
    assertEquals(intList.hashCode(), Arrays.asList(2, 3, 4).hashCode());
    assertEquals(IntList.wrap(new int[] {}).hashCode(), Arrays.asList().hashCode());
  }

  public void testToArray() {
    List<Integer> intList = IntList.wrap(new int[] { 2, 3, 4 });
    ArrayList<Integer> arrayList = new ArrayList<>(intList);
    assertEquals(arrayList, Arrays.asList(2, 3, 4));
  }

  public void testInterator() {
    List<Integer> intList = IntList.wrap(new int[] { 0, 1, 2, 3, 4, 5 }).subList(2, 5);
    Iterator<Integer> iterator = intList.iterator();
    assertTrue(iterator.hasNext());
    AssertFail.of(() -> iterator.remove());
    assertEquals(iterator.next().intValue(), 2);
    assertTrue(iterator.hasNext());
    assertEquals(iterator.next().intValue(), 3);
    assertTrue(iterator.hasNext());
    assertEquals(iterator.next().intValue(), 4);
    assertFalse(iterator.hasNext());
    AssertFail.of(() -> iterator.next());
  }

  public void testListIterator() {
    List<Integer> intList = IntList.wrap(new int[] { 0, 1, 2, 3, 4, 5 }).subList(2, 5);
    assertTrue(intList.listIterator(0).hasNext());
    assertTrue(intList.listIterator(2).hasNext());
    assertFalse(intList.listIterator(3).hasNext());
    AssertFail.of(() -> intList.listIterator(-1));
    AssertFail.of(() -> intList.listIterator(4));
  }

  public void testListIteratorPrevious() {
    List<Integer> intList = IntList.wrap(new int[] { 0, 1, 2, 3, 4, 5 }).subList(2, 5);
    ListIterator<Integer> listIterator = intList.listIterator(1);
    assertEquals(listIterator.previousIndex(), 0);
    AssertFail.of(() -> listIterator.add(3));
    AssertFail.of(() -> listIterator.set(3));
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
    List<Integer> intList = IntList.wrap(new int[] { 0, 1, 2, 3, 4, 5 }).subList(2, 5);
    List<Integer> copy = Serialization.copy(intList);
    assertEquals(copy, Arrays.asList(2, 3, 4));
    assertTrue(intList instanceof RandomAccess);
  }

  public void testToString() {
    String s1 = Arrays.asList(10, 2, 0, -4).toString();
    String s2 = Integers.asList(new int[] { 10, 2, 0, -4 }).toString();
    assertEquals(s1, s2);
  }
}
