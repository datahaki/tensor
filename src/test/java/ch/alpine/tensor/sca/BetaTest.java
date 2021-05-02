// code by jph
package ch.alpine.tensor.sca;

import ch.alpine.tensor.RationalScalar;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.mat.HilbertMatrix;
import ch.alpine.tensor.usr.AssertFail;
import junit.framework.TestCase;

public class BetaTest extends TestCase {
  public void testExact() {
    Scalar beta = Beta.of(5, 4);
    Scalar exact = RationalScalar.of(1, 280);
    Chop._14.requireClose(beta, exact);
  }

  public void testNumeric() {
    Scalar beta = Beta.of(2.3, 3.2);
    Scalar exact = RealScalar.of(0.05402979174835722);
    Chop._14.requireClose(beta, exact);
  }

  public void testVectorFail() {
    AssertFail.of(() -> Beta.of(HilbertMatrix.of(3)));
  }
}
