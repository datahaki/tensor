// code by jph
package ch.ethz.idsc.tensor;

import java.util.Iterator;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Stream;

/** suggested base class for implementations of {@link Scalar} */
public abstract class AbstractScalar implements Scalar {
  @Override // from Tensor
  public final Tensor copy() {
    return this; // instance of Scalar is immutable
  }

  @Override // from Tensor
  public final Tensor unmodifiable() {
    return this; // instance of Scalar is immutable
  }

  /** when using get() on {@link AbstractScalar} the list of arguments has to be empty */
  @Override // from Tensor
  public final Tensor get(Integer... index) {
    if (0 < index.length)
      throw new IllegalArgumentException();
    return this;
  }

  /** when using Get() on {@link AbstractScalar} the list of arguments has to be empty */
  @Override // from Tensor
  public final Scalar Get(Integer... index) {
    if (0 < index.length)
      throw new IllegalArgumentException();
    return this;
  }

  /** when using get() on {@link AbstractScalar} the list of arguments has to be empty */
  @Override // from Tensor
  public final Tensor get(List<Integer> index) {
    if (0 < index.size())
      throw new IllegalArgumentException();
    return this;
  }

  @Override // from Tensor
  public final int length() {
    return LENGTH;
  }

  @Override // from Tensor
  public final boolean isScalar() {
    return true;
  }

  @Override // from Tensor
  public final Stream<Tensor> flatten(int level) {
    return Stream.of(this);
  }

  @Override // from Tensor
  public final Tensor pmul(Tensor tensor) {
    // Tensor::pmul delegates pointwise multiplication to Scalar::pmul
    return tensor.multiply(this);
  }

  @Override // from Tensor
  public final void set(Tensor tensor, Integer... index) {
    throw TensorRuntimeException.of(this, tensor);
  }

  @Override // from Tensor
  public final <T extends Tensor> void set(Function<T, ? extends Tensor> function, Integer... index) {
    throw TensorRuntimeException.of(this);
  }

  @Override // from Tensor
  public final Tensor append(Tensor tensor) {
    throw TensorRuntimeException.of(this, tensor);
  }

  @Override // from Tensor
  public final Iterator<Tensor> iterator() {
    throw TensorRuntimeException.of(this);
  }

  @Override // from Tensor
  public final Tensor extract(int fromIndex, int toIndex) {
    throw TensorRuntimeException.of(this);
  }

  @Override // from Tensor
  public final Tensor block(List<Integer> fromIndex, List<Integer> dimensions) {
    throw TensorRuntimeException.of(this);
  }

  @Override // from Tensor
  public final Tensor dot(Tensor tensor) {
    throw TensorRuntimeException.of(this, tensor);
  }

  /***************************************************/
  @Override // from Scalar
  public final Scalar add(Tensor tensor) {
    return plus((Scalar) tensor);
  }

  @Override // from Scalar
  public final Scalar subtract(Tensor tensor) {
    return add(tensor.negate());
  }

  @Override // from Scalar
  public final Tensor map(Function<Scalar, ? extends Tensor> function) {
    return function.apply(this);
  }

  /***************************************************/
  /** not final because for {@link DoubleScalar} division should be carried out directly */
  @Override // from Scalar
  public Scalar divide(Scalar scalar) {
    return multiply(scalar.invert());
  }

  /** @param scalar
   * @return */
  // EXPERIMENTAL
  public Scalar under(Scalar scalar) {
    return invert().multiply(scalar);
  }

  /***************************************************/
  /** @param scalar
   * @return this plus input scalar */
  protected abstract Scalar plus(Scalar scalar);

  /***************************************************/
  @Override // from Object
  public abstract int hashCode();

  @Override // from Object
  public abstract boolean equals(Object object);

  @Override // from Object
  public abstract String toString();
}