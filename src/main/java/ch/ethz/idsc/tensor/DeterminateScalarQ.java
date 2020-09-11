// code by jph
package ch.ethz.idsc.tensor;

import ch.ethz.idsc.tensor.qty.Quantity;

public enum DeterminateScalarQ {
  ;
  /** @param tensor
   * @return whether scalar is in exact precision or a machine number but not Infinity or NaN */
  public static boolean of(Tensor tensor) {
    if (tensor instanceof ComplexScalar) {
      ComplexScalar complexScalar = (ComplexScalar) tensor;
      return of(complexScalar.real()) //
          && of(complexScalar.imag());
    }
    if (tensor instanceof Quantity) {
      Quantity quantity = (Quantity) tensor;
      return of(quantity.value());
    }
    return MachineNumberQ.of(tensor) //
        || ExactScalarQ.of(tensor);
  }

  public static Scalar require(Scalar scalar) {
    if (of(scalar))
      return scalar;
    throw TensorRuntimeException.of(scalar);
  }
}
