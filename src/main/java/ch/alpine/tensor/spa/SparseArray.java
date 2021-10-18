// code by jph
package ch.alpine.tensor.spa;

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
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import ch.alpine.tensor.AbstractTensor;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.TensorRuntimeException;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.alg.Dimensions;
import ch.alpine.tensor.alg.Dot;
import ch.alpine.tensor.ext.Integers;
import ch.alpine.tensor.ext.Lists;
import ch.alpine.tensor.ext.MergeIllegal;

/** The string expression is as in mathematica except that the tensor library starts indexing at 0.
 * Example:
 * <pre>
 * SparseArray[{{0, 0}->1, {0, 2}->3, {1, 0}->5, {1, 1}->6, {1, 2}->8, {2, 1}->2, {2, 2}->9, {2, 4}->4}, {3, 5}]
 * </pre>
 * 
 * <p>inspired by
 * <a href="https://reference.wolfram.com/language/ref/SparseArray.html">SparseArray</a> */
public class SparseArray extends AbstractTensor implements Serializable {
  /** @param fallback zero element, for instance {@link RealScalar#ZERO}
   * @param dimensions with non-negative values
   * @return empty sparse array with given dimensions
   * @throws Exception if fallback element is not zero */
  public static Tensor of(Scalar fallback, int... dimensions) {
    return dimensions.length == 0 //
        ? fallback
        : new SparseArray(checkFallback(fallback), //
            Integers.asList(IntStream.of(dimensions).map(Integers::requirePositiveOrZero).toArray()));
  }

  // ---
  private final Scalar fallback;
  /** the content of size is not modified by sparse array */
  private final List<Integer> size;
  private final NavigableMap<Integer, Tensor> navigableMap;

  private SparseArray(Scalar fallback, List<Integer> size) {
    this(fallback, size, new TreeMap<>());
  }

  private SparseArray(Scalar fallback, List<Integer> size, NavigableMap<Integer, Tensor> navigableMap) {
    this.fallback = fallback;
    this.size = size;
    this.navigableMap = navigableMap;
  }

  @Override // from Tensor
  public Tensor unmodifiable() {
    throw new UnsupportedOperationException();
  }

  @Override // from Tensor
  public Tensor copy() {
    return new SparseArray(fallback, size, navigableMap.entrySet().stream() //
        .collect(_map(Entry::getKey, entry -> entry.getValue().copy())));
  }

  @Override // from AbstractTensor
  protected Tensor byRef(int i) {
    Tensor tensor = navigableMap.get(requireInRange(i));
    if (Objects.isNull(tensor))
      return size.size() == 1 //
          ? fallback
          : new SparseArray(fallback, Lists.rest(size));
    return tensor;
  }

