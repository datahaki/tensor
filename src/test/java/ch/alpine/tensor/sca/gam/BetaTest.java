// code by jph
package ch.alpine.tensor.sca.gam;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.RationalScalar;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.mat.HilbertMatrix;
import ch.alpine.tensor.sca.Chop;
import ch.alpine.tensor.usr.AssertFail;

public class BetaTest {
  @Test
  public void testExact() {
    Scalar beta = Beta.of(5, 4);
    Scalar exact = RationalScalar.of(1, 280);
    Chop._14.requireClose(beta, exact);
  }

  @Test
  public void testNumeric() {
    Scalar beta = Beta.of(2.3, 3.2);
    Scalar exact = RealScalar.of(0.05402979174835722);
    Chop._14.requireClose(beta, exact);
  }

  @Test
  public void testVectorFail() {
    AssertFail.of(() -> Beta.of(HilbertMatrix.of(3)));
  }
}
