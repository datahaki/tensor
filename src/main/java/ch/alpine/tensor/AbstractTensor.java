// code by jph
package ch.alpine.tensor;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

import ch.alpine.tensor.ext.Integers;

public abstract class AbstractTensor implements Tensor {
  /** @param i
   * @return reference to i-th element */
  protected abstract Tensor byRef(int i);

  @Override // from Tensor
  public final Scalar Get(int i) {
    return (Scalar) byRef(i);
  }

  @Override // from Tensor
  public final Scalar Get(int i, int j) {
    return byRef(i).Get(j);
  }

  @Override // from Tensor
  public final Tensor get(int i) {
    return byRef(i).copy();
  }

  @Override // from Tensor
  public final Tensor get(int... index) {
    return get(Integers.asList(index));
  }

  @Override // from Tensor
  public final Tensor get(List<Integer> index) {
    if (index.isEmpty())
      return copy();
    int head = index.get(0);
    List<Integer> sublist = index.subList(1, index.size());
    return head == ALL //
        ? Tensor.of(stream().map(tensor -> tensor.get(sublist)))
        : byRef(head).get(sublist);
  }

  @Override // from Tensor
  public final void set(Tensor tensor, int... index) {
    set(tensor, Integers.asList(index));
  }

  @Override // from Tensor
  public final Tensor dot(Tensor tensor) {
    if (length() == 0 || byRef(0) instanceof Scalar) { // quick hint whether this is a vector
      Integers.requireEquals(length(), tensor.length());
      AtomicInteger i = new AtomicInteger();
      return tensor.stream().map(entry -> entry.multiply(Get(i.getAndIncrement()))) //
          .reduce(Tensor::add).orElse(RealScalar.ZERO);
    }
    return Tensor.of(stream().map(entry -> entry.dot(tensor)));
  }

  @Override // from Tensor
  public final Stream<Tensor> flatten(int level) {
    if (level == 0)
      return stream(); // UnmodifiableTensor overrides stream()
    int ldecr = level - 1;
    return stream().flatMap(tensor -> tensor.flatten(ldecr));
  }

  @Override // from Object
  public final int hashCode() {
    int hashCode = 1;
    for (Tensor tensor : this)
      hashCode = 31 * hashCode + tensor.hashCode();
    return hashCode;
  }

  @Override // from Object
  public final boolean equals(Object object) {
    if (object instanceof Tensor) {
      Tensor tensor = (Tensor) object;
      if (length() == tensor.length()) {
        AtomicInteger i = new AtomicInteger();
        return tensor.stream().allMatch(entry -> entry.equals(byRef(i.getAndIncrement())));
      }
    }
    return false;
  }
}
