// code by jph
package ch.alpine.tensor.ext.ref;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Objects;

public enum ClassHierarchy {
  ;
  /** Example: for cls == ArrayList.class
   * the returned deque has the stream/pop content
   * [AbstractCollection, AbstractList, ArrayList]
   * 
   * @param cls
   * @return hierarchy of given class starting with Object */
  public static Deque<Class<?>> of(Class<?> cls) {
    Deque<Class<?>> deque = new ArrayDeque<>();
    for (; !Objects.isNull(cls); cls = cls.getSuperclass())
      deque.push(cls); // prepends to the collection
    return deque;
  }
}
