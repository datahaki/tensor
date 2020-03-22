// code by jph
package ch.ethz.idsc.tensor;

import ch.ethz.idsc.tensor.qty.Quantity;

public enum DeterminateScalarQ {
  ;
  /** @param tensor
   * @return */
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
}
