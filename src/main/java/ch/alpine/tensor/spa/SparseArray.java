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
import ch.alpine.tensor.Scalars;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.Throw;
import ch.alpine.tensor.alg.Dimensions;
import ch.alpine.tensor.alg.Dot;
import ch.alpine.tensor.ext.Integers;
import ch.alpine.tensor.ext.Lists;
import ch.alpine.tensor.ext.MergeIllegal;

/** The string expression is as in Mathematica except that the tensor library starts indexing at 0.
 * Example:
 * <pre>
 * SparseArray[{{0, 2}->3, {1, 0}->5, {1, 1}->6, {1, 2}->8, {2, 1}->2, {2, 4}->4}, {3, 5}, 0]
 * </pre>
 * 
 * <p>inspired by
 * <a href="https://reference.wolfram.com/language/ref/SparseArray.html">SparseArray</a> */
public class SparseArray extends AbstractTensor implements Serializable {
  /** @param fallback scalar, for instance {@link RealScalar#ZERO}
   * @param dimensions with non-negative values
   * @return empty sparse array with given dimensions, or fallback if no dimensions are specified
   * @throws Exception if fallback element is not zero */
  public static Tensor of(Scalar fallback, int... dimensions) {
    Scalars.requireZero(fallback);
    return dimensions.length == 0 //
        ? fallback
        : new SparseArray(fallback, //
            Integers.asList(IntStream.of(dimensions).map(Integers::requirePositiveOrZero).toArray()));
  }

  private final Scalar fallback;
  /** the content of size is not modified by sparse array */
  private final List<Integer> size;
  private final NavigableMap<Integer, Tensor> navigableMap;

  /* package */ SparseArray(Scalar fallback, List<Integer> size, NavigableMap<Integer, Tensor> navigableMap) {
    this.fallback = fallback;
    this.size = size;
    this.navigableMap = navigableMap;
  }

  private SparseArray(Scalar fallback, List<Integer> size) {
    this(fallback, size, new TreeMap<>());
  }

  /** @return fallback scalar */
  public Scalar fallback() {
    return fallback;
  }

