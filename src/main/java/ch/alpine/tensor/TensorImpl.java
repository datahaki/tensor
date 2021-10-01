// code by jph
package ch.alpine.tensor;

import java.io.Serializable;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import ch.alpine.tensor.ext.Integers;

/** reference implementation of the interface Tensor */
/* package */ class TensorImpl implements Tensor, Serializable {
  private final List<Tensor> list;

  /** @param list to be guaranteed non-null */
  public TensorImpl(List<Tensor> list) {
    this.list = list;
  }

  @Override // from Tensor
  public Tensor unmodifiable() {
    return new UnmodifiableTensor(list);
  }

  @Override // from Tensor
  public Tensor copy() {
    return Tensor.of(list.stream().map(Tensor::copy));
  }

  @Override // from Tensor
  public Tensor get(int i) {
    return list.get(i).copy();
  }

  @Override // from Tensor
  public Tensor get(int... index) {
    return get(Integers.asList(index));
  }

  @Override // from Tensor
  public Tensor get(List<Integer> index) {
    if (index.isEmpty())
      return copy();
    int head = index.get(0);
    List<Integer> sublist = index.subList(1, index.size());
    return head == ALL //
        ? Tensor.of(list.stream().map(tensor -> tensor.get(sublist)))
        : list.get(head).get(sublist);
  }

  @Override // from Tensor
  public Scalar Get(int i) {
    return (Scalar) list.get(i);
  }

  @Override // from Tensor
  public Scalar Get(int i, int j) {
    return list.get(i).Get(j);
  }

  @Override // from Tensor
  public void set(Tensor tensor, int... index) {
    _set(tensor, Integers.asList(index));
  }

  /** @param tensor
   * @param index
   * @see UnmodifiableTensor */
  protected void _set(Tensor tensor, List<Integer> index) {
    int head = index.get(0);
    if (index.size() == 1)
      if (head == ALL) {
        TensorImpl impl = (TensorImpl) tensor;
        _range(impl).forEach(pos -> list.set(pos, impl.list.get(pos).copy()));
      } else
        list.set(head, tensor.copy());
    else {
      List<Integer> sublist = index.subList(1, index.size());
      if (head == ALL) {
        TensorImpl impl = (TensorImpl) tensor;
        _range(impl).forEach(pos -> ((TensorImpl) list.get(pos))._set(impl.list.get(pos), sublist));
      } else
        ((TensorImpl) list.get(head))._set(tensor, sublist);
    }
  }

  @Override // from Tensor
  public <T extends Tensor> void set(Function<T, ? extends Tensor> function, int... index) {
    _set(function, Integers.asList(index));
  }

  /** @param function
   * @param index
   * @see UnmodifiableTensor */
  @SuppressWarnings("unchecked")
  protected <T extends Tensor> void _set(Function<T, ? extends Tensor> function, List<Integer> index) {
    int head = index.get(0);
    if (index.size() == 1)
      if (head == ALL)
        IntStream.range(0, list.size()).forEach(pos -> list.set(pos, function.apply((T) list.get(pos)).copy()));
      else
        list.set(head, function.apply((T) list.get(head)).copy());
    else {
      List<Integer> sublist = index.subList(1, index.size());
      if (head == ALL)
        list.stream().map(TensorImpl.class::cast).forEach(impl -> impl._set(function, sublist));
      else
        ((TensorImpl) list.get(head))._set(function, sublist);
    }
  }

  @Override // from Tensor
  public Tensor append(Tensor tensor) {
    list.add(tensor.copy());
    return this;
  }

  @Override // from Tensor
  public int length() {
    return list.size();
  }

  @Override // from Tensor
  public Stream<Tensor> stream() {
    return list.stream();
  }

  @Override // from Tensor
  public Stream<Tensor> flatten(int level) {
    if (level == 0)
      return stream(); // UnmodifiableTensor overrides stream()
    int ldecr = level - 1;
    return list.stream().flatMap(tensor -> tensor.flatten(ldecr));
  }

  @Override // from Tensor
  public Tensor extract(int fromIndex, int toIndex) {
    return Tensor.of(list.subList(fromIndex, toIndex).stream().map(Tensor::copy));
  }

  @Override // from Tensor
  public Tensor block(List<Integer> fromIndex, List<Integer> dimensions) {
    Integers.requireEquals(fromIndex.size(), dimensions.size());
    return fromIndex.isEmpty() //
        ? copy()
        : _block(fromIndex, dimensions);
  }

  /** @param fromIndex non-empty
   * @param dimensions of same size as fromIndex
   * @return
   * @see ViewTensor */
  protected Tensor _block(List<Integer> fromIndex, List<Integer> dimensions) {
    int beg = fromIndex.get(0);
    int end = beg + dimensions.get(0);
    if (fromIndex.size() == 1)
      return extract(beg, end);
    int size = fromIndex.size();
    return Tensor.of(list.subList(beg, end).stream() //
        .map(TensorImpl.class::cast) //
        .map(impl -> impl._block(fromIndex.subList(1, size), dimensions.subList(1, size))));
  }

  @Override // from Tensor
  public Tensor negate() {
    return Tensor.of(list.stream().map(Tensor::negate));
  }

  @Override // from Tensor
  public Tensor add(Tensor tensor) {
    TensorImpl impl = (TensorImpl) tensor;
    return Tensor.of(_range(impl).mapToObj(index -> list.get(index).add(impl.list.get(index))));
  }

  @Override // from Tensor
  public Tensor subtract(Tensor tensor) {
    TensorImpl impl = (TensorImpl) tensor;
    return Tensor.of(_range(impl).mapToObj(index -> list.get(index).subtract(impl.list.get(index))));
  }

  @Override // from Tensor
  public Tensor pmul(Tensor tensor) {
    TensorImpl impl = (TensorImpl) tensor;
    return Tensor.of(_range(impl).mapToObj(index -> list.get(index).pmul(impl.list.get(index))));
  }

  @Override // from Tensor
  public Tensor multiply(Scalar scalar) {
    return Tensor.of(list.stream().map(tensor -> tensor.multiply(scalar)));
  }

  @Override // from Tensor
  public Tensor divide(Scalar scalar) {
    return Tensor.of(list.stream().map(tensor -> tensor.divide(scalar)));
  }

  @Override // from Tensor
  public Tensor dot(Tensor tensor) {
    if (list.isEmpty() || list.get(0) instanceof Scalar) { // quick hint whether this is a vector
      Integers.requireEquals(length(), tensor.length()); // <- check is necessary otherwise error might be undetected
      AtomicInteger atomicInteger = new AtomicInteger();
      return tensor.stream().map(rhs -> rhs.multiply((Scalar) list.get(atomicInteger.getAndIncrement()))) //
          .reduce(Tensor::add).orElse(RealScalar.ZERO);
    }
    return Tensor.of(list.stream().map(entry -> entry.dot(tensor)));
  }

  // helper function
  private IntStream _range(TensorImpl impl) {
    // check is necessary otherwise error might be undetected
    return IntStream.range(0, Integers.requireEquals(list.size(), impl.list.size()));
  }

  @Override // from Tensor
  public Tensor map(Function<Scalar, ? extends Tensor> function) {
    return Tensor.of(list.stream().map(tensor -> tensor.map(function)));
  }

  /** @return list
   * @see ViewTensor */
  protected List<Tensor> list() {
    return list;
  }

  // ---
  @Override // from Iterable
  public Iterator<Tensor> iterator() {
    return list.iterator();
  }

  @Override // from Object
  public int hashCode() {
    return list.hashCode();
  }

  @Override // from Object
  public boolean equals(Object object) {
    return object instanceof TensorImpl //
        && list.equals(((TensorImpl) object).list);
  }

  @Override // from Object
  public String toString() {
    return list.stream().map(Tensor::toString).collect(StaticHelper.EMBRACE);
  }
}
