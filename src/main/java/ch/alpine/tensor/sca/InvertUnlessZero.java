// code by jph
package ch.alpine.tensor.sca;

import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Scalars;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.api.ScalarUnaryOperator;
import ch.alpine.tensor.nrm.NormalizeUnlessZero;
import ch.alpine.tensor.qty.Quantity;

/** Hints:
 * 
 * 1)
 * Scalars.isZero(scalar) == false
 * does not imply that scalar.reciprocal() results in a meaningful number
 * 
 * For instance,
 * Scalars.isZero(4.9E-324) == false
 * whereas 1.0 / 4.9E-324 == Infinity
 * 
 * 2)
 * even if scalar.reciprocal() is a number, the product
 * factor.multiply(scalar.reciprocal())
 * may still result in Infinity.
 * 
 * 3)
 * better numerical accuracy is achieved by using direct division
 * a / b instead of a * (1 / b)
 * 
 * 4)
 * for input of type {@link Quantity} the unit is inverted regardless of the value
 * 
 * @see NormalizeUnlessZero */
public enum InvertUnlessZero implements ScalarUnaryOperator {
  FUNCTION;

  /** @param scalar
   * @return if the given scalar is an instance of {@link RealScalar} the function simplifies to
   * Scalars.isZero(scalar) ? scalar : scalar.reciprocal(); */
  @Override
  public Scalar apply(Scalar scalar) {
    if (Scalars.isZero(scalar))
      /* UnitNegate also appears in BenIsraelCohen
       * yet we do not place the function in the global scope */
      return scalar instanceof Quantity quantity //
          ? Quantity.of(quantity.value(), quantity.unit().negate())
          : scalar;
    return scalar.reciprocal();
  }

  @SuppressWarnings("unchecked")
  public static <T extends Tensor> T of(T tensor) {
    return (T) tensor.map(FUNCTION);
  }
}
