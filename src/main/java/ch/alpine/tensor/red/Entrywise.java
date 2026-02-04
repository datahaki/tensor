// code by jph
package ch.alpine.tensor.red;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

import ch.alpine.tensor.ComplexScalar;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Unprotect;
import ch.alpine.tensor.api.ScalarBinaryOperator;
import ch.alpine.tensor.api.TensorBinaryOperator;
import ch.alpine.tensor.ext.Integers;

/** Entrywise applies a BinaryOperator<Scalar> across multiple tensors.
 * The tensors are required to have the same dimensions/structure.
 * 
 * <p>Example:
 * <pre>
 * Tensor box = {{0, 7}, {0, 8}, {1, 5}, {2, 7}};
 * Entrywise.max().of(box) == {2, 8}
 * Entrywise.min().of(box) == {0, 5}
 * </pre>
 * 
 * <p>Example:
 * Let real and imag be tensors of same dimensions with entries of type
 * {@link RealScalar}. Then the following code gives a tensor with
 * {@link ComplexScalar} consisting of corresponding real and imaginary
 * values drawn from given tensors.
 * <pre>
 * Tensor tensor = Entrywise.with(ComplexScalar::of).apply(real, imag);
 * </pre>
 * 
 * <p>Entrywise reproduces existing functionality:
 * <pre>
 * Entrywise.with(Scalar::add).of(Tensors.of(a, b, c)) == a.add(b).add(c)
 * Entrywise.with(Scalar::multiply).of(Tensors.of(a, b, c)) == Times.of(a, b, c)
 * </pre>
 * 
 * @see Times#of(Tensor...) */
public class Entrywise implements TensorBinaryOperator {
  /** @param scalarBinaryOperator non-null
   * @return
   * @throws Exception if given binaryOperator is null */
  public static Entrywise with(ScalarBinaryOperator scalarBinaryOperator) {
    return new Entrywise(Objects.requireNonNull(scalarBinaryOperator));
  }

  // shorthand Min::of does not result in serializable operator, due to template arguments
  private static final Entrywise MIN = with(Min::of);
  private static final Entrywise MAX = with(Max::of);
  private static final Entrywise MUL = with(Scalar::multiply);

  /** @return entrywise minimum operator */
  public static Entrywise min() {
    return MIN;
  }

  /** @return entrywise maximum operator */
  public static Entrywise max() {
    return MAX;
  }

  /** @return pointwise multiplication
   * @see Times */
  public static Entrywise mul() {
    return MUL;
  }

  // ---
  private final ScalarBinaryOperator scalarBinaryOperator;

  private Entrywise(ScalarBinaryOperator scalarBinaryOperator) {
    this.scalarBinaryOperator = scalarBinaryOperator;
  }

  @Override // from BinaryOperator
  public Tensor apply(Tensor a, Tensor b) {
    if (a instanceof Scalar)
      return scalarBinaryOperator.apply((Scalar) a, (Scalar) b);
    Iterator<Tensor> ia = a.iterator();
    Iterator<Tensor> ib = b.iterator();
    List<Tensor> list = new ArrayList<>(Integers.requireEquals(a.length(), b.length()));
    while (ia.hasNext())
      list.add(apply(ia.next(), ib.next()));
    return Unprotect.using(list);
  }

  /** Example:
   * <pre>
   * Entrywise.with(Min::of).of({{1, 2, 3}, {5, 0, 4}}) == {1, 0, 3}
   * Entrywise.with(Max::of).of({{1, 2, 3}, {5, 0, 4}}) == {5, 2, 4}
   * </pre>
   * 
   * @param tensor
   * @return
   * @throws Exception */
  public Tensor of(Tensor tensor) {
    return tensor.stream().reduce(this).orElseThrow();
  }
}
