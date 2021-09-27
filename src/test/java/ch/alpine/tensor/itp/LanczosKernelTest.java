// code by jph
package ch.alpine.tensor.itp;

import ch.alpine.tensor.RationalScalar;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Scalars;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.alg.Range;
import ch.alpine.tensor.mat.Tolerance;
import ch.alpine.tensor.num.Boole;
import ch.alpine.tensor.sca.Chop;
import junit.framework.TestCase;

public class LanczosKernelTest extends TestCase {
  public void testSimple() {
    LanczosKernel lanczosKernel = LanczosKernel._3;
    assertEquals(lanczosKernel.semi(), 3);
    for (Tensor _x : Range.of(-5, 5 + 1)) {
      Scalar param = (Scalar) _x;
      Scalar scalar = lanczosKernel.apply(param);
      scalar = Tolerance.CHOP.apply(scalar);
      assertEquals(Boole.of(Scalars.isZero(param)), scalar);
    }
  }

  public void testIntermediate() {
    LanczosKernel lanczosKernel = LanczosKernel._3;
    Scalar apply = lanczosKernel.apply(RationalScalar.HALF);
    Chop._14.requireClose(apply, RealScalar.of(0.6079271018540267));
  }
}
