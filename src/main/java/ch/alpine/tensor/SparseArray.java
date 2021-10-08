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

import ch.alpine.tensor.alg.ConstantArray;
import ch.alpine.tensor.alg.Dimensions;
import ch.alpine.tensor.ext.Integers;
import ch.alpine.tensor.ext.Lists;
import ch.alpine.tensor.ext.PackageTestAccess;

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
    if (Objects.isNull(tensor))
      return size.size() == 1 //
          ? fallback
          : new SparseArray(Lists.rest(size), fallback);
    return tensor;
  }

  @Override // from Tensor
  public void set(Tensor tensor, List<Integer> index) {
    List<Integer> _size = Lists.rest(size);
    int head = index.get(0);
    // TODO does not handle Tensor.ALL yet
    SparseArrays.requireInRange(head, length());
    if (index.size() == 1)
      _set(head, tensor.copy(), _size);
    else
      navigableMap.computeIfAbsent(head, i -> new SparseArray(_size, fallback)) //
          .set(tensor, Lists.rest(index));
  }

  @SuppressWarnings("unchecked")
  @Override // from Tensor
  public <T extends Tensor> void set(Function<T, ? extends Tensor> function, List<Integer> index) {
    List<Integer> _size = Lists.rest(size);
    int head = index.get(0);
    if (index.size() == 1) // terminal case
      if (head == ALL)
        IntStream.range(0, length()).forEach(i -> _set(i, function.apply((T) byRef(i)).copy(), _size));
      else
        _set(head, function.apply((T) byRef(head)).copy(), _size);
    else {
      List<Integer> _index = Lists.rest(index);
      if (head == ALL)
        IntStream.range(0, length()) //
            .forEach(i -> navigableMap.computeIfAbsent(i, j -> new SparseArray(_size, fallback)).set(function, _index));
      else {
        navigableMap.computeIfAbsent(head, j -> {
          SparseArrays.requireInRange(head, length());
          return new SparseArray(_size, fallback);
        }).set(function, _index);
      }
    }
  }

  /** @param index
   * @param tensor with array structure and dimensions equals to given list
   * @param list */
  private void _set(int index, Tensor tensor, List<Integer> list) {
    Dimensions dimensions = new Dimensions(tensor);
    if (dimensions.isArray() && dimensions.list().equals(list)) {
      // TODO not efficient implementation to check for fallback array equality
      if (tensor.equals(ConstantArray.of(fallback, list)))
        navigableMap.remove(index);
      else
        navigableMap.put(index, tensor);
    } else
      throw TensorRuntimeException.of(tensor);
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
    int len0 = Integers.requirePositiveOrZero(toIndex - fromIndex);
    if (len0 == 0)
      return Tensors.empty();
    List<Integer> newl = new ArrayList<>(size);
    newl.set(0, len0);
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
  public Tensor block(List<Integer> ofs, List<Integer> len) {
    int depth = Integers.requireEquals(ofs.size(), len.size());
    if (depth == 0)
      return this;
    int head = ofs.get(0);
    SparseArrays.requireInRange(head, length());
    int len0 = Integers.requirePositiveOrZero(len.get(0));
    if (len0 == 0)
      return Tensors.empty();
    List<Integer> _ofs = Lists.rest(ofs);
    List<Integer> _len = Lists.rest(len);
    return new SparseArray( //
        Stream.concat(len.stream(), size.stream().skip(depth)).collect(Collectors.toList()), //
        fallback, navigableMap.subMap(head, head + len0).entrySet().stream() //
            .collect(_map(entry -> entry.getKey() - head, entry -> entry.getValue().block(_ofs, _len))));
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

  @PackageTestAccess
  static <T> Collector<T, ?, NavigableMap<Integer, Tensor>> _map( //
      Function<? super T, Integer> keyMapper, //
      Function<? super T, Tensor> valueMapper) {
    return Collectors.toMap(keyMapper, valueMapper, (e1, e2) -> null, TreeMap::new);
  }
}
