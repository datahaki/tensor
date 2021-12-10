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

/** Lenient Add implements the rules
 * <pre>
 * 3[m]+0[s] == 3[m]
 * 0[m]+0[s] == 0
 * </pre>
 * that are required for some algorithms.
 * 
 * Remark: The computations throw an exception by {@link Quantity#add(Tensor)}. */
public enum LenientAdd {
  ;
  private static final BinaryOperator<Tensor> INNER = Inner.with((p, q) -> of(p, (Scalar) q));

  /** @param p
   * @param q
   * @return */
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
}
