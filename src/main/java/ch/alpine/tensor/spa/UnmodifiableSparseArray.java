// code by jph
package ch.alpine.tensor.spa;

import java.util.Iterator;
import java.util.List;
import java.util.NavigableMap;
import java.util.function.Function;
import java.util.stream.Stream;

import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;

/* very similar to UnmodifiableTensor */
class UnmodifiableSparseArray extends SparseArray {
  public UnmodifiableSparseArray(Scalar fallback, List<Integer> size, NavigableMap<Integer, Tensor> navigableMap) {
    super(fallback, size, navigableMap);
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
  public Tensor block(List<Integer> fromIndex, List<Integer> dimensions) {
    return super.block(fromIndex, dimensions).unmodifiable();
  }

  @Override // from TensorImpl
  public Stream<Tensor> stream() {
    return super.stream().map(Tensor::unmodifiable);
  }

  @Override // from TensorImpl
  public Iterator<Tensor> iterator() {
    Iterator<Tensor> iterator = super.iterator();
    return new Iterator<>() {
      @Override
      public boolean hasNext() {
        return iterator.hasNext();
      }

      @Override
      public Tensor next() {
        return iterator.next().unmodifiable();
      }
      // default implementation of Iterator#remove() throws an UnsupportedOperationException
    };
  }
}
