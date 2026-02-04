// code by jph
package ch.alpine.tensor.sca.var;

import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.api.ScalarUnaryOperator;
import ch.alpine.tensor.sca.Clips;
import ch.alpine.tensor.sca.Sign;

/** inspired by
 * <a href="https://reference.wolfram.com/language/ref/VariogramModel.html">VariogramModel</a> */
public record BoundedLinear(Scalar a, Scalar b) implements ScalarUnaryOperator {
  @Override
  public Scalar apply(Scalar r) {
    Sign.requirePositiveOrZero(r);
    return Clips.positive(b).rescale(r).multiply(a);
  }
}