  @Override // from Tensor
  public Tensor unmodifiable() {
    return new UnmodifiableSparseArray(fallback, size, navigableMap);
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
  public Tensor get(List<Integer> index) {
    if (index.isEmpty())
      return copy();
    int head = index.get(0);
    List<Integer> _index = Lists.rest(index);
    if (head == ALL) {
      List<Integer> list = IntStream.range(0, size.size()) //
          .filter(i -> index.size() <= i || index.get(i) == ALL) //
          .map(size::get) //
          .boxed().collect(Collectors.toList());
      return new SparseArray(fallback, list, navigableMap.entrySet().stream() //
          .collect(_map(Entry::getKey, entry -> entry.getValue().get(_index)))); //
    }
    return byRef(head).get(_index);
  }

  @Override // from Tensor
  public void set(Tensor tensor, List<Integer> index) {
    int head = index.get(0);
    List<Integer> _size = Lists.rest(size);
    if (index.size() == 1) // terminal case
      if (head == ALL) {
        Integers.requireEquals(size.get(0), tensor.length());
        AtomicInteger i = new AtomicInteger();
        tensor.stream().forEach(entry -> _set(i.getAndIncrement(), entry, _size));
      } else
        _set(requireInRange(head), tensor, _size);
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
        IntStream.range(0, length()).forEach(i -> _set(i, function.apply((T) byRef(i)), _size));
      else
        _set(head, function.apply((T) byRef(head)), _size);
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
      navigableMap.put(index, list.isEmpty() || tensor instanceof SparseArray //
          ? tensor.copy()
          : StaticHelper.of(fallback, list, tensor));
    else
      throw new Throw(tensor, list);
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
    requireInRangeClosed(fromIndex);
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
    if (tensor instanceof SparseArray sparseArray) {
      Integers.requireEquals(length(), tensor.length());
      return new SparseArray(fallback.add(sparseArray.fallback), size, //
          Stream.concat(navigableMap.keySet().stream(), sparseArray.navigableMap.keySet().stream()) //
              .distinct().collect(_map(i -> i, i -> byRef(i).add(sparseArray.byRef(i))))).trim();
    }
    return tensor.add(this);
  }

  @Override // from Tensor
  public Tensor subtract(Tensor tensor) {
    if (tensor instanceof SparseArray sparseArray) {
      Integers.requireEquals(length(), tensor.length());
      return new SparseArray(fallback.subtract(sparseArray.fallback), size, //
          Stream.concat(navigableMap.keySet().stream(), sparseArray.navigableMap.keySet().stream()) //
              .distinct().collect(_map(i -> i, i -> byRef(i).subtract(sparseArray.byRef(i))))).trim();
    }
    return tensor.negate().add(this);
  }

  @Override // from Tensor
  public Tensor multiply(Scalar scalar) {
    return new SparseArray(Scalars.requireZero(fallback.multiply(scalar)), size, //
        navigableMap.entrySet().stream().collect(_map(Entry::getKey, entry -> entry.getValue().multiply(scalar))));
  }

  @Override // from Tensor
  public Tensor divide(Scalar scalar) {
    return new SparseArray(Scalars.requireZero(fallback.divide(scalar)), size, //
        navigableMap.entrySet().stream().collect(_map(Entry::getKey, entry -> entry.getValue().divide(scalar))));
  }

  @Override // from Tensor
  public Tensor dot(Tensor tensor) {
    Scalar dot_fallback = tensor instanceof SparseArray sparseArray //
        ? fallback.multiply(sparseArray.fallback())
        : fallback;
    if (size.size() == 1) {
      List<Integer> rest = Lists.rest(Dimensions.of(tensor));
      return rest.isEmpty() //
          ? navigableMap.entrySet().stream() //
              .map(entry -> tensor.Get(entry.getKey()).multiply((Scalar) entry.getValue())) //
              .reduce(Scalar::add) //
              .orElse(dot_fallback)
          : navigableMap.entrySet().stream() //
              .map(entry -> StaticHelper.of(dot_fallback, rest, tensor.get(entry.getKey()).multiply((Scalar) entry.getValue()))) //
              .reduce(Tensor::add) //
              .orElseGet(() -> new SparseArray(dot_fallback, rest));
    }
    return new SparseArray(dot_fallback, Dot.combine(size, Dimensions.of(tensor)), //
        navigableMap.entrySet().stream().collect(_map(Entry::getKey, entry -> entry.getValue().dot(tensor))));
  }

  @Override // from Tensor
  public Tensor map(Function<Scalar, ? extends Tensor> function) {
    Tensor map_fallback = function.apply(fallback);
    Dimensions dimensions = new Dimensions(map_fallback);
    if (dimensions.isArray()) {
      List<Scalar> elements = map_fallback.flatten(-1) //
          .map(Scalar.class::cast) //
          .filter(Scalars::isZero) //
          .distinct() //
          .limit(2) //
          .collect(Collectors.toList());
      if (elements.size() == 1) {
        List<Integer> result = Stream.concat(size.stream(), dimensions.list().stream()).collect(Collectors.toList());
        SparseArray sparseArray = new SparseArray(elements.get(0), result);
        visit((list, scalar) -> sparseArray.set(function.apply(scalar), list));
        return sparseArray;
      }
    }
    return new Normal(function).apply(this);
  }

  @Override // from Tensor
  public Tensor block(List<Integer> ofs, List<Integer> len) {
    int depth = Integers.requireEquals(ofs.size(), len.size());
    if (depth == 0)
      return this;
    int head = requireInRangeClosed(ofs.get(0));
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

  /** @param ofs 0, or 1 for Mathematica index convention
   * @return
   * @throws Exception if ofs is outside valid range */
  public String toString(int ofs) {
    return "SparseArray" + '[' + visit(new SparseArrayToString(ofs)) + ", " + Tensors.vector(size) + ", " + fallback + ']';
  }

  @Override // from Object
  public String toString() {
    return toString(0);
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
        ((SparseArray) entry.getValue()).visit(sparseEntryVisitor, array, depth + 1);
      }
    else // terminal case
      for (Entry<Integer, Tensor> entry : navigableMap.entrySet()) {
        array[depth] = entry.getKey();
        Scalar scalar = (Scalar) entry.getValue();
        if (!scalar.equals(fallback))
          sparseEntryVisitor.accept(Integers.asList(array), scalar);
      }
  }

  private SparseArray trim() {
    navigableMap.values().removeIf(1 < size.size() //
        ? tensor -> tensor instanceof SparseArray sparseArray && sparseArray.navigableMap.isEmpty()
        : fallback::equals);
    return this;
  }

  /** @param index
   * @return index
   * @throws Exception if given index is negative or greater equals to length() */
  private int requireInRange(int index) {
    if (0 <= index && index < length())
      return index;
    throw new IllegalArgumentException("index=" + index + " length=" + length());
  }

  /** @param index
   * @return index
   * @throws Exception if given index is negative or greater than length() */
  private int requireInRangeClosed(int index) {
    if (0 <= index && index <= length())
      return index;
    throw new IllegalArgumentException("index=" + index + " length=" + length());
  }

  private static <T> Collector<T, ?, NavigableMap<Integer, Tensor>> _map( //
      Function<? super T, Integer> keyMapper, //
      Function<? super T, Tensor> valueMapper) {
    return Collectors.toMap(keyMapper, valueMapper, MergeIllegal.operator(), TreeMap::new);
  }
}
