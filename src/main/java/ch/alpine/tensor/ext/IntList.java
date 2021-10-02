// code by jph
package ch.alpine.tensor.ext;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.RandomAccess;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

/** API EXPERIMENTAL
 * 
 * implementation assumes that "list does not permit null elements".
 * 
 * In consequence, calls to certain functions with null as parameter
 * throw an exception. For example contains(null) */
/* package */ class IntList implements List<Integer>, RandomAccess, Serializable {
  /** @param array non-null
   * @return unmodifiable list */
  public static List<Integer> wrap(int[] array) {
    return new IntList(array, 0, array.length);
  }

  // ---
  private final int[] array;
  private final int ofs;
  private final int len;

  /** @param array non-null
   * @param ofs guaranteed to be non-negative
   * @param len non-negative */
  private IntList(int[] array, int ofs, int len) {
    if (len < 0)
      throw new IllegalArgumentException();
    this.array = array;
    this.ofs = ofs;
    this.len = len;
  }

  @Override
  public int size() {
    return len;
  }

  @Override
  public boolean isEmpty() {
    return size() == 0;
  }

  @Override
  public Integer get(int index) {
    if (index < 0)
      throw new IllegalArgumentException();
    if (len <= index)
      throw new IllegalArgumentException();
    return array[ofs + index];
  }

  @Override
  public IntList subList(int fromIndex, int toIndex) {
    if (fromIndex < 0)
      throw new IllegalArgumentException();
    if (len < fromIndex)
      throw new IllegalArgumentException();
    if (len < toIndex)
      throw new IllegalArgumentException();
    return new IntList(array, ofs + fromIndex, Math.subtractExact(toIndex, fromIndex));
  }

  @Override
  public Stream<Integer> stream() {
    return Arrays.stream(array).skip(ofs).limit(len).boxed();
  }

  @Override
  public Object[] toArray() {
    return stream().toArray();
  }

  @Override
  public Iterator<Integer> iterator() {
    return listIterator();
  }

  @Override
  public ListIterator<Integer> listIterator() {
    return listIterator(0);
  }

  @Override
  public ListIterator<Integer> listIterator(int index) {
    if (index < 0)
      throw new IllegalArgumentException();
    if (size() < index)
      throw new IllegalArgumentException();
    return new ListIterator<>() {
      int count = index;

      @Override
      public boolean hasNext() {
        return count < len;
      }

      @Override
      public Integer next() {
        if (hasNext())
          return array[ofs + (count++)];
        throw new NoSuchElementException();
      }

      @Override
      public boolean hasPrevious() {
        return 0 < count;
      }

      @Override
      public Integer previous() {
        if (hasPrevious())
          return array[ofs + (--count)];
        throw new NoSuchElementException();
      }

      @Override
      public int nextIndex() {
        return count + 1;
      }

      @Override
      public int previousIndex() {
        return count - 1;
      }

      @Override
      public void remove() {
        throw new UnsupportedOperationException();
      }

      @Override
      public void set(Integer e) {
        throw new UnsupportedOperationException();
      }

      @Override
      public void add(Integer e) {
        throw new UnsupportedOperationException();
      }
    };
  }

  @Override
  public int indexOf(Object object) {
    Objects.requireNonNull(object);
    for (int index = 0; index < len; ++index)
      if (object.equals(array[ofs + index]))
        return index;
    return -1;
  }

  @Override
  public int lastIndexOf(Object object) {
    Objects.requireNonNull(object); // list does not permit null elements
    for (int index = len - 1; 0 <= index; --index)
      if (object.equals(array[ofs + index]))
        return index;
    return -1;
  }

  @Override
  public boolean contains(Object object) {
    // throws exception if object == null even if stream is empty
    return stream().anyMatch(object::equals);
  }

  @Override
  public boolean containsAll(Collection<?> collection) {
    return collection.stream().allMatch(this::contains);
  }

  @Override
  public int hashCode() {
    int hashCode = 1;
    for (int i = ofs; i < ofs + len; ++i)
      hashCode = 31 * hashCode + array[i];
    return hashCode;
  }

  @Override
  public boolean equals(Object object) {
    if (object instanceof List) {
      List<?> list = (List<?>) object;
      if (list.size() == len) {
        AtomicInteger atomicInteger = new AtomicInteger(ofs);
        return list.stream().allMatch(value -> value.equals(array[atomicInteger.getAndIncrement()]));
      }
    }
    return false;
  }

  // ---
  @Override
  public <T> T[] toArray(T[] a) {
    throw new UnsupportedOperationException();
  }

  @Override
  public boolean add(Integer e) {
    throw new UnsupportedOperationException();
  }

  @Override
  public boolean remove(Object o) {
    throw new UnsupportedOperationException();
  }

  @Override
  public boolean addAll(Collection<? extends Integer> c) {
    throw new UnsupportedOperationException();
  }

  @Override
  public boolean addAll(int index, Collection<? extends Integer> c) {
    throw new UnsupportedOperationException();
  }

  @Override
  public boolean removeAll(Collection<?> c) {
    throw new UnsupportedOperationException();
  }

  @Override
  public boolean retainAll(Collection<?> c) {
    throw new UnsupportedOperationException();
  }

  @Override
  public void clear() {
    throw new UnsupportedOperationException();
  }

  @Override
  public Integer set(int index, Integer element) {
    throw new UnsupportedOperationException();
  }

  @Override
  public void add(int index, Integer element) {
    throw new UnsupportedOperationException();
  }

  @Override
  public Integer remove(int index) {
    throw new UnsupportedOperationException();
  }
}
