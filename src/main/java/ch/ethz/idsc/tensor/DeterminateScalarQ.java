// code by jph
package ch.ethz.idsc.tensor;

import ch.ethz.idsc.tensor.qty.Quantity;

/** not Infinity or NaN
 * 
 * check is useful after division by a numeric value equal or close to zero */
public enum DeterminateScalarQ {
  ;
  /** @param scalar
   * @return whether scalar is in exact precision or a machine number but not Infinity or NaN */
  public static boolean of(Scalar scalar) {
    if (scalar instanceof Quantity) {
      Quantity quantity = (Quantity) scalar;
      return of(quantity.value());
    }
    if (scalar instanceof ComplexScalar) {
      ComplexScalar complexScalar = (ComplexScalar) scalar;
      return _of(complexScalar.real()) //
          && _of(complexScalar.imag());
    }
    return _of(scalar);
  }

  /** @param scalar
   * @return */
  private static boolean _of(Scalar scalar) {
    return MachineNumberQ.of(scalar) //
        || ExactScalarQ.of(scalar);
  }

  /** @param scalar
   * @return
   * @throws Exception if given scalar does not satisfy the predicate {@link DeterminateScalarQ} */
  public static Scalar require(Scalar scalar) {
    if (of(scalar))
      return scalar;
    throw TensorRuntimeException.of(scalar);
  }
}
