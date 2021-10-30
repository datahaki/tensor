// code by jph
package ch.alpine.tensor.usr;

import java.util.function.BinaryOperator;

import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.sca.Clip;

public interface BivariateEvaluation extends BinaryOperator<Scalar> {
  Clip clipX();

  Clip clipY();
}
