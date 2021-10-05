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

/** reference implementation of the interface Tensor
 * 
 * TODO "dense", or "list" - tensor !? */
/* package */ class TensorImpl extends AbstractTensor implements Serializable {
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

  @Override
  protected Tensor byRef(int i) {
    return list.get(i);
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
        list.stream().forEach(entry -> entry.set(function, sublist));
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
  public Tensor extract(int fromIndex, int toIndex) {
    return Tensor.of(list.subList(fromIndex, toIndex).stream().map(Tensor::copy));
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
  public Tensor map(Function<Scalar, ? extends Tensor> function) {
    return Tensor.of(list.stream().map(tensor -> tensor.map(function)));
  }

  // ---
  @Override // from Tensor
  public Tensor block(List<Integer> fromIndex, List<Integer> dimensions) {
    int size = Integers.requireEquals(fromIndex.size(), dimensions.size());
    if (size == 0)
      return this;
    int head = fromIndex.get(0);
    List<Tensor> subList = list.subList(head, head + dimensions.get(0));
    if (size == 1)
      return new TensorImpl(subList);
    List<Integer> subHead = fromIndex.subList(1, size);
    List<Integer> subDims = dimensions.subList(1, size);
    return Tensor.of(subList.stream().map(entry -> entry.block(subHead, subDims)));
  }

  @Override // from Tensor
  public Stream<Tensor> stream() {
    return list.stream();
  }

  @Override // from Iterable
  public Iterator<Tensor> iterator() {
    return list.iterator();
  }

  @Override // from Object
  public String toString() {
    return list.stream().map(Tensor::toString).collect(StaticHelper.EMBRACE);
  }
}
