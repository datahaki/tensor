// code by jph
package ch.alpine.tensor.chq;

import java.util.Optional;

import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.alg.Flatten;
import ch.alpine.tensor.mat.Tolerance;
import ch.alpine.tensor.red.Max;
import ch.alpine.tensor.sca.Abs;
import ch.alpine.tensor.sca.Chop;

public class ApproximateEqualityQ {
  // TODO TENSOR
  public static Chop chop(Tensor tensor) {
    if (ExactTensorQ.of(tensor))
      return Chop.NONE;
    Optional<Scalar> optional = Flatten.scalars(tensor).map(Abs.FUNCTION).reduce(Max::of);
    if (optional.isEmpty())
      return Tolerance.CHOP;
    Scalar maxAbs = optional.orElseThrow();
    return Chop.below(maxAbs.number().doubleValue() * 1E-12);
  }
}
