// code by jph
package ch.alpine.tensor.usr;

import ch.alpine.tensor.api.ScalarBinaryOperator;
import ch.alpine.tensor.sca.Clip;

/* package */ interface BivariateEvaluation extends ScalarBinaryOperator {
  Clip clipX();

  Clip clipY();
}
