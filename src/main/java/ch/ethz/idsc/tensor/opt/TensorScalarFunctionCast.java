// code by jph
package ch.ethz.idsc.tensor.opt;

import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.sca.ScalarUnaryOperator;

/* package */ class TensorScalarFunctionCast implements ScalarUnaryOperator {
  private final ScalarTensorFunction scalarTensorFunction;

  public TensorScalarFunctionCast(ScalarTensorFunction scalarTensorFunction) {
    this.scalarTensorFunction = scalarTensorFunction;
  }

  @Override
  public Scalar apply(Scalar scalar) {
    return (Scalar) scalarTensorFunction.apply(scalar);
  }
}