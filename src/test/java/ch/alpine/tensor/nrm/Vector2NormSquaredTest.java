// code by jph
package ch.alpine.tensor.nrm;

import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.sca.AbsSquared;
import ch.alpine.tensor.sca.Chop;
import ch.alpine.tensor.usr.AssertFail;
import junit.framework.TestCase;

public class Vector2NormSquaredTest extends TestCase {
  public void testBetween() {
    Tensor v1 = Tensors.vector(1, 2, 5);
    Tensor v2 = Tensors.vector(0, -2, 10);
    Scalar n1 = Vector2NormSquared.between(v1, v2);
    Scalar n2 = Vector2Norm.between(v1, v2);
    Chop._13.requireClose(n1, AbsSquared.FUNCTION.apply(n2));
  }

  public void testEmpty() {
    AssertFail.of(() -> Vector2NormSquared.of(Tensors.empty()));
  }
}
