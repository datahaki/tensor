// code by jph
package ch.alpine.tensor.red;

import java.util.function.BinaryOperator;

import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Throw;
import ch.alpine.tensor.chq.ExactScalarQ;

public enum EqualsReduce implements BinaryOperator<Scalar> {
  INSTANCE;

  /** @param tensor
   * @return common {@link Scalar#zero()} of all entries in given tensor
   * with precedence of inexact precision over exact precision */
  public static Scalar zero(Tensor tensor) {
    return tensor.flatten(-1) //
        .map(Scalar.class::cast) //
        .map(Scalar::zero) //
        .reduce(INSTANCE) //
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
