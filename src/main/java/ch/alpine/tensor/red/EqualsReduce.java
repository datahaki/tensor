// code by jph
package ch.alpine.tensor.red;

import java.util.function.BinaryOperator;

import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Throw;
import ch.alpine.tensor.chq.ExactScalarQ;

public enum EqualsReduce implements BinaryOperator<Scalar> {
  INSTANCE;

  public static Scalar zero(Tensor tensor) {
    return tensor.flatten(-1) //
        .map(Scalar.class::cast) //
        .map(Scalar::zero) //
        .reduce(EqualsReduce.INSTANCE) //
        .orElseThrow();
  }

  @Override
  public Scalar apply(Scalar t, Scalar u) {
    if (t.equals(u))
      return ExactScalarQ.of(t) //
          ? u
          : t;
    throw new Throw(t, u);
  }
}
