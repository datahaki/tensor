// code by jph
package ch.alpine.tensor.sca;

import ch.alpine.tensor.ComplexScalar;
import ch.alpine.tensor.Gaussian;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.usr.AssertFail;
import junit.framework.TestCase;

public class ArcTanhTest extends TestCase {
  public void testReal() {
    Scalar scalar = ArcTanh.of(RealScalar.of(0.5));
    assertEquals(scalar, RealScalar.of(0.5493061443340548));
  }

  public void testComplex() {
    Scalar scalar = ArcTanh.of(ComplexScalar.of(5, -9));
    // 0.0468657 - 1.48591 I
    assertEquals(scalar, ComplexScalar.of(0.04686573907359337, -1.4859071898107274));
  }

  public void testFail() {
    Scalar scalar = Gaussian.of(2, 3);
    AssertFail.of(() -> ArcTanh.FUNCTION.apply(scalar));
  }
}
