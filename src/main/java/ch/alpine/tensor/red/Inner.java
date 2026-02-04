// code by jph
package ch.alpine.tensor.red;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.function.BiFunction;

import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Unprotect;
import ch.alpine.tensor.api.TensorBinaryOperator;
import ch.alpine.tensor.ext.Integers;

/** The implementation is only consistent with Mathematica::Inner[f, l1, l2, g]
 * in the special case where g == Identity.
 * 
 * Inner is more general than {@link Entrywise} since the operator accepts a
 * {@link Tensor} as second argument.
 * 
 * <p>inspired by
 * <a href="https://reference.wolfram.com/language/ref/Inner.html">Inner</a>
 * 
 * @see Entrywise */
public class Inner implements TensorBinaryOperator {
  /** @param biFunction non-null
   * @return
   * @throws Exception if given biFunction is null */
  public static TensorBinaryOperator with(BiFunction<Scalar, ? super Tensor, ? extends Tensor> biFunction) {
    return new Inner(Objects.requireNonNull(biFunction));
  }

  // ---
  private final BiFunction<Scalar, ? super Tensor, ? extends Tensor> biFunction;

  private Inner(BiFunction<Scalar, ? super Tensor, ? extends Tensor> biFunction) {
    this.biFunction = biFunction;
  }

  @Override // from BinaryOperator
  public Tensor apply(Tensor a, Tensor b) {
    if (a instanceof Scalar)
      return biFunction.apply((Scalar) a, b);
    Iterator<Tensor> ia = a.iterator();
    Iterator<Tensor> ib = b.iterator();
    List<Tensor> list = new ArrayList<>(Integers.requireEquals(a.length(), b.length()));
    while (ia.hasNext())
      list.add(apply(ia.next(), ib.next()));
    return Unprotect.using(list);
  }
}