  @Override // from Tensor
  public void set(Tensor tensor, List<Integer> index) {
    int head = index.get(0);
    List<Integer> _size = Lists.rest(size);
    if (index.size() == 1) // terminal case
      if (head == ALL) {
        Integers.requireEquals(size.get(0), tensor.length());
        AtomicInteger i = new AtomicInteger();
        tensor.stream().map(Tensor::copy).forEach(entry -> _set(i.getAndIncrement(), entry, _size));
      } else
        _set(requireInRange(head), tensor.copy(), _size);
    else {
      List<Integer> _index = Lists.rest(index);
      if (head == ALL) {
        Integers.requireEquals(size.get(0), tensor.length());
        AtomicInteger i = new AtomicInteger();
        tensor.stream().forEach(entry -> //
        navigableMap.computeIfAbsent(i.getAndIncrement(), j -> new SparseArray(fallback, _size)) //
            .set(entry, _index));
      } else {
        requireInRange(head);
        navigableMap.computeIfAbsent(head, j -> new SparseArray(fallback, _size)).set(tensor, _index);
      }
    }
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
            .forEach(i -> navigableMap.computeIfAbsent(i, j -> new SparseArray(fallback, _size)).set(function, _index));
      else
        navigableMap.computeIfAbsent(requireInRange(head), j -> new SparseArray(fallback, _size)).set(function, _index);
    }
  }

  /** @param index
   * @param tensor with array structure and dimensions equals to given list
   * @param list */
  private void _set(int index, Tensor tensor, List<Integer> list) {
    if (new Dimensions(tensor).isArrayWith(list::equals))
      navigableMap.put(index, tensor);
    else
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
    requireInRange(fromIndex);
    Integers.requireLessEquals(toIndex, length());
    int len0 = Integers.requirePositiveOrZero(toIndex - fromIndex);
    if (len0 == 0)
      return Tensors.empty();
    List<Integer> newl = new ArrayList<>(size);
    newl.set(0, len0);
    return new SparseArray(fallback, newl, navigableMap.subMap(fromIndex, toIndex).entrySet().stream() //
        .collect(_map(entry -> entry.getKey() - fromIndex, entry -> entry.getValue().copy())));
  }

  @Override // from Tensor
  public Tensor negate() {
    return new SparseArray(fallback.negate(), size, navigableMap.entrySet().stream() //
        .collect(_map(Entry::getKey, entry -> entry.getValue().negate())));
  }

  @Override // from Tensor
  public Tensor add(Tensor tensor) {
    if (tensor instanceof SparseArray) {
      Integers.requireEquals(length(), tensor.length());
      SparseArray sparseArray = (SparseArray) tensor;
      return new SparseArray(fallback.add(sparseArray.fallback), size, //
          Stream.concat(navigableMap.keySet().stream(), sparseArray.navigableMap.keySet().stream()) //
              .distinct().collect(_map(i -> i, i -> byRef(i).add(sparseArray.byRef(i))))).collapse();
    }
    return tensor.add(this);
  }

  @Override // from Tensor
  public Tensor subtract(Tensor tensor) {
    if (tensor instanceof SparseArray) {
      Integers.requireEquals(length(), tensor.length());
      SparseArray sparseArray = (SparseArray) tensor;
      return new SparseArray(fallback.subtract(sparseArray.fallback), size, //
          Stream.concat(navigableMap.keySet().stream(), sparseArray.navigableMap.keySet().stream()) //
              .distinct().collect(_map(i -> i, i -> byRef(i).subtract(sparseArray.byRef(i))))).collapse();
    }
    return tensor.negate().add(this);
  }

  @Override // from Tensor
  public Tensor pmul(Tensor tensor) {
    Integers.requireEquals(length(), tensor.length());
    return new SparseArray(fallback, size, navigableMap.entrySet().stream() //
        .collect(_map(Entry::getKey, entry -> entry.getValue().pmul(tensor.get(entry.getKey()))))).collapse();
  }

  @Override // from Tensor
  public Tensor multiply(Scalar scalar) {
    return new SparseArray(checkFallback(fallback.multiply(scalar)), size, //
        navigableMap.entrySet().stream().collect(_map(Entry::getKey, entry -> entry.getValue().multiply(scalar))));
  }

  @Override // from Tensor
  public Tensor divide(Scalar scalar) {
    return new SparseArray(checkFallback(fallback.divide(scalar)), size, //
        navigableMap.entrySet().stream().collect(_map(Entry::getKey, entry -> entry.getValue().divide(scalar))));
  }

  @Override // from Tensor
  public Tensor dot(Tensor tensor) {
    if (size.size() == 1)
      return navigableMap.entrySet().stream() //
          .map(entry -> tensor.get(entry.getKey()).multiply((Scalar) entry.getValue())) //
          .reduce(Tensor::add) //
          .orElseGet(() -> {
            List<Integer> list = Dot.combine(size, Dimensions.of(tensor));
            return list.isEmpty() ? fallback : new SparseArray(fallback, list);
          });
    return new SparseArray(fallback, Dot.combine(size, Dimensions.of(tensor)), //
        navigableMap.entrySet().stream().collect(_map(Entry::getKey, entry -> entry.getValue().dot(tensor)))).collapse();
  }

  @Override // from Tensor
  public Tensor map(Function<Scalar, ? extends Tensor> function) {
    return new SparseArray(checkFallback((Scalar) function.apply(fallback)), size, navigableMap.entrySet() //
        .stream().collect(_map(Entry::getKey, entry -> entry.getValue().map(function)))).collapse();
  }

  @Override // from Tensor
  public Tensor block(List<Integer> ofs, List<Integer> len) {
    int depth = Integers.requireEquals(ofs.size(), len.size());
    if (depth == 0)
      return this;
    int head = requireInRange(ofs.get(0));
    int len0 = Integers.requirePositiveOrZero(len.get(0));
    if (len0 == 0)
      return Tensors.empty();
    List<Integer> _ofs = Lists.rest(ofs);
    List<Integer> _len = Lists.rest(len);
    return new SparseArray(fallback, //
        Stream.concat(len.stream(), size.stream().skip(depth)).collect(Collectors.toList()), //
        navigableMap.subMap(head, head + len0).entrySet().stream() //
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
    return getClass().getSimpleName() + '[' + visit(new SparseArrayToString()) + ", " + Tensors.vector(size) + ", " + fallback + ']';
  }

  /** @param sparseEntryVisitor
   * @return result provided by {@link SparseEntryVisitor#result()} */
  public <T> T visit(SparseEntryVisitor<T> sparseEntryVisitor) {
    visit(sparseEntryVisitor, new int[size.size()], 0);
    return sparseEntryVisitor.result();
  }

  private void visit(SparseEntryVisitor<?> sparseEntryVisitor, int[] array, int depth) {
    if (depth + 1 < array.length)
      for (Entry<Integer, Tensor> entry : navigableMap.entrySet()) {
        array[depth] = entry.getKey();
        Tensor tensor = entry.getValue();
        if (tensor instanceof SparseArray)
          ((SparseArray) tensor).visit(sparseEntryVisitor, array, depth + 1);
        else
          throw TensorRuntimeException.of(tensor);
      }
    else // terminal case
      for (Entry<Integer, Tensor> entry : navigableMap.entrySet()) {
        int index = entry.getKey();
        array[depth] = index;
        Scalar scalar = (Scalar) entry.getValue();
        if (!scalar.equals(fallback))
          sparseEntryVisitor.accept(Integers.asList(array), scalar);
      }
  }

  private SparseArray collapse() {
    navigableMap.values().removeIf(1 < size.size() //
        ? tensor -> (tensor instanceof SparseArray && ((SparseArray) tensor).navigableMap.isEmpty())
        : fallback::equals);
    return this;
  }

  private static <T> Collector<T, ?, NavigableMap<Integer, Tensor>> _map( //
      Function<? super T, Integer> keyMapper, //
      Function<? super T, Tensor> valueMapper) {
    return Collectors.toMap(keyMapper, valueMapper, MergeIllegal.operator(), TreeMap::new);
  }

  /** @param index
   * @return index
   * @throws Exception if given index is negative or greater equals to length() */
  private int requireInRange(int index) {
    if (0 <= index && index < length())
      return index;
    throw new IllegalArgumentException("index=" + index + " length=" + length());
  }

  private static Scalar checkFallback(Scalar fallback) {
    if (fallback.one().zero().equals(fallback))
      return fallback;
    throw TensorRuntimeException.of(fallback);
  }
}
