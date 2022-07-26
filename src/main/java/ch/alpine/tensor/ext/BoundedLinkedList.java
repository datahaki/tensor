// code by jph
// adapted from http://www.java2s.com/Code/Android/UI/BoundedLinkedList.htm
package ch.alpine.tensor.ext;

import java.io.Serializable;
import java.util.Collection;
import java.util.LinkedList;

/** @param <E> the type of elements held in this collection
 * 
 * Applications:
 * {@link BoundedLinkedList} is used in FIR, and IIR filters
 * in repository sophus.
 * 
 * implements {@link Serializable}
 * 
 * @implSpec
 * implementation is not thread safe */
public class BoundedLinkedList<E> extends LinkedList<E> {
  private final int maxSize;

  /** @param maxSize non-negative
   * @throws Exception if maxSize is negative */
  public BoundedLinkedList(int maxSize) {
    if (maxSize < 0)
      throw new IllegalArgumentException(Integer.toString(maxSize));
    this.maxSize = maxSize;
  }

  @Override // from LinkedList
  public boolean add(E object) {
    super.add(object);
    if (maxSize < size())
      removeFirst();
    return !isEmpty();
  }

  @Override // from LinkedList
  public void add(int location, E object) {
    super.add(location, object);
    if (maxSize < size())
      removeFirst();
  }

  @Override // from LinkedList
  public boolean addAll(Collection<? extends E> collection) {
    throw new UnsupportedOperationException();
  }

  @Override // from LinkedList
  public boolean addAll(int index, Collection<? extends E> collection) {
    throw new UnsupportedOperationException();
  }

  @Override // from LinkedList
  public void addFirst(E object) {
    throw new UnsupportedOperationException();
  }

  @Override // from LinkedList
  public void addLast(E object) {
    add(object);
  }
}
