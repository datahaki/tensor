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
    set(tensor, Integers.asList(index));
  }

  @Override // from Tensor
  public void set(Tensor tensor, List<Integer> index) {
    int head = index.get(0);
    if (index.size() == 1) // terminal case
      if (head == ALL) {
        Integers.requireEquals(length(), tensor.length());
        AtomicInteger i = new AtomicInteger();
        tensor.stream().map(Tensor::copy).forEach(entry -> list.set(i.getAndIncrement(), entry)); // insert copies
      } else
        list.set(head, tensor.copy()); // insert copy
    else { // function set is called with tensor provided by reference
      List<Integer> sublist = index.subList(1, index.size());
      if (head == ALL) {
        Integers.requireEquals(length(), tensor.length());
        AtomicInteger i = new AtomicInteger();
        tensor.stream().forEach(entry -> list.get(i.getAndIncrement()).set(entry, sublist));
      } else
        list.get(head).set(tensor, sublist);
    }
  }

  @Override // from Tensor
  public <T extends Tensor> void set(Function<T, ? extends Tensor> function, int... index) {
    set(function, Integers.asList(index));
  }

  @SuppressWarnings("unchecked")
  @Override // from Tensor
  public <T extends Tensor> void set(Function<T, ? extends Tensor> function, List<Integer> index) {
    int head = index.get(0);
    if (index.size() == 1) // terminal case
      if (head == ALL)
        IntStream.range(0, list.size()).forEach(i -> list.set(i, function.apply((T) list.get(i)).copy()));
      else
        list.set(head, function.apply((T) list.get(head)).copy());
    else {
      List<Integer> sublist = index.subList(1, index.size());
      if (head == ALL)
        stream().forEach(entry -> entry.set(function, sublist));
      else
        list.get(head).set(function, sublist);
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
    int size = Integers.requireEquals(fromIndex.size(), dimensions.size());
    if (size == 0)
      return this;
    int head = fromIndex.get(0);
    List<Tensor> subList = list().subList(head, head + dimensions.get(0));
    if (size == 1)
      return new TensorImpl(subList);
    List<Integer> subHead = fromIndex.subList(1, size);
    List<Integer> subDims = dimensions.subList(1, size);
    return Tensor.of(subList.stream().map(entry -> entry.block(subHead, subDims)));
  }

  @Override // from Tensor
  public Tensor negate() {
    return Tensor.of(list.stream().map(Tensor::negate));
  }

  @Override // from Tensor
  public Tensor add(Tensor tensor) {
    Integers.requireEquals(length(), tensor.length());
    AtomicInteger i = new AtomicInteger();
    return Tensor.of(tensor.stream().map(entry -> list.get(i.getAndIncrement()).add(entry)));
  }

  @Override // from Tensor
  public Tensor subtract(Tensor tensor) {
    Integers.requireEquals(length(), tensor.length());
    AtomicInteger i = new AtomicInteger();
    return Tensor.of(tensor.stream().map(entry -> list.get(i.getAndIncrement()).subtract(entry)));
  }

  @Override // from Tensor
  public Tensor pmul(Tensor tensor) {
    Integers.requireEquals(length(), tensor.length());
    AtomicInteger i = new AtomicInteger();
    return Tensor.of(tensor.stream().map(entry -> list.get(i.getAndIncrement()).pmul(entry)));
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
      Integers.requireEquals(length(), tensor.length());
      AtomicInteger i = new AtomicInteger();
      return tensor.stream().map(entry -> entry.multiply(Get(i.getAndIncrement()))) //
          .reduce(Tensor::add).orElse(RealScalar.ZERO);
    }
    return Tensor.of(list.stream().map(entry -> entry.dot(tensor)));
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
    if (object instanceof Tensor) {
      Tensor tensor = (Tensor) object;
      if (length() == tensor.length()) {
        AtomicInteger i = new AtomicInteger();
        return tensor.stream().allMatch(entry -> entry.equals(list.get(i.getAndIncrement())));
      }
    }
    return false;
  }

  @Override // from Object
  public String toString() {
    return list.stream().map(Tensor::toString).collect(StaticHelper.EMBRACE);
  }
}
