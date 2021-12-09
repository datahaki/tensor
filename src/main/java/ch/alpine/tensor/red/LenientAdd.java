// code by jph
package ch.alpine.tensor.red;

import java.util.function.BinaryOperator;

import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Scalars;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.TensorRuntimeException;
import ch.alpine.tensor.Unprotect;
import ch.alpine.tensor.qty.Quantity;
import ch.alpine.tensor.qty.QuantityUnit;
import ch.alpine.tensor.qty.Unit;

/** implements the rule 3[m]+0[s] == 3[m]
 * which throws an exception by {@link Quantity#add(Tensor)}
 * but is require for some algorithms. */
public enum LenientAdd {
  ;
  private static final BinaryOperator<Tensor> INNER = Inner.with((p, q) -> of(p, (Scalar) q));

  public static Scalar of(Scalar p, Scalar q) {
    Unit p_unit = QuantityUnit.of(p);
    Unit q_unit = QuantityUnit.of(q);
    if (!p_unit.equals(q_unit)) {
      boolean p_zero = Scalars.isZero(p);
      boolean q_zero = Scalars.isZero(q);
      Scalar sum = Unprotect.withoutUnit(p).add(Unprotect.withoutUnit(q));
      if (p_zero)
        return q_zero //
            ? sum // drop both units
            : Quantity.of(sum, q_unit);
      if (q_zero)
        return Quantity.of(sum, p_unit);
      throw TensorRuntimeException.of(p, q);
    }
    return p.add(q);
  }

  public static Tensor of(Tensor p, Tensor q) {
    return INNER.apply(p, q);
  }

  public static Tensor dot(Tensor p, Tensor q) {
    return Times.of(p, q).stream().reduce(LenientAdd::of).orElseThrow();
  }
}
