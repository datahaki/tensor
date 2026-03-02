// code by jph
package ch.alpine.tensor;

import java.util.List;
import java.util.ListIterator;
import java.util.function.Function;
import java.util.stream.Stream;

/** the content of the UnmodifiableTensor is read-only.
 * An attempt to modify the content results in an exception.
 * 
 * <p>UnmodifiableTensor does not duplicate memory.
 * UnmodifiableTensor wraps the original content list.
 * 
 * <p>All methods in the default implementation {@link TensorImpl} that
 * 1) give references of sub-tensors, or that
 * 2) may result in the modification of the content
 * are overloaded.
 * 
 * <p>The copy {@link UnmodifiableTensor#copy()} is modifiable. */
/* package */ class UnmodifiableTensor extends TensorImpl {
  /** @param list to be guaranteed non-null */
  public UnmodifiableTensor(List<Tensor> list) {
    super(list);
  }

  @Override // from TensorImpl
  protected Tensor byRef(int i) {
    return super.byRef(i).unmodifiable();
  }

  @Override // from TensorImpl
  public Tensor unmodifiable() {
    return this;
  }

  @Override // from TensorImpl
  public void set(Tensor tensor, List<Integer> index) {
    throw new UnsupportedOperationException("unmodifiable");
  }

  @Override // from TensorImpl
  public <T extends Tensor> void set(Function<T, ? extends Tensor> function, List<Integer> index) {
    throw new UnsupportedOperationException("unmodifiable");
  }

  @Override // from TensorImpl
  public Tensor append(Tensor tensor) {
    throw new UnsupportedOperationException("unmodifiable");
  }

  @Override // from TensorImpl
  public Tensor block(List<Integer> fromIndex, List<Integer> dimensions) {
    return super.block(fromIndex, dimensions).unmodifiable();
  }

  @Override // from TensorImpl
  public Stream<Tensor> stream() {
    return super.stream().map(Tensor::unmodifiable);
  }

  @Override // from TensorImpl
  public ListIterator<Tensor> iterator() {
    return StaticHelper.unmodifiable(super.iterator());
  }
}
