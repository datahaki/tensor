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
import java.util.function.Function;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import ch.alpine.tensor.ext.Integers;

/** Hint:
 * Mathematica::Normal[sparse] is
 * <pre>
 * Tensor normal = Array.of(sparse::get, Dimensions.of(sparse));
 * </pre> */
/* package */ class SparseArray extends AbstractTensor implements Serializable {
  /** the content of size is not modified by sparse array */
  private final List<Integer> size;
  private final Scalar fallback;
  private final NavigableMap<Integer, Tensor> navigableMap;

  public SparseArray(List<Integer> size, Scalar fallback, NavigableMap<Integer, Tensor> navigableMap) {
    this.size = size;
    this.fallback = fallback;
    this.navigableMap = navigableMap;
  }

  public SparseArray(List<Integer> size, Scalar fallback) {
    this(size, fallback, new TreeMap<>());
  }

  @Override // from Tensor
  public Tensor unmodifiable() {
    throw new UnsupportedOperationException();
  }

  @Override // from Tensor
  public Tensor copy() {
    return new SparseArray(size, fallback, navigableMap.entrySet().stream() //
        .collect(_map(Entry::getKey, entry -> entry.getValue().copy())));
  }

  @Override // from AbstractTensor
  protected Tensor byRef(int i) {
    SparseArrays.requireInRange(i, length());
    Tensor tensor = navigableMap.get(i);
    if (Objects.isNull(tensor)) {
      if (size.size() == 1)
        return fallback;
      return new SparseArray(size.subList(1, size.size()), fallback);
    }
    return tensor;
  }

  @Override // from Tensor
  public void set(Tensor tensor, List<Integer> index) {
    Integers.requirePositive(index.size());
    int head = index.get(0);
    SparseArrays.requireInRange(head, length());
    if (index.size() == 1)
      navigableMap.put(head, tensor.copy());
    else {
      Tensor entry = navigableMap.get(head);
      if (Objects.isNull(entry))
        navigableMap.put(head, new SparseArray(size.subList(1, size.size()), fallback));
      navigableMap.get(head).set(tensor, index.subList(1, index.size()));
    }
  }

  @Override // from Tensor
  public <T extends Tensor> void set(Function<T, ? extends Tensor> function, List<Integer> index) {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException();
  }

  @Override // from Tensor
  public Tensor append(Tensor tensor) {
    throw new UnsupportedOperationException(); // dimensions of sparse array may not change
  }

  @Override // from Tensor
  public int length() {
    return size.get(0);
  }

  @Override // from Tensor
  public Tensor extract(int fromIndex, int toIndex) {
    SparseArrays.requireInRange(fromIndex, length());
    SparseArrays.requireInRange(toIndex, length());
    int len = Integers.requirePositiveOrZero(toIndex - fromIndex);
    if (len == 0)
      return Tensors.empty();
    List<Integer> newl = new ArrayList<>(size);
    newl.set(0, len);
    return new SparseArray(newl, fallback, navigableMap.subMap(fromIndex, toIndex).entrySet().stream() //
        .collect(_map(entry -> entry.getKey() - fromIndex, entry -> entry.getValue().copy())));
  }

  @Override // from Tensor
  public Tensor negate() {
    return new SparseArray(size, fallback.negate(), navigableMap.entrySet().stream() //
        .collect(_map(Entry::getKey, entry -> entry.getValue().negate())));
  }

  @Override // from Tensor
  public Tensor add(Tensor tensor) {
    if (tensor instanceof SparseArray) {
      Integers.requireEquals(length(), tensor.length());
      SparseArray sparseArray = (SparseArray) tensor;
      return new SparseArray(size, fallback.add(sparseArray.fallback), //
          Stream.concat(navigableMap.keySet().stream(), sparseArray.navigableMap.keySet().stream()) //
              .distinct().collect(_map(i -> i, i -> byRef(i).add(sparseArray.byRef(i)))));
    }
    return tensor.add(this);
  }

  @Override // from Tensor
  public Tensor subtract(Tensor tensor) {
    if (tensor instanceof SparseArray) {
      Integers.requireEquals(length(), tensor.length());
      SparseArray sparseArray = (SparseArray) tensor;
      return new SparseArray(size, fallback.subtract(sparseArray.fallback), //
          Stream.concat(navigableMap.keySet().stream(), sparseArray.navigableMap.keySet().stream()) //
              .distinct().collect(_map(i -> i, i -> byRef(i).subtract(sparseArray.byRef(i)))));
    }
    return tensor.negate().add(this);
  }

  @Override // from Tensor
  public Tensor pmul(Tensor tensor) {
    Integers.requireEquals(length(), tensor.length());
    return new SparseArray(size, fallback, navigableMap.entrySet().stream() //
        .collect(_map(Entry::getKey, entry -> entry.getValue().pmul(tensor.get(entry.getKey())))));
  }

  @Override // from Tensor
  public Tensor multiply(Scalar scalar) {
    return new SparseArray(size, fallback.multiply(scalar), navigableMap.entrySet().stream() //
        .collect(_map(Entry::getKey, entry -> entry.getValue().multiply(scalar))));
  }

  @Override // from Tensor
  public Tensor divide(Scalar scalar) {
    return new SparseArray(size, fallback.divide(scalar), navigableMap.entrySet().stream() //
        .collect(_map(Entry::getKey, entry -> entry.getValue().divide(scalar))));
  }

  @Override // from Tensor
  public Tensor map(Function<Scalar, ? extends Tensor> function) {
    Scalar mappedfb = (Scalar) function.apply(fallback);
    if (Scalars.nonZero(mappedfb))
      throw TensorRuntimeException.of(mappedfb);
    return new SparseArray(size, mappedfb, navigableMap.entrySet().stream() //
        .collect(_map(Entry::getKey, entry -> entry.getValue().map(function))));
  }

  @Override // from Tensor
  public Tensor block(List<Integer> fromIndex, List<Integer> dimensions) {
    int depth = Integers.requireEquals(fromIndex.size(), dimensions.size());
    if (depth == 0)
      return this;
    int head = fromIndex.get(0);
    SparseArrays.requireInRange(head, length());
    int len = Integers.requirePositiveOrZero(dimensions.get(0));
    if (len == 0)
      return Tensors.empty();
    List<Integer> _from = fromIndex.subList(1, depth);
    List<Integer> _dims = dimensions.subList(1, depth);
    return new SparseArray( //
        Stream.concat(dimensions.stream(), size.stream().skip(depth)).collect(Collectors.toList()), //
        fallback, navigableMap.subMap(head, head + len).entrySet().stream() //
            .collect(_map(entry -> entry.getKey() - head, entry -> entry.getValue().block(_from, _dims))));
  }

  @Override // from Tensor
  public Stream<Tensor> stream() {
    return IntStream.range(0, length()).mapToObj(this::byRef);
  }

  @Override // from Iterable
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

  @Override // from Object
  public String toString() {
    return size.toString() + " " + navigableMap.entrySet().stream() //
        .map(entry -> String.format("%d -> %s", entry.getKey(), entry.getValue())) //
        .collect(StaticHelper.EMBRACE);
  }

  // helper function
  private static <T> Collector<T, ?, NavigableMap<Integer, Tensor>> _map( //
      Function<? super T, Integer> keyMapper, //
      Function<? super T, Tensor> valueMapper) {
    return Collectors.toMap(keyMapper, valueMapper, (e1, e2) -> null, TreeMap::new);
  }
}
