// code by jph
package ch.ethz.idsc.tensor;

import ch.ethz.idsc.tensor.qty.Quantity;

public enum DeterminateScalarQ {
  ;
  /** @param scalar
   * @return whether scalar is in exact precision or a machine number but not Infinity or NaN */
  public static boolean of(Scalar scalar) {
    if (scalar instanceof ComplexScalar) {
      ComplexScalar complexScalar = (ComplexScalar) scalar;
      return of(complexScalar.real()) //
          && of(complexScalar.imag());
    }
    if (scalar instanceof Quantity) {
      Quantity quantity = (Quantity) scalar;
      return of(quantity.value());
    }
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
