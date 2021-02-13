// code by jph
package ch.ethz.idsc.tensor.nrm;

import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.sca.AbsSquared;
import ch.ethz.idsc.tensor.sca.Chop;
import ch.ethz.idsc.tensor.usr.AssertFail;
import junit.framework.TestCase;

public class VectorNorm2SquaredTest extends TestCase {
  public void testBetween() {
    Tensor v1 = Tensors.vector(1, 2, 5);
    Tensor v2 = Tensors.vector(0, -2, 10);
    Scalar n1 = VectorNorm2Squared.between(v1, v2);
    Scalar n2 = VectorNorm2.between(v1, v2);
    Chop._13.requireClose(n1, AbsSquared.FUNCTION.apply(n2));
  }
  // public void testMatrix() {
  // Tensor matrix = Tensors.matrix(new Number[][] { { 1, 2, 3 }, { 9, -3, 0 } });
  // Scalar nrm = VectorNorm2Squared.ofMatrix(matrix);
  // assertEquals(nrm, VectorNorm2Squared.ofMatrix(Transpose.of(matrix)));
  // // Mathematica: 9.493062577750756
  // Scalar s = DoubleScalar.of(9.493062577750756);
  // Chop._14.requireClose(nrm, s.multiply(s));
  // }

  public void testEmpty() {
    AssertFail.of(() -> VectorNorm2Squared.of(Tensors.empty()));
  }
}
