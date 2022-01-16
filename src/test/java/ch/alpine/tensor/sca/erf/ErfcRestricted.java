// code by jph
package ch.alpine.tensor.sca.erf;

import ch.alpine.tensor.DoubleScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Scalars;
import ch.alpine.tensor.TensorRuntimeException;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.api.ScalarUnaryOperator;
import ch.alpine.tensor.num.Polynomial;
import ch.alpine.tensor.sca.Abs;

/** IMPLEMENTATION IS BASED ON THE TAYLOR SERIES
 * RESTRICTED TO A BOUNDED INTERVAL AROUND ZERO
 * 
 * the purpose is only for comparison */
/* package */ enum ErfcRestricted implements ScalarUnaryOperator {
  FUNCTION;

  private static final ScalarUnaryOperator POLYNOMIAL = Polynomial.of(Tensors.vector( //
      1, //
      -1.1283791670955126, 0, // x
      +0.3761263890318375, 0, // x^3
      -0.11283791670955126, 0, // x^5
      +0.02686617064513125, 0, // x^7
      -0.005223977625442187, 0, // x^9
      +0.0008548327023450852, 0, // x^11
      -0.00012055332981789664, 0, // x^13
      +0.000014925650358406252 // x^15
  ));

  @Override
  public Scalar apply(Scalar scalar) {
    if (Scalars.lessThan(Abs.of(scalar), DoubleScalar.of(0.7))) // error < 10^-9
      return POLYNOMIAL.apply(scalar);
    throw TensorRuntimeException.of(scalar);
  }
}
