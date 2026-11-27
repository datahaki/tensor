// code by gemini
package ch.alpine.tensor.ext;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class SwapRemoveList<T> implements Serializable {
  private final List<T> list = new ArrayList<>();

  public void add(T value) {
    list.add(value);
  }

  public T get(int index) {
    Objects.checkIndex(index, list.size());
    return list.get(index);
  }

  public T removeByIndex(int index) {
    int size = list.size();
    Objects.checkIndex(index, size);
    int tail = size - 1;
    T last = list.remove(tail);
    if (index == tail)
      return last;
    return list.set(index, last);
  }

  public int size() {
    return list.size();
  }

  public boolean isEmpty() {
    return list.isEmpty();
  }

  public void clear() {
    list.clear();
  }
}
