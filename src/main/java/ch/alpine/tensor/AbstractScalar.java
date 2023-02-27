// code by jph
package ch.alpine.tensor;

import java.util.Iterator;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Stream;

import ch.alpine.tensor.ext.Integers;

/** suggested base class for implementations of {@link Scalar} */
public abstract class AbstractScalar implements Scalar {
  /** the return type of Scalar#copy is deliberately not Scalar
   * to remind the application layer that invoking copy() on a
   * Scalar is never necessary, because Scalars are immutable. */
  @Override // from Tensor
  public final Tensor copy() {
    return this; // instance of Scalar is immutable
  }

  @Override // from Tensor
  public final Tensor unmodifiable() {
    return this; // instance of Scalar is immutable
  }

  @Override // from Tensor
  public final Tensor get(int i) {
    throw new Throw(this, i);
  }

  /** when using get() on {@code AbstractScalar} the list of arguments has to be empty */
  @Override // from Tensor
  public final Tensor get(int... index) {
    if (index.length == 0)
      return this;
    throw new Throw(this, Integers.asList(index));
  }

  /** when using get() on {@code AbstractScalar} the list of arguments has to be empty */
  @Override // from Tensor
  public final Tensor get(List<Integer> index) {
    if (index.size() == 0)
      return this;
    throw new Throw(this, index);
  }

  @Override // from Tensor
  public final Scalar Get(int i) {
    throw new Throw(this, i);
  }

  @Override // from Tensor
  public final Scalar Get(int i, int j) {
    throw new Throw(this, i, j);
  }

  @Override // from Tensor
  public final int length() {
    return LENGTH;
  }

  @Override // from Tensor
  public final Stream<Tensor> stream() {
    throw new Throw(this);
  }

  @Override // from Tensor
  public final Stream<Tensor> flatten(int level) {
    return Stream.of(this);
  }

  @Override // from Tensor
  public final void set(Tensor tensor, int... index) {
    throw new Throw(this, tensor, Integers.asList(index));
  }

  @Override // from Tensor
  public void set(Tensor tensor, List<Integer> index) {
    throw new Throw(this, tensor, index);
  }

  @Override // from Tensor
  public final <T extends Tensor> void set(Function<T, ? extends Tensor> function, int... index) {
    throw new Throw(this, Integers.asList(index));
  }

  @Override // from Tensor
  public <T extends Tensor> void set(Function<T, ? extends Tensor> function, List<Integer> index) {
    throw new Throw(this, index);
  }

  @Override // from Tensor
  public final Tensor append(Tensor tensor) {
    throw new Throw(this, tensor);
  }

  @Override // from Tensor
  public final Iterator<Tensor> iterator() {
    throw new Throw(this);
  }

  @Override // from Tensor
  public final Tensor extract(int fromIndex, int toIndex) {
    throw new Throw(this, fromIndex, toIndex);
  }

  @Override // from Tensor
  public final Tensor block(List<Integer> fromIndex, List<Integer> dimensions) {
    if (Integers.requireEquals(fromIndex.size(), dimensions.size()) == 0)
      return this;
    throw new Throw(this, fromIndex, dimensions);
  }

  @Override // from Tensor
  public final Tensor dot(Tensor tensor) {
    throw new Throw(this, tensor);
  }

  // ---
  // final default implementations
  @Override // from Scalar
  public final Scalar add(Tensor tensor) {
    return plus((Scalar) tensor);
  }

  @Override // from Scalar
  public final Tensor map(Function<Scalar, ? extends Tensor> function) {
    return function.apply(this).copy();
  }

  // ---
  // non-final default implementations; override for precision or speed
  @Override // from Scalar
  public Scalar subtract(Tensor tensor) {
    return add(tensor.negate());
  }

  @Override // from Scalar
  public Scalar divide(Scalar scalar) {
    return multiply(scalar.reciprocal());
  }

  @Override // from Scalar
  public Scalar under(Scalar scalar) {
    System.out.println(scalar + " / " + this);
    return reciprocal().multiply(scalar);
  }

  // ---
  /** @param scalar
   * @return this plus given scalar */
  protected abstract Scalar plus(Scalar scalar);

  // ---
  @Override // from Object
  public abstract int hashCode();

  @Override // from Object
  public abstract boolean equals(Object object);

  @Override // from Object
  public abstract String toString();
}
