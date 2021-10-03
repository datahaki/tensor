// code by jph
package ch.alpine.tensor;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.NavigableMap;
import java.util.Objects;
import java.util.TreeMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import ch.alpine.tensor.alg.Dimensions;
import ch.alpine.tensor.ext.Integers;

/** API EXPERIMENTAL */
/* package */ class SparseArray extends AbstractTensor implements Serializable {
  private final List<Integer> size;
  private final Scalar fallback;
  private final NavigableMap<Integer, Tensor> navigableMap;

  public SparseArray(List<Integer> size, Scalar fallback, NavigableMap<Integer, Tensor> navigableMap) {
    Integers.requirePositive(size.size());
    this.size = size;
    this.fallback = fallback;
    this.navigableMap = navigableMap;
  }

  public SparseArray(List<Integer> size, Scalar fallback) {
    this(size, fallback, new TreeMap<>());
  }

  @Override
  public Tensor unmodifiable() {
    throw new UnsupportedOperationException();
  }

  @Override
  public Tensor copy() {
    return new SparseArray(size, fallback, navigableMap.entrySet().stream().collect(Collectors.toMap( //
        Entry::getKey, entry -> entry.getValue().copy(), (e1, e2) -> null, TreeMap::new)));
  }

  @Override
  protected Tensor byRef(int i) {
    assertInRange(i);
    Tensor tensor = navigableMap.get(i);
    if (Objects.isNull(tensor)) {
      if (size.size() == 1)
        return fallback;
      return new SparseArray(size.subList(1, size.size()), fallback);
    }
    return tensor;
  }

  private void assertInRange(int i) {
    if (i < 0 || length() <= i)
      throw new IllegalArgumentException();
  }

  @Override
  public void set(Tensor tensor, List<Integer> index) {
    Integers.requirePositive(index.size());
    int head = index.get(0);
    assertInRange(head);
    if (index.size() == 1)
      navigableMap.put(head, tensor.copy());
    else {
      Tensor entry = navigableMap.get(head);
      if (Objects.isNull(entry))
        navigableMap.put(head, new SparseArray(size.subList(1, size.size()), fallback));
      navigableMap.get(head).set(tensor, index.subList(1, index.size()));
    }
  }

  @Override
  public <T extends Tensor> void set(Function<T, ? extends Tensor> function, int... index) {
    // TODO Auto-generated method stub
  }

  @Override
  public <T extends Tensor> void set(Function<T, ? extends Tensor> function, List<Integer> index) {
    // TODO Auto-generated method stub
  }

  @Override
  public Tensor append(Tensor tensor) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public int length() {
    return size.get(0);
  }

  @Override
  public Tensor extract(int fromIndex, int toIndex) {
    assertInRange(fromIndex);
    assertInRange(toIndex);
    int len = Integers.requirePositiveOrZero(toIndex - fromIndex);
    if (len == 0)
      return Tensors.empty();
    List<Integer> newl = new ArrayList<>(size);
    newl.set(0, len);
    return new SparseArray(newl, fallback, navigableMap.entrySet().stream().collect(Collectors.toMap( //
        entry -> entry.getKey() - fromIndex, entry -> entry.getValue().copy(), (e1, e2) -> null, TreeMap::new)));
  }

  @Override
  public Tensor negate() {
    return new SparseArray(size, fallback, navigableMap.entrySet().stream().collect(Collectors.toMap( //
        Entry::getKey, entry -> entry.getValue().negate(), (e1, e2) -> null, TreeMap::new)));
  }

  @Override
  public Tensor add(Tensor tensor) {
    Integers.requireEquals(length(), tensor.length());
    AtomicInteger i = new AtomicInteger();
    List<Integer> subList = size.subList(1, size.size());
    // TODO this produces full array :-(
    return Tensor.of(tensor.stream().map(entry -> {
      int index = i.getAndIncrement();
      if (navigableMap.containsKey(index))
        return navigableMap.get(index).add(entry);
      if (Dimensions.of(entry).equals(subList))
        return entry.map(fallback::add);
      throw TensorRuntimeException.of(entry);
    }));
  }

  @Override
  public Tensor subtract(Tensor tensor) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Tensor pmul(Tensor tensor) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Tensor multiply(Scalar scalar) {
    return new SparseArray(size, fallback, navigableMap.entrySet().stream().collect(Collectors.toMap( //
        Entry::getKey, entry -> entry.getValue().multiply(scalar), (e1, e2) -> null, TreeMap::new)));
  }

  @Override
  public Tensor divide(Scalar scalar) {
    return new SparseArray(size, fallback, navigableMap.entrySet().stream().collect(Collectors.toMap( //
        Entry::getKey, entry -> entry.getValue().divide(scalar), (e1, e2) -> null, TreeMap::new)));
  }

  @Override
  public Tensor map(Function<Scalar, ? extends Tensor> function) {
    return new SparseArray(size, (Scalar) function.apply(fallback), navigableMap.entrySet().stream().collect(Collectors.toMap( //
        Entry::getKey, entry -> entry.getValue().map(function), (e1, e2) -> null, TreeMap::new)));
  }

  @Override
  public Tensor block(List<Integer> fromIndex, List<Integer> dimensions) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Stream<Tensor> stream() {
    return IntStream.range(0, length()).mapToObj(this::byRef);
  }

  @Override
  public Iterator<Tensor> iterator() {
    return new Iterator<>() {
      int count = 0;

      @Override
      public boolean hasNext() {
        return count < length();
      }

      @Override
      public Tensor next() {
        return byRef(count++);
      }
    };
  }
}
