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
import ch.alpine.tensor.ext.Lists;
import ch.alpine.tensor.io.MathematicaFormat;

/** reference implementation of the interface Tensor */
/* package */ class TensorImpl extends AbstractTensor implements Serializable {
  private final List<Tensor> list;

  /** @param list to be guaranteed non-null */
  public TensorImpl(List<Tensor> list) {
    this.list = list;
  }

  @Override // from Tensor
  public Tensor copy() {
    return Tensor.of(stream().map(Tensor::copy));
  }

  @Override // from Tensor
  public Tensor unmodifiable() {
    return new UnmodifiableTensor(list);
  }

  @Override // from AbstractTensor
  protected Tensor byRef(int i) {
    return list.get(i);
  }

  @Override // from Tensor
  public Tensor get(List<Integer> index) {
    if (index.isEmpty())
      return copy();
    int head = index.get(0);
    List<Integer> _index = Lists.rest(index);
    return head == ALL //
        ? Tensor.of(stream().map(tensor -> tensor.get(_index)))
        : byRef(head).get(_index);
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
      List<Integer> _index = Lists.rest(index);
      if (head == ALL) {
        Integers.requireEquals(length(), tensor.length());
        AtomicInteger i = new AtomicInteger();
        tensor.forEach(entry -> list.get(i.getAndIncrement()).set(entry, _index));
      } else
        list.get(head).set(tensor, _index);
    }
  }

  @SuppressWarnings("unchecked")
  @Override // from Tensor
  public <T extends Tensor> void set(Function<T, ? extends Tensor> function, List<Integer> index) {
    int head = index.get(0);
    if (index.size() == 1) // terminal case
      if (head == ALL)
        IntStream.range(0, length()).forEach(i -> list.set(i, function.apply((T) list.get(i)).copy()));
      else
        list.set(head, function.apply((T) list.get(head)).copy());
    else {
      List<Integer> _index = Lists.rest(index);
      if (head == ALL)
        stream().forEach(entry -> entry.set(function, _index));
      else
        list.get(head).set(function, _index);
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
    return Tensor.of(stream().map(Tensor::negate));
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
  public Tensor multiply(Scalar scalar) {
    return Tensor.of(stream().map(tensor -> tensor.multiply(scalar)));
  }

  @Override // from Tensor
  public Tensor divide(Scalar scalar) {
    return Tensor.of(stream().map(tensor -> tensor.divide(scalar)));
  }

  @Override // from Tensor
  public Tensor map(Function<Scalar, ? extends Tensor> function) {
    return Tensor.of(stream().map(tensor -> tensor.map(function)));
  }

  @Override // from Tensor
  public Tensor dot(Tensor tensor) {
    if (length() == 0 || byRef(0) instanceof Scalar) { // quick hint whether this is a vector
      Integers.requireEquals(length(), tensor.length());
      AtomicInteger i = new AtomicInteger();
      return tensor.stream().map(entry -> entry.multiply(Get(i.getAndIncrement()))) //
          .reduce(Tensor::add).orElse(RealScalar.ZERO);
    }
    return Tensor.of(stream().map(entry -> entry.dot(tensor)));
  }

  @Override // from Tensor
  public Tensor block(List<Integer> ofs, List<Integer> len) {
    int size = Integers.requireEquals(ofs.size(), len.size());
    if (size == 0)
      return this;
    int head = ofs.get(0);
    int len0 = len.get(0);
    List<Tensor> subList = list.subList(head, head + len0);
    if (size == 1)
      return new TensorImpl(subList);
    List<Integer> _ofs = Lists.rest(ofs);
    List<Integer> _len = Lists.rest(len);
    return Tensor.of(subList.stream().map(entry -> entry.block(_ofs, _len)));
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
    return list.stream().map(Tensor::toString).collect(MathematicaFormat.EMBRACE);
  }
}
